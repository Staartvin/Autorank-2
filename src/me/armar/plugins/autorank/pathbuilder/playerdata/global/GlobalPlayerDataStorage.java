package me.armar.plugins.autorank.pathbuilder.playerdata.global;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataManager;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataStorage;
import me.armar.plugins.autorank.storage.mysql.SQLConnection;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.warningmanager.WarningManager;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GlobalPlayerDataStorage implements PlayerDataStorage {

    static String TABLE_PLAYERDATA_STORAGE_COMPLETED_PATHS = "playerdata_completed_paths";
    static String TABLE_SERVER_REGISTER = "servers";

    private Autorank plugin;
    private SQLConnection connection;

    private PlayerDataCache playerDataCache = new PlayerDataCache();

    public GlobalPlayerDataStorage(Autorank instance) {
        this.plugin = instance;


        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Load the database connection.
            try {
                boolean loadedDatabase = this.loadDatabaseConnection().get();

                if (loadedDatabase) {
                    this.loadServerRegister();
                    this.loadPlayerData();
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        // Periodically run update to load data in cache.
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (getConnection() != null && !getConnection().isClosed()) {
                this.updateCacheFromRemote();
            }
        }, AutorankTools.TICKS_PER_SECOND * 30, AutorankTools.TICKS_PER_SECOND * 30);
    }

    @NotNull
    private CompletableFuture<Boolean> loadDatabaseConnection() {

        return CompletableFuture.supplyAsync(() -> {
            SettingsConfig configHandler = plugin.getSettingsConfig();

            if (!configHandler.useMySQL()) {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Can't load MySQL database, as " +
                        "you've disabled the MySQL server.");
                return false;
            }

            String hostname = configHandler.getMySQLCredentials(SettingsConfig.MySQLCredentials.HOSTNAME);
            String username = configHandler.getMySQLCredentials(SettingsConfig.MySQLCredentials.USERNAME);
            String password = configHandler.getMySQLCredentials(SettingsConfig.MySQLCredentials.PASSWORD);
            String database = configHandler.getMySQLCredentials(SettingsConfig.MySQLCredentials.DATABASE);

            connection = new SQLConnection(hostname, username, password, database);

            if (connection.connect()) {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully attached to your " +
                        "MySQL database to retrieve playerdata");
                return true;
            } else {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not attach to your " +
                        "MySQL database to retrieve playerdata");
                plugin.getWarningManager().registerWarning("Could not attach to your " +
                        "MySQL database to retrieve playerdata", WarningManager.HIGH_PRIORITY_WARNING);
                return false;
            }
        });
    }

    private SQLConnection getConnection() {
        return connection;
    }

    private void loadServerRegister() {
        // Load the table that has all the server names registered in it.

        // Create table if it does not exist.
        getConnection().execute("CREATE TABLE IF NOT EXISTS " + TABLE_SERVER_REGISTER +
                "(server_name varchar(36) NOT NULL, hostname varchar(55) NOT NULL, last_updated timestamp DEFAULT " +
                "CURRENT_TIMESTAMP, UNIQUE(server_name, hostname))");

        getConnection().execute("INSERT INTO " + TABLE_SERVER_REGISTER + " VALUES ('" +
                plugin.getSettingsConfig().getMySQLCredentials(SettingsConfig.MySQLCredentials.SERVER_NAME) + "', " +
                "'" + getHostname() + "', " +
                "CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE last_updated=CURRENT_TIMESTAMP");

        plugin.debugMessage("Loaded online server register.");

    }

    private String getHostname() {
        return plugin.getServer().getIp() + ":" + plugin.getServer().getPort();
    }

    private void loadPlayerData() {
        // Load the table that stores all the player data.

        // Create table if it does not exist.
        getConnection().execute("CREATE TABLE IF NOT EXISTS " + TABLE_PLAYERDATA_STORAGE_COMPLETED_PATHS +
                "(server_name varchar(36) NOT NULL, uuid varchar(36) NOT NULL, completed_path varchar(36) NOT NULL, " +
                "UNIQUE(server_name, uuid, completed_path))");

        plugin.debugMessage("Loaded online playerdata storage.");

        this.updateCacheFromRemote();

    }

    private void updateCacheFromRemote() {
        ResultSet resultSet =
                getConnection().executeQuery("SELECT * FROM " + TABLE_PLAYERDATA_STORAGE_COMPLETED_PATHS + " ORDER BY" +
                        " uuid");

        while (true) {
            try {
                if (!resultSet.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String serverName = null, completedPath = null;
            UUID uuid = null;

            try {
                serverName = resultSet.getString("server_name");
                uuid = UUID.fromString(resultSet.getString("uuid"));
                completedPath = resultSet.getString("completed_path");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (uuid == null || serverName == null || completedPath == null) {
                continue;
            }

            CachedPlayerData cachedPlayerData = this.playerDataCache.getCachedPlayerData(uuid);

            cachedPlayerData.addCachedEntry(completedPath, serverName);
        }

        getConnection().close(null, null, resultSet);
    }

    @Override
    public Collection<Integer> getCompletedRequirements(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasCompletedRequirement(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addCompletedRequirement(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCompletedRequirements(UUID uuid, String pathName, Collection<Integer> requirements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Integer> getCompletedRequirementsWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addCompletedRequirementWithMissingResults(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeCompletedRequirementWithMissingResults(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasCompletedRequirementWithMissingResults(UUID uuid, String pathName, int requirementId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Integer> getCompletedPrerequisites(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasCompletedPrerequisite(UUID uuid, String pathName, int prerequisiteId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addCompletedPrerequisite(UUID uuid, String pathName, int prerequisiteId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCompletedPrerequisites(UUID uuid, String pathName, Collection<Integer> prerequisites) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getChosenPathsWithMissingResults(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addChosenPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeChosenPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasChosenPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getActivePaths(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasActivePath(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addActivePath(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeActivePath(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setActivePaths(UUID uuid, Collection<String> paths) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getCompletedPaths(UUID uuid) {
        return null;
    }

    /**
     * Check whether a player has completed a given path globally.
     *
     * @param uuid          UUID of the player
     * @param completedPath Path to check
     * @return true if the path has been completed, false otherwise.
     */
    public boolean hasCompletedPath(@NonNull UUID uuid, @NonNull String completedPath) {
        return this.playerDataCache.getCachedPlayerData(uuid).getCachedEntriesByPath(completedPath).size() > 0;
    }

    /**
     * Add a completed path to the storage.
     *
     * @param uuid          UUID of the player that completed the path
     * @param completedPath Name of the path that was completed.
     */
    public void addCompletedPath(@NonNull UUID uuid, @NonNull String completedPath) {
        Validate.notNull(uuid);
        Validate.notNull(completedPath);

        String serverName = plugin.getSettingsConfig().getMySQLCredentials(SettingsConfig.MySQLCredentials.SERVER_NAME);

        // Add item to indicate that a path has been completed.
        getConnection().execute("INSERT INTO " + TABLE_PLAYERDATA_STORAGE_COMPLETED_PATHS + " VALUES ('" + serverName + "', '" + uuid.toString() + "', '" + completedPath + "') ON " +
                "DUPLICATE KEY UPDATE uuid=uuid;");

        // Update the last time a server has updated values.
        getConnection().execute("UPDATE " + TABLE_SERVER_REGISTER + " SET last_updated = CURRENT_TIMESTAMP " +
                "WHERE server_name = '" + serverName + "';");

    }

    @Override
    public void removeCompletedPath(UUID uuid, String pathName) {
        String serverName = plugin.getSettingsConfig().getMySQLCredentials(SettingsConfig.MySQLCredentials.SERVER_NAME);

        // Remove path that matches the name of the path.
        getConnection().execute("DELETE FROM " + TABLE_PLAYERDATA_STORAGE_COMPLETED_PATHS + " WHERE uuid='"
                + uuid.toString() + "' AND server_name='" + serverName + "' AND completed_path='" + pathName + "';");
    }

    @Override
    public void setCompletedPaths(UUID uuid, Collection<String> paths) {
        String serverName = plugin.getSettingsConfig().getMySQLCredentials(SettingsConfig.MySQLCredentials.SERVER_NAME);

        // First remove all paths that are currently stored there.
        getConnection().execute("DELETE FROM " + TABLE_PLAYERDATA_STORAGE_COMPLETED_PATHS + " WHERE uuid='"
                + uuid.toString() + "' AND server_name='" + serverName + "';");

        // For each path, add them again.
        paths.forEach(completedPath -> this.addCompletedPath(uuid, completedPath));
    }

    @Override
    public int getTimesCompletedPath(UUID uuid, String pathName) {
        return hasCompletedPath(uuid, pathName) ? 1 : 0;
    }

    @Override
    public Collection<String> getCompletedPathsWithMissingResults(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addCompletedPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeCompletedPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasCompletedPathWithMissingResults(UUID uuid, String pathName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasLeaderboardExemption(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLeaderboardExemption(UUID uuid, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasAutoCheckingExemption(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAutoCheckingExemption(UUID uuid, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasTimeAdditionExemption(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimeAdditionExemption(UUID uuid, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerDataManager.PlayerDataStorageType getDataStorageType() {
        return PlayerDataManager.PlayerDataStorageType.GLOBAL;
    }

}
