package me.armar.plugins.autorank.config;

import org.bukkit.configuration.file.FileConfiguration;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency;

/**
 * This class is used to access the properties of the Settings.yml file.
 * All global configurations options can be accessed via this clas..
 * 
 * @author Staartvin
 * 
 */
public class SettingsConfig {

	/**
	 * The different type of credentials that is used to connect to a MySQL database.
	 */
	public enum MySQLCredentials {
		DATABASE, HOSTNAME, PASSWORD, TABLE, USERNAME
	}

	private SimpleYamlConfiguration config;

	private String fileName = "Settings.yml";

	private final Autorank plugin;

	public SettingsConfig(final Autorank instance) {
		plugin = instance;
	}

	/**
	 * Create a new Settings.yml file.
	 */
	public void createNewFile() {
		config = new SimpleYamlConfiguration(plugin, fileName, fileName);

		plugin.getLogger().info("Settings file loaded (Settings.yml)");
	}

	/**
	 * Get the Settings.yml file.
	 * @return
	 */
	public FileConfiguration getConfig() {
		if (config != null) {
			return (FileConfiguration) config;
		}

		return null;
	}

	/**
	 * Reload the Settings.yml file.
	 */
	public void reloadConfig() {
		if (config != null) {
			config.reloadFile();
		}
	}

	/**
	 * Save the Settings.yml file.
	 */
	public void saveConfig() {
		if (config == null) {
			return;
		}

		config.saveFile();
	}

	/**
	 * Check whether Autorank should display only the commands that a player is able to perform when showing the help pages.
	 * @return true if Autorank should take permissions into consideration, false otherwise.
	 */
	public boolean doBaseHelpPageOnPermissions() {
		return this.getConfig().getBoolean("show help command based on permission", false);
	}

	/**
	 * Check whether Autorank should validate whether there is a new version of Autorank available online.
	 * @return true if Autorank should, false otherwise.
	 */
	public boolean doCheckForNewerVersion() {
		return this.getConfig().getBoolean("auto-updater.check-for-new-versions", true);
	}

	/**
	 * Get the layout of the /ar check command. 
	 * @return the layout of the /ar check command.
	 */
	public String getCheckCommandLayout() {
		return this.getConfig().getString("check command layout",
				"&p has played for &time and is on path '&path'. Requirements to be ranked up: &reqs");
	}

	/**
	 * Get the time (in minutes) to let Autorank wait before checking players again.
	 * @return interval time in minutes
	 */
	public int getIntervalTime() {
		return this.getConfig().getInt("interval check", 5);
	}

	/**
	 * Get the layout of the leaderboards.
	 * @return the layout of the /ar leaderboard command
	 */
	public String getLeaderboardLayout() {
		return this.getConfig().getString("leaderboard layout",
				"&6&r | &b&p - &7&d day(s), &h hour(s) and &m minute(s).");
	}

	/**
	 * Get the number of players Autorank should display on any leaderboard.
	 * @return number of players to show. By default 10.
	 */
	public int getLeaderboardLength() {
		return this.getConfig().getInt("leaderboard length", 10);
	}

	/**
	 * Get the value of a specific MySQL credential.
	 * @param option Type of credential
	 * @return the value for the given MySQL credential.
	 */
	public String getMySQLCredentials(final MySQLCredentials option) {
		switch (option) {
			case HOSTNAME:
				return this.getConfig().getString("sql.hostname");
			case USERNAME:
				return this.getConfig().getString("sql.username");
			case PASSWORD:
				return this.getConfig().getString("sql.password");
			case DATABASE:
				return this.getConfig().getString("sql.database");
			case TABLE:
				return this.getConfig().getString("sql.table");
			default:
				return null;
		}
	}

	/**
	 * Check whether Autorank should disable automatically checking whether a player has completed a path.
	 * @return true if Autorank should disable it, false otherwise.
	 */
	public boolean isAutomaticPathDisabled() {
		return this.getConfig().getBoolean("disable automatic path checking", false);
	}

	/**
	 * Check whether Autorank should only use the primary permission group that Vault returns instead of using all the permission groups of a player.
	 * @return true if Autorank should, false otherwise.
	 */
	public boolean onlyUsePrimaryGroupVault() {
		return this.getConfig().getBoolean("use primary group for vault", true);
	}

	/**
	 * Check whether Autorank should broadcast a message to all online players when a {@link TimeType} file is reset.
	 * @return true if Autorank should notice all players. False otherwise.
	 */
	public boolean shouldBroadcastDataReset() {
		return this.getConfig().getBoolean("broadcast resetting of data files", true);
	}

	/**
	 * Check whether Autorank should output warnings (when an admin comes online) if there are any.
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
		return this.getConfig().getBoolean("advanced AutorankDependency output", false);
	}

	/**
	 * Check whether Autorank should take AFK into account when calculating online time of a player.
	 * If this is true, Autorank can use third party plugins to detect whether a player is AFK.
	 * See {@link me.armar.plugins.autorank.hooks.DependencyManager#isAFK(org.bukkit.entity.Player)}.
	 * 
	 * @return true when AFK integration should be used, false otherwise.
	 */
	public boolean useAFKIntegration() {
		return this.getConfig().getBoolean("afk integration", true);
	}

	/**
	 * Check whether Autorank should output debug messages to the server console.
	 */
	public boolean useDebugOutput() {
		return this.getConfig().getBoolean("use debug", false);
	}

	/**
	 * Check whether Autorank should display the global time of players instead of their local time in any of the leaderboards.
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
	 * Check whether Autorank should use partial completion. If this is set to true, Autorank will mark completed requirements when a player is on a path so
	 * that they don't have to meet all requirements at the same time. Instead, they can first complete requirement 1 and then 2, and Autorank will still consider
	 * the path as completed.
	 */
	public boolean usePartialCompletion() {
		return this.getConfig().getBoolean("use partial completion", true);
	}

	/**
	 * Get the plugin that is used to get the local play time of player.
	 * This is only accounted for the local time. The global time is still
	 * calculated by Autorank.
	 * 
	 * @return {@link me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency}
	 *         AutorankDependency that is used
	 */
	public AutorankDependency useTimeOf() {

		final String timePlugin = this.getConfig().getString("use time of", "Autorank");

		if (timePlugin.equalsIgnoreCase("Stats"))
			return AutorankDependency.STATS;
		else if (timePlugin.equalsIgnoreCase("OnTime"))
			return AutorankDependency.ONTIME;
		else if (timePlugin.equals("Statz"))
			return AutorankDependency.STATZ;
		else
			return AutorankDependency.AUTORANK;
	}

}
