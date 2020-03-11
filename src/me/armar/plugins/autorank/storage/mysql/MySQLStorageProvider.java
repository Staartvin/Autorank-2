package me.armar.plugins.autorank.storage.mysql;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;

/**
 * This class represents a storage provider in terms of MySQL. It can be used alongside other MySQL connections. This
 * storage provider uses a cache manager to control for the time-consuming tasks of connecting and querying the MySQL
 * database.
 */
public class MySQLStorageProvider extends PlayTimeStorageProvider {

    // How many minutes can a cached entry be cached before it is considered to be expired.
    public static int CACHE_EXPIRY_TIME = 2;
    // Thread pool for saving and retrieving storage.
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    // Store table names for different time types
    private Map<TimeType, String> tableNames = new HashMap<>();
    // Use library to handle connections to MySQL database.
    private SQLConnection mysqlLibrary;
    // Use a cache manager to store the cached values.
    private CacheManager cacheManager = new CacheManager();
    private boolean isLoaded = false;

    public MySQLStorageProvider(Autorank instance) {
        super(instance);

        CACHE_EXPIRY_TIME = plugin.getSettingsConfig().getIntervalTime();

        // Run task to update time in cache periodically.
        instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, () -> {

            // Find all UUIDS that are in the cache.
            Set<UUID> cachedUUIDs = cacheManager.getCachedUUIDs();

            for (UUID cachedUUID : cachedUUIDs) {
                // Loop over all time types and see if they should be updated.
                for (TimeType timeType : TimeType.values()) {
                    if (cacheManager.shouldUpdateCachedEntry(timeType, cachedUUID)) {

                        plugin.debugMessage("Refreshing cached global time of " + cachedUUID);

                        // Get their global playtime
                        int playTime = 0;
                        try {
                            playTime = getFreshPlayerTime(timeType, cachedUUID).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                        // Put the freshly acquired data in the cache.
                        cacheManager.registerCachedTime(timeType, cachedUUID, playTime);
                    }
                }
            }

        }, AutorankTools.TICKS_PER_MINUTE, (CACHE_EXPIRY_TIME * AutorankTools.TICKS_PER_MINUTE) / 2);
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.DATABASE;
    }

    @Override
    public void setPlayerTime(TimeType timeType, UUID uuid, int time) {
        // Run async to prevent load issues.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            plugin.debugMessage("Setting time (" + timeType + ") of '" + uuid.toString() + "' to " + time);

            // Check if connection is still alive
            if (mysqlLibrary.isClosed()) {
                mysqlLibrary.connect();
            }

            String tableName = tableNames.get(timeType);

            final String statement = "INSERT INTO " + tableName + " VALUES ('" + uuid.toString() + "', " + time
                    + ", CURRENT_TIMESTAMP) " + "ON DUPLICATE KEY UPDATE " + "time=" + time;

            // Update cache with new value
            cacheManager.registerCachedTime(timeType, uuid, time);

            mysqlLibrary.execute(statement);
        });
    }

    @Override
    public CompletableFuture<Integer> getPlayerTime(TimeType timeType, UUID uuid) {

        return CompletableFuture.supplyAsync(() -> {
            // If we have a cached time value, use that instead of quering the database.
            if (cacheManager.hasCachedTime(timeType, uuid)) {
                plugin.debugMessage("Getting cached time (" + timeType + ") of '" + uuid.toString() + "'");
                return cacheManager.getCachedTime(timeType, uuid);
            }

            try {
                return getFreshPlayerTime(timeType, uuid).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return 0;
        });
    }

    @Override
    public void resetData(TimeType timeType) {
        String tableName = this.tableNames.get(timeType);

        String statement = "TRUNCATE TABLE " + tableName;

        // Run clean statement async so it won't bother main thread.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> mysqlLibrary.execute(statement));
    }

    @Override
    public void addPlayerTime(TimeType timeType, UUID uuid, int timeToAdd) {

        // Run async to prevent load issues.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            plugin.debugMessage("Adding " + timeToAdd + " minutes of (" + timeType + ") to '" + uuid.toString() + "'");

            // Check if connection is still alive
            if (mysqlLibrary.isClosed()) {
                mysqlLibrary.connect();
            }

            String tableName = tableNames.get(timeType);

            final String statement = "INSERT INTO " + tableName + " VALUES ('" + uuid.toString() + "', " + timeToAdd
                    + ", CURRENT_TIMESTAMP) " + "ON DUPLICATE KEY UPDATE " + "time=time+" + timeToAdd;

            mysqlLibrary.execute(statement);
        });
    }

    @Override
    public String getName() {
        return "MySQLStorageProvider";
    }

    @Override
    public CompletableFuture<Boolean> initialiseProvider() {

        return CompletableFuture.supplyAsync(() -> {
            // Load table names
            loadTableNames();

            // Load settings from settings file and initialize MySQL connection .
            try {
                if (!loadMySQLVariables().get()) {
                    return false;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return false;
            }

            // Check whether loading was successful.
            if (mysqlLibrary == null) {
                return false;
            }

            // Load and create tables
            createTables();

            isLoaded = true;
            return true;
        });
    }

    @Override
    public int purgeOldEntries(int threshold) {
        // TODO: implement this
        return 0;
    }

    @Override
    public CompletableFuture<Integer> getNumberOfStoredPlayers(TimeType timeType) {

        return CompletableFuture.supplyAsync(() -> {
            String tableName = tableNames.get(timeType);

            String statement = "SELECT COUNT(uuid) FROM " + tableName;

            ResultSet rs = mysqlLibrary.executeQuery(statement);

            if (rs == null)
                return 0;

            try {
                if (rs.next()) {
                    return rs.getInt(1);
                }

            } catch (final SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("VendorError: " + e.getErrorCode());
            }

            return 0;
        });
    }

    @Override
    public List<UUID> getStoredPlayers(TimeType timeType) {
        List<UUID> uuids = new ArrayList<>();

        String tableName = tableNames.get(timeType);

        String statement = "SELECT uuid FROM " + tableName;

        ResultSet rs = mysqlLibrary.executeQuery(statement);

        if (rs == null)
            return uuids;

        try {

            // Loop over all rows to get all UUIDs.
            while (rs.next()) {
                String uuidString = rs.getString("uuid");

                uuids.add(UUID.fromString(uuidString));
            }

        } catch (final SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }

        return uuids;
    }

    @Override
    public void saveData() {
        // There is no in-memory data (apart from the cache). Hence, there is no need to save anything as it is
        // already stored in the database.
    }

    @Override
    public boolean canImportData() {
        return false;
    }

    @Override
    public void importData() {
        // TODO: implement data in some way
    }

    @Override
    public boolean canBackupData() {
        return true;
    }

    @Override
    public boolean backupData() {
        List<String> statements = new ArrayList<>();

        DateFormat df = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");

        for (Map.Entry<TimeType, String> entry : tableNames.entrySet()) {
            String tableName = entry.getValue();

            String backupTableName = tableName + "_backup_" + df.format(new Date());

            statements.add(String.format("CREATE TABLE `%1$s` LIKE `%2$s`;", backupTableName, tableName));
            statements.add(String.format("INSERT INTO `%1$s` SELECT * FROM `%2$s`;",
                    backupTableName, tableName));
        }

        mysqlLibrary.executeQueries(statements);

        return true;
    }

    @Override
    public int clearBackupsBeforeDate(LocalDate date) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

        List<String> tablesToDelete = new ArrayList<>();

        try (ResultSet resultSet =
                     mysqlLibrary.executeQuery("SHOW TABLES;")) {

            while (resultSet != null && resultSet.next()) {
                // Read all tables and delete the ones that are not needed.

                String tableName = resultSet.getString(1);

                // Check what the date of the file is.
                String fileDateString = tableName.replaceAll("[^\\d]", "");

                Date fileDate = null;

                try {
                    fileDate = df.parse(fileDateString);
                } catch (ParseException e) {
                    // Ignore error.
                }

                // Ignore file if date could not be parsed
                if (fileDate == null) {
                    continue;
                }

                if (fileDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(date)) {
                    // This file is from before the date, so delete it.
                    tablesToDelete.add(tableName);
                }
            }

            // Now remove all tables that should be deleted.
            tablesToDelete.forEach(tableName -> {
                mysqlLibrary.execute("DROP TABLE `" + tableName + "`;");
            });


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tablesToDelete.size();
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    private void loadTableNames() {
        String prefix = plugin.getSettingsConfig().getMySQLSetting(SettingsConfig.MySQLSettings.TABLE_PREFIX);

        tableNames.put(TimeType.TOTAL_TIME, prefix + "totalTime");
        tableNames.put(TimeType.DAILY_TIME, prefix + "dailyTime");
        tableNames.put(TimeType.WEEKLY_TIME, prefix + "weeklyTime");
        tableNames.put(TimeType.MONTHLY_TIME, prefix + "monthlyTime");
    }

    /**
     * Grab the credentials defined in the Setting config and initialise connection to MySQL database.
     */
    private CompletableFuture<Boolean> loadMySQLVariables() {

        return CompletableFuture.supplyAsync(() -> {
            final SettingsConfig configHandler = plugin.getSettingsConfig();

            if (!configHandler.useMySQL()) {
                plugin.getLogger().warning("Autorank is trying to register a MySQL storage provider, but MySQL is " +
                        "disabled in the settings file!");
                return false;
            }

            mysqlLibrary = SQLConnection.getInstance(configHandler);

            if (!mysqlLibrary.connect()) {
                mysqlLibrary = null;
                plugin.getLogger().severe("Could not connect to MySQL!");
                plugin.debugMessage(ChatColor.RED + "Could not connect to MySQL!");
                return false;
            } else {
                plugin.debugMessage(ChatColor.RED + "Successfully established connection to MySQL");
                return true;
            }
        });
    }

    /**
     * Initialise the tables for the MySQL database.
     */
    private void createTables() {
        // Check if connection is still alive
        if (mysqlLibrary.isClosed()) {
            mysqlLibrary.connect();
        }

        // Create tables if they do not exist.
        for (Map.Entry<TimeType, String> entry : this.tableNames.entrySet()) {
            String statement = "CREATE TABLE IF NOT EXISTS " + entry.getValue() + " (uuid VARCHAR(40) not NULL, "
                    + " time INTEGER not NULL, " + " modified TIMESTAMP not NULL, " + " PRIMARY KEY ( uuid ))";

            // Run async to prevent load issues.
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> mysqlLibrary.execute(statement));
        }
    }

    /**
     * Get fresh time from the database for a player. This will always gather the most recent data from the MySQL
     * database and does not use caches.
     *
     * @param timeType Type of time to get
     * @param uuid     UUID of the player
     * @return value of time for a player
     */
    private CompletableFuture<Integer> getFreshPlayerTime(TimeType timeType, UUID uuid) {

        return CompletableFuture.supplyAsync(() -> {
            // Check if connection is still alive
            if (mysqlLibrary.isClosed()) {
                mysqlLibrary.connect();
            }

            String tableName = this.tableNames.get(timeType);

            int time = 0;

            final String statement = "SELECT * FROM " + tableName + " WHERE uuid='" + uuid.toString() + "'";

            try (ResultSet rs = this.mysqlLibrary.executeQuery(statement)) {

                if (rs == null)
                    return time;

                if (rs.next()) {
                    time = rs.getInt(2);
                    rs.close();
                }

            } catch (SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("VendorError: " + e.getErrorCode());
            }

            // Cache value so we don't grab it again.
            cacheManager.registerCachedTime(timeType, uuid, time);

            plugin.debugMessage("("
                    + (Thread.currentThread().getName().contains("Server thread") ? "not async" : "async") +
                    ") Obtained fresh global time (" + timeType + ") of '" + uuid.toString() + "'" +
                    " with value " + time);

            return time;
        });
    }

    /**
     * Disconnect from database manually.
     */
    private void disconnectDatabase() {
        executor.shutdown();

        try {
            plugin.debugMessage(ChatColor.RED + "Awaiting termination of MySQL thread...");
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            plugin.getLogger().warning("Failed to await termination of thread pool. Interrupted.");
        }

        if (mysqlLibrary != null) {
            mysqlLibrary.closeConnection();
        }
    }
}
