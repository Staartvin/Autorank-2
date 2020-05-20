package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;

/**
 * This class is used to access the properties of the Settings.yml file. All
 * global configurations options can be accessed via this clas..
 *
 * @author Staartvin
 */
public class SettingsConfig extends AbstractConfig {

    /**
     * Get the value of a specific MySQL setting.
     *
     * @param option Type of setting
     * @return the value for the given MySQL setting.
     */
    public String getMySQLSetting(final MySQLSettings option) {
        switch (option) {
            case HOSTNAME:
                return this.getConfig().getString("sql.hostname");
            case USERNAME:
                return this.getConfig().getString("sql.username");
            case PASSWORD:
                return this.getConfig().getString("sql.password");
            case DATABASE:
                return this.getConfig().getString("sql.database");
            case TABLE_PREFIX:
                return this.getConfig().getString("sql.table prefix");
            case SERVER_NAME:
                return this.getConfig().getString("sql.server name", "")
                        .replace("%ip%", getPlugin().getServer().getIp())
                        .replace("%port%", getPlugin().getServer().getPort() + "")
                        .replace("%name%", getPlugin().getServer().getName());
            case USESSL:
            	return this.getConfig().getString("sql.usessl");
            default:
                throw new IllegalArgumentException(option + " is not a valid MySQL settings option");
        }
    }

    public SettingsConfig(final Autorank instance) {
        this.setFileName("Settings.yml");
        this.setPlugin(instance);
    }

    @Override
    public void reloadConfig() {
        this.loadConfig();
    }

    /**
     * Check whether Autorank should display only the commands that a player is
     * able to perform when showing the help pages.
     *
     * @return true if Autorank should take permissions into consideration,
     * false otherwise.
     */
    public boolean doBaseHelpPageOnPermissions() {
        return this.getConfig().getBoolean("show help command based on permission", false);
    }

    /**
     * Check whether Autorank should validate whether there is a new version of
     * Autorank available online.
     *
     * @return true if Autorank should, false otherwise.
     */
    public boolean doCheckForNewerVersion() {
        return this.getConfig().getBoolean("auto-updater.check-for-new-versions", true);
    }

    /**
     * Get the layout of the /ar check command.
     *
     * @return the layout of the /ar check command.
     */
    public String getCheckCommandLayout() {
        return this.getConfig().getString("check command layout",
                "&p has played for &time and is on path '&path'. Requirements to be ranked up: &reqs");
    }

    /**
     * Get the time (in minutes) to let Autorank wait before checking players
     * again.
     *
     * @return interval time in minutes
     */
    public int getIntervalTime() {
        return this.getConfig().getInt("interval check", 5);
    }

    /**
     * Get the layout of the leaderboards.
     *
     * @return the layout of the /ar leaderboard command
     */
    public String getLeaderboardLayout() {
        return this.getConfig().getString("leaderboard layout",
                "&6&r | &b&p - &7&d day(s), &h hour(s) and &m minute(s).");
    }

    /**
     * Get the number of players Autorank should display on any leaderboard.
     *
     * @return number of players to show. By default 10.
     */
    public int getLeaderboardLength() {
        return this.getConfig().getInt("leaderboard length", 10);
    }

    /**
     * Check whether Autorank should broadcast a message to all online players
     * when a {@link me.armar.plugins.autorank.storage.TimeType} file is reset.
     *
     * @return true if Autorank should notice all players. False otherwise.
     */
    public boolean shouldBroadcastDataReset() {
        return this.getConfig().getBoolean("broadcast resetting of data files", true);
    }

    /**
     * Check whether Autorank should disable automatically checking whether a
     * player has completed a path.
     *
     * @return true if Autorank should disable it, false otherwise.
     */
    public boolean isAutomaticPathDisabled() {
        return this.getConfig().getBoolean("disable automatic path checking", false);
    }

    /**
     * Check whether Autorank should only use the primary permission group that
     * Vault returns instead of using all the permission groups of a player.
     *
     * @return true if Autorank should, false otherwise.
     */
    public boolean onlyUsePrimaryGroupVault() {
        return this.getConfig().getBoolean("use primary group for vault", true);
    }

    /**
     * The different types of settings that are used to connect to a MySQL
     * database.
     */
    public enum MySQLSettings {
        DATABASE, HOSTNAME, PASSWORD, SERVER_NAME, TABLE_PREFIX, USERNAME, USESSL
    }

    /**
     * Check whether Autorank should output warnings (when an admin comes
     * online) if there are any.
     *
     * @return true if Autorank should output warnings, false otherwise.
     */
    public boolean showWarnings() {
        return this.getConfig().getBoolean("show warnings", true);
    }

    /**
     * Check whether Autorank should log detailed information about <br>
     * the found third party plugins.
     *
     * @return true if Autorank should, false otherwise.
     */
    public boolean useAdvancedDependencyLogs() {
        return this.getConfig().getBoolean("advanced dependency output", false);
    }

    /**
     * Check whether Autorank should take AFK into account when calculating
     * online time of a player. If this is true, Autorank can use third party
     * plugins to detect whether a player is AFK. See
     * {@link me.armar.plugins.autorank.hooks.DependencyManager#isAFK(org.bukkit.entity.Player)}
     * .
     *
     * @return true when AFK integration should be used, false otherwise.
     */
    public boolean useAFKIntegration() {
        return this.getConfig().getBoolean("afk integration", true);
    }

    /**
     * Check whether Autorank should output debug messages to the server
     * console.
     */
    public boolean useDebugOutput() {
        return this.getConfig().getBoolean("use debug", false);
    }

    /**
     * Check whether Autorank should display the global time of players instead
     * of their local time in any of the leaderboards.
     *
     * @return true if Autorank should, false otherwise.
     */
    public boolean useGlobalTimeInLeaderboard() {
        return this.getConfig().getBoolean("use global time in leaderboard", false);
    }

    /**
     * Check whether Autorank should use MySQL to store global times of players.
     */
    public boolean useMySQL() {
        return this.getConfig().getBoolean("sql.enabled");
    }

    /**
     * Check whether Autorank should automatically remove old storage from its database.
     *
     * @return true if it should, false otherwise.
     */
    public boolean shouldRemoveOldEntries() {
        return this.getConfig().getBoolean("automatically archive old data", true);
    }

    /**
     * Get the primary storage provider.
     *
     * @return string in config representing the primary storage provider.
     */
    public String getPrimaryStorageProvider() {
        return this.getConfig().getString("primary storage provider", "flatfile");
    }

    /**
     * Get the number of days after which Autorank can remove an old backup.
     *
     * @return number of days from when we can remove old data.
     */
    public int getBackupRemovalTime() {
        return this.getConfig().getInt("automatically remove backups if older than", 14);
    }

}
