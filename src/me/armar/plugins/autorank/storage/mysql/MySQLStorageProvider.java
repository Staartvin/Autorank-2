package me.armar.plugins.autorank.storage.mysql;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import org.bukkit.ChatColor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * This class represents a storage provider in terms of MySQL. It can be used alongside other MySQL connections. This
 * storage provider uses a cache manager to control for the time-consuming tasks of connecting and querying the MySQL
 * database.
 */
public class MySQLStorageProvider extends StorageProvider {

    // Thread pool for saving and retrieving storage.
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    // Variables to connect to database.
    private String hostname, username, password, database;

    // Store table names for different time types
    private Map<TimeType, String> tableNames = new HashMap<>();

    // Use library to handle connections to MySQL database.
    private SQLDataStorage mysqlLibrary;

    // Use a cache manager to store the cached values.
    private CacheManager cacheManager = new CacheManager();

    // TODO implement MySQL provider
    public MySQLStorageProvider(Autorank instance) {
        super(instance);

        // Initialise provider to make it ready for use.
        if (!this.initialiseProvider()) {
            plugin.debugMessage("There was an error loading storage provider '" + getName() + "'.");
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.DATABASE;
    }

    @Override
    public void setPlayerTime(TimeType timeType, UUID uuid, int time) {

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

        // Run async to prevent load issues.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> mysqlLibrary.execute(statement));


    }

    @Override
    public int getPlayerTime(TimeType timeType, UUID uuid) {

        // If we have a cached time value, use that instead of quering the database.
        if (cacheManager.hasCachedTime(timeType, uuid)) {
            plugin.debugMessage("Getting cached time (" + timeType + ") of '" + uuid.toString() + "'");
            return cacheManager.getCachedTime(timeType, uuid);
        }

        plugin.debugMessage("Obtaining fresh time (" + timeType + ") of '" + uuid.toString() + "'");

        return getFreshPlayerTime(timeType, uuid);
    }

    @Override
    public void resetData(TimeType timeType) {

        String tableName = this.tableNames.get(timeType);

        String statement = "TRUNCATE TABLE " + tableName;

        // Run clean statement async so it won't bother main thread.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> mysqlLibrary.execute(statement));

        return;
    }

    @Override
    public void addPlayerTime(TimeType timeType, UUID uuid, int timeToAdd) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            int currentTimeValue = getFreshPlayerTime(timeType, uuid);

            setPlayerTime(timeType, uuid, currentTimeValue + timeToAdd);
        });
    }

    @Override
    public String getName() {
        return "MySQLStorageProvider";
    }

    @Override
    public boolean initialiseProvider() {

        // Load table names
        loadTableNames();

        // Load settings from settings file.
        loadMySQLVariables();

        // Check whether loading was successful.
        if (mysqlLibrary == null) {
            return false;
        }

        // Load and create tables
        createTables();

        return true;
    }

    @Override
    public int purgeOldEntries(int threshold) {
        // TODO: implement this
        return 0;
    }

    @Override
    public int getNumberOfStoredPlayers(TimeType timeType) {

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
                UUID uuid = UUID.fromString(uuidString);

                uuids.add(uuid);
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

    private void loadTableNames() {
        tableNames.put(TimeType.TOTAL_TIME, "totalTime");
        tableNames.put(TimeType.DAILY_TIME, "dailyTime");
        tableNames.put(TimeType.WEEKLY_TIME, "weeklyTime");
        tableNames.put(TimeType.MONTHLY_TIME, "monthlyTime");
    }

    /**
     * Grab the credentials defined in the Setting config and initialise connection to MySQL database.
     */
    private void loadMySQLVariables() {

        final SettingsConfig configHandler = plugin.getSettingsConfig();


        if (!configHandler.useMySQL()) {
            plugin.getLogger().warning("Autorank is trying to register a MySQL storage provider, but MySQL is " +
                    "disabled in the settings file!");
            return;
        }

        hostname = configHandler.getMySQLCredentials(SettingsConfig.MySQLCredentials.HOSTNAME);
        username = configHandler.getMySQLCredentials(SettingsConfig.MySQLCredentials.USERNAME);
        password = configHandler.getMySQLCredentials(SettingsConfig.MySQLCredentials.PASSWORD);
        database = configHandler.getMySQLCredentials(SettingsConfig.MySQLCredentials.DATABASE);

        mysqlLibrary = new SQLDataStorage(hostname, username, password, database);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!mysqlLibrary.connect()) {
                mysqlLibrary = null;
                plugin.getLogger().severe("Could not connect to " + hostname);
                plugin.debugMessage(ChatColor.RED + "Could not connect to MYSQL!");
            } else {
                plugin.debugMessage(ChatColor.RED + "Successfully established connection to " + hostname);
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
    private int getFreshPlayerTime(TimeType timeType, UUID uuid) {
        // Check if connection is still alive
        if (mysqlLibrary.isClosed()) {
            mysqlLibrary.connect();
        }

        String tableName = this.tableNames.get(timeType);

        // Initialise new callable class
        final Callable<Integer> callable = new GrabPlayerTimeTask(mysqlLibrary, uuid, tableName);

        // Sumbit callable
        final Future<Integer> futureValue = executor.submit(callable);

        // Grab value, will block thread.
        // That's why you need to run this async.
        int value = 0;

        try {
            plugin.debugMessage("Fresh Gcheck performed "
                    + (Thread.currentThread().getName().contains("Server thread") ? "not ASYNC" : "ASYNC") + " ("
                    + Thread.currentThread().getName() + ")");
            value = futureValue.get();
        } catch (final InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Cache value so we don't grab it again.
        cacheManager.registerCachedTime(timeType, uuid, value);

        plugin.debugMessage("Obtained fresh global time (" + timeType + ") of '" + uuid.toString() + "' with value" +
                " " + value);

        return value;
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
