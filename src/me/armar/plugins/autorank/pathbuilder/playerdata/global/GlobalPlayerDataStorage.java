package me.armar.plugins.autorank.pathbuilder.playerdata.global;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.warningmanager.WarningManager;
import org.bukkit.ChatColor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GlobalPlayerDataStorage {

    static String TABLE_PLAYERDATA_STORAGE_COMPLETED_PATHS = "playerdata_completed_paths";
    static String TABLE_SERVER_REGISTER = "servers";
    private Autorank plugin;
    private SQLConnection connection;

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

    }

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
                        "MySQL dratabase to retrieve playerdata");
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
        getConnection().performUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_SERVER_REGISTER +
                "(server_name varchar(36) NOT NULL, hostname varchar(55) NOT NULL, last_updated timestamp DEFAULT " +
                "CURRENT_TIMESTAMP, UNIQUE(server_name, hostname))");

        getConnection().performUpdate("INSERT INTO " + TABLE_SERVER_REGISTER + " VALUES ('" +
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
        getConnection().performUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_PLAYERDATA_STORAGE_COMPLETED_PATHS +
                "(server_name varchar(36) NOT NULL, uuid varchar(36) NOT NULL, completed_path varchar(36) NOT NULL, " +
                "UNIQUE(server_name, uuid, completed_path))");

        plugin.debugMessage("Loaded online playerdata storage.");

    }


}
