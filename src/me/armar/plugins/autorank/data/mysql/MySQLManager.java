package me.armar.plugins.autorank.data.mysql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.config.SettingsConfig.MySQLCredentials;
import me.armar.plugins.autorank.playtimes.PlaytimeManager;

/**
 * This class keeps all incoming and outgoing connections under control. It
 * sends MySQL queries and can locate the database. MySQLManager class is
 * (hopefully) fail-prove and organised.
 * 
 * This also has a fail-safe when two queries are altering at the same time.
 * 
 * @author Staartvin
 * 
 */
public class MySQLManager {

    // Thread pool for saving and retrieving data.
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    String hostname, username, password, database, table;
    // Keeps track of when a call to the database was for this player
    private final HashMap<UUID, Long> lastChecked = new HashMap<UUID, Long>();
    // Stores the last received global time for a player
    private final HashMap<UUID, Integer> lastReceivedTime = new HashMap<UUID, Integer>();

    private SQLDataStorage mysql;
    private final Autorank plugin;

    public MySQLManager(final Autorank instance) {
        plugin = instance;

        sqlSetup();
    }

    /**
     * Disconnect from database manually.
     */
    public void disconnectDatabase() {
        executor.shutdown();
        plugin.debugMessage(ChatColor.RED + "Awaiting termination of MySQL thread...");
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            plugin.getLogger().warning("Failed to await termination of thread pool. Interrupted.");
        }

        if (mysql != null) {
            mysql.closeConnection();
        }
    }

    /**
     * Get all the times of the players in the MySQL database
     * 
     * @return A hashmap containing all uuids that are in the database, or an
     *         empty one if MySQL is disabled
     */
    public HashMap<UUID, Integer> getAllPlayersFromDatabase() {
        HashMap<UUID, Integer> times = new HashMap<>();

        if (!this.isMySQLEnabled())
            return times;

        // Check if connection is still alive
        if (mysql.isClosed()) {
            mysql.connect();
        }

        // Retrieve database time

        // Initialise new callable class
        final Callable<HashMap<UUID, Integer>> callable = new GrabAllTimesTask(mysql, table);

        // Sumbit callable
        final Future<HashMap<UUID, Integer>> futureValue = executor.submit(callable);

        try {
            plugin.debugMessage("Fresh AllCheck performed "
                    + (Thread.currentThread().getName().contains("Server thread") ? "not ASYNC" : "ASYNC") + " ("
                    + Thread.currentThread().getName() + ")");
            times = futureValue.get();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final ExecutionException e) {
            e.printStackTrace();
        }

        return times;
    }

    /**
     * Get the cached value of the global time of a player.
     * 
     * @param uuid
     *            UUID of the player
     * @return cached global time or 0 if nothing was cached.
     */
    public Integer getCachedGlobalTime(final UUID uuid) {
        if (!lastReceivedTime.containsKey(uuid)) {
            return 0;
        }

        final int cached = lastReceivedTime.get(uuid);

        // Weird cached
        if (cached <= 0) {
            return 0;
        }

        return cached;
    }

    /**
     * Get the name of the database Autorank uses to store global times.
     * 
     * @return string name of the database
     */
    public String getDatabaseName() {
        return database;
    }

    /**
     * Add minutes to the global time of a player.
     * 
     * @param uuid
     *            UUID of the player
     * @param timeDifference
     *            Minutes to add
     * @throws IllegalArgumentException
     *             Thrown when MySQL is not enabled.
     */
    public void addGlobalTime(final UUID uuid, final int timeDifference) throws IllegalArgumentException {
        // Check for MySQL
        if (!plugin.getMySQLManager().isMySQLEnabled()) {
            try {
                throw new SQLException("MySQL database is not enabled so you can't modify database!");
            } catch (final SQLException e) {
                e.printStackTrace();
                return;
            }
        }

        final int time = getFreshGlobalTime(uuid);

        if (time >= 0) {
            setGlobalTime(uuid, time + timeDifference);
        } else {
            setGlobalTime(uuid, timeDifference);
        }

    }

    /**
     * Get the fresh global time of the database. This will trigger a remote
     * lookup, so this method is blocking.
     * 
     * @param uuid
     *            UUID of the player
     * @return the non-cached global time of a player or 0 if nothing was found.
     */
    public int getFreshGlobalTime(final UUID uuid) {
        if (uuid == null)
            return 0;
        return plugin.getMySQLManager().getFreshDatabaseTime(uuid);
    }

    /**
     * Get the total playtime across all servers (multiple servers write to 1
     * database and get the total playtime from there).
     * 
     * @param uuid
     *            UUID to check for
     * @return Global playtime across all servers or 0 if no time was found
     */
    public int getGlobalTime(final UUID uuid) {
        if (uuid == null)
            return 0;
        return plugin.getMySQLManager().getDatabaseTime(uuid);
    }

    /**
     * Get the database time of player <br>
     * Run this ASYNC, because it will block the thread it's on.
     * <p>
     * This will return an updated value every 5 minutes. Calling it every
     * minute isn't necessary, as it will only update every 5 minutes. You'll
     * get a cached value if you try to anyway.
     * 
     * @param uuid
     *            UUID of the player
     * @return Time player has played across all servers
     */
    public int getDatabaseTime(final UUID uuid) {

        // Do not make a call to the database every time.
        // Instead, only call once every 5 minutes.
        if (!isOutOfDate(uuid)) {

            int cachedTime = getCachedGlobalTime(uuid);
            plugin.debugMessage("Obtained cached global time of '" + uuid.toString() + "' with value " + cachedTime);

            return cachedTime;
        }

        return this.getFreshDatabaseTime(uuid);
    }

    /**
     * Get the database time of a certain UUID.
     * <p>
     * This will always return the result that is currently in the database and
     * is never a cached value.
     * <p>
     * A new request will always be made to get the value, therefore this should
     * be run async.
     * 
     * @param uuid
     *            UUID of the player
     * @return Fresh value of database time for UUID.
     */
    public int getFreshDatabaseTime(final UUID uuid) {
        plugin.debugMessage("Obtaining fresh global time of '" + uuid.toString() + "'");

        // Mysql is not enabled
        if (!isMySQLEnabled())
            return 0;

        // Check if connection is still alive
        if (mysql.isClosed()) {
            mysql.connect();
        }

        // Retrieve database time

        // Initialise new callable class
        final Callable<Integer> callable = new GrabDatabaseTimeTask(mysql, uuid, table);

        // Sumbit callable
        final Future<Integer> futureValue = executor.submit(callable);

        // Grab value (will block thread, but there is no other way)
        // That's why you need to run this async.
        int value = 0;

        try {
            plugin.debugMessage("Fresh Gcheck performed "
                    + (Thread.currentThread().getName().contains("Server thread") ? "not ASYNC" : "ASYNC") + " ("
                    + Thread.currentThread().getName() + ")");
            value = futureValue.get();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final ExecutionException e) {
            e.printStackTrace();
        }

        // Store last received time and last received value
        lastChecked.put(uuid, System.currentTimeMillis());
        lastReceivedTime.put(uuid, value);

        plugin.debugMessage("Obtained fresh global time of '" + uuid.toString() + "' with value " + value);

        return value;
    }

    /**
     * Check whether MySQL is enabled in the Settings config.
     * 
     * @return true if MySQL is enabled. False otherwise.
     */
    public boolean isMySQLEnabled() {
        return mysql != null;
    }

    /**
     * Check whether the cached value of a global time of a player is out of
     * date or not. If it is, Autorank will grab a fresh time.
     * 
     * @param uuid
     *            UUID of the player
     * @return true if the cached value is outdated (or if it's not stored).
     *         False otherwise.
     */
    public boolean isOutOfDate(final UUID uuid) {
        // Checks whether the last check was five minutes ago.
        // When the last check was more than five minutes ago,
        // the database time is 'outdated'

        // Never checked
        if (!lastChecked.containsKey(uuid)) {
            return true;
        }

        final long currentTime = System.currentTimeMillis();

        final long lastCheckedTime = lastChecked.get(uuid);

        // Weird time received.
        if (lastCheckedTime <= 0) {
            return true;
        }

        // Get the difference in minutes
        if ((currentTime - lastCheckedTime) / 60000 >= PlaytimeManager.INTERVAL_MINUTES) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Refresh the global time of all players that are online on the server.
     */
    public void refreshGlobalTime() {

        // Do nothing if MySQL is not enabled
        if (!this.isMySQLEnabled())
            return;

        // Spawn an async thread that will update all the times every x minutes.
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    // Update fresh database time.
                    getFreshDatabaseTime(p.getUniqueId());
                }
            }

        }, 20, 20 * 60 * PlaytimeManager.INTERVAL_MINUTES);

    }

    /**
     * Set the global time of a player
     * 
     * @param uuid
     *            UUID of the player
     * @param time
     *            Time to change to in minutes
     */
    public boolean setGlobalTime(final UUID uuid, final int time) {

        plugin.debugMessage("Setting global time of '" + uuid.toString() + "' to " + time);

        if (!isMySQLEnabled())
            return false;

        // Check if connection is still alive
        if (mysql.isClosed()) {
            mysql.connect();
        }

        final String statement = "INSERT INTO " + table + " VALUES ('" + uuid.toString() + "', " + time
                + ", CURRENT_TIMESTAMP) " + "ON DUPLICATE KEY UPDATE " + "time=" + time;

        // Run async to prevent load issues.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                mysql.execute(statement);
            }
        });

        // Update cache records
        this.lastChecked.put(uuid, System.currentTimeMillis());
        this.lastReceivedTime.put(uuid, time);

        return true;
    }

    /**
     * Initialise the tables for the MySQL database.
     */
    public void setupTable() {
        // Check if connection is still alive
        if (mysql.isClosed()) {
            mysql.connect();
        }

        final String statement = "CREATE TABLE  IF NOT EXISTS " + table + " " + "(uuid VARCHAR(255) not NULL, "
                + " time INTEGER not NULL, " + " modified TIMESTAMP not NULL, " + " PRIMARY KEY ( uuid ))";

        // Run async to prevent load issues.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                mysql.execute(statement);
            }
        });

    }

    /**
     * Grab the credentials defined in the Setting config and initialise the
     * tables via {@linkplain #setupTable()}.
     */
    public void sqlSetup() {

        final SettingsConfig configHandler = plugin.getSettingsConfig();
        /*
         * final ConfigurationSection s = config.getConfigurationSection("sql");
         * 
         * if (s == null) { plugin.getLogger().warning(
         * "MySQL options are missing in the advancedconfig.yml!"); return; }
         */
        
        if (configHandler.useMySQL()) {

            hostname = configHandler.getMySQLCredentials(MySQLCredentials.HOSTNAME);
            username = configHandler.getMySQLCredentials(MySQLCredentials.USERNAME);
            password = configHandler.getMySQLCredentials(MySQLCredentials.PASSWORD);
            database = configHandler.getMySQLCredentials(MySQLCredentials.DATABASE);
            table = configHandler.getMySQLCredentials(MySQLCredentials.TABLE);

            mysql = new SQLDataStorage(hostname, username, password, database);

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    if (!mysql.connect()) {
                        mysql = null;
                        plugin.getLogger().severe("Could not connect to " + hostname);
                        plugin.debugMessage(ChatColor.RED + "Could not connect to MYSQL!");
                    } else {
                        plugin.debugMessage(ChatColor.RED + "Successfully established connection to " + hostname);
                    }

                    if (mysql != null) {
                        setupTable();
                    }
                }
            });
        }
    }
}
