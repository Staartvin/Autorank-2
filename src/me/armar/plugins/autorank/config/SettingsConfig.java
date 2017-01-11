package me.armar.plugins.autorank.config;

import org.bukkit.configuration.file.FileConfiguration;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;

/**
 * This class has all methods to get data from the configs.
 * <br>
 * The configurations of the Settings.yml can be reached from here.
 * 
 * @author Staartvin
 * 
 */
public class SettingsConfig {

	public enum MySQLOptions {
		DATABASE, HOSTNAME, PASSWORD, TABLE, USERNAME
	}

	private SimpleYamlConfiguration config;

	private String fileName = "Settings.yml";

	private final Autorank plugin;

	public SettingsConfig(final Autorank instance) {
		plugin = instance;
	}

	public void createNewFile() {
		config = new SimpleYamlConfiguration(plugin, fileName, fileName);

		plugin.getLogger().info("Settings file loaded (Settings.yml)");
	}

	public FileConfiguration getConfig() {
		if (config != null) {
			return (FileConfiguration) config;
		}

		return null;
	}

	public void reloadConfig() {
		if (config != null) {
			config.reloadFile();
		}
	}

	public void saveConfig() {
		if (config == null) {
			return;
		}

		config.saveFile();
	}

	/**
	 * Should we only show the commands on /ar help that a player has access to?
	 * 
	 * @return true if we should, false otherwise.
	 */
	public boolean doBaseHelpPageOnPermission() {
		return this.getConfig().getBoolean("show help command based on permission", false);
	}

	/**
	 * Should we check for a new version online?
	 * 
	 * @return true if we should, false otherwise.
	 */
	public boolean doCheckForNewerVersion() {
		return this.getConfig().getBoolean("auto-updater.check-for-new-versions", true);
	}

	public String getCheckCommandLayout() {
		return this.getConfig().getString("check command layout",
				"&p has played for &time and is in group(s) &groups. Requirements to be ranked up: &reqs");
	}

	/**
	 * How often should we check players?
	 * 
	 * @return how many minutes we should wait before checking players again.
	 */
	public int getIntervalTime() {
		return this.getConfig().getInt("interval check", 5);
	}

	public String getLeaderboardLayout() {
		return this.getConfig().getString("leaderboard layout",
				"&6&r | &b&p - &7&d day(s), &h hour(s) and &m minute(s).");
	}

	public int getLeaderboardLength() {
		return this.getConfig().getInt("leaderboard length", 10);
	}

	public String getMySQLSettings(final MySQLOptions option) {
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
	 * Check whether an admin has disabled automatic ranking.
	 * 
	 * @return true if it is disabled, false otherwise.
	 */
	public boolean isAutomaticPathDisabled() {
		return this.getConfig().getBoolean("disable automatic path checking", false);
	}

	public boolean onlyUsePrimaryGroupVault() {
		return this.getConfig().getBoolean("use primary group for vault", true);
	}

	/**
	 * Should we broadcast in the server when any time gets reset?
	 */
	public boolean shouldBroadcastDataReset() {
		return this.getConfig().getBoolean("broadcast resetting of data files", true);
	}

	public boolean showWarnings() {
		return this.getConfig().getBoolean("show warnings", true);
	}

	/**
	 * Check whether Autorank should log detailed information about <br>
	 * the found dependencies.
	 * 
	 * @return true if has to, false otherwise.
	 */
	public boolean useAdvancedDependencyLogs() {
		return this.getConfig().getBoolean("advanced dependency output", false);
	}

	/**
	 * Whether Autorank should care about players that are AFK or not. <br>
	 * 
	 * @return true when AFK integration should be used; false otherwise.
	 */
	public boolean useAFKIntegration() {
		return this.getConfig().getBoolean("afk integration", true);
	}

	/**
	 * Should we output debug messages?
	 */
	public boolean useDebugOutput() {
		return this.getConfig().getBoolean("use debug", false);
	}

	/**
	 * Should we display the global time in the leaderboard of Autorank.
	 * 
	 * @return true if we should, false otherwise.
	 */
	public boolean useGlobalTimeInLeaderboard() {
		return this.getConfig().getBoolean("use global time in leaderboard", false);
	}

	/**
	 * Should we use the MySQL database?
	 */
	public boolean useMySQL() {
		return this.getConfig().getBoolean("sql.enabled");
	}

	/**
	 * Are we using partial completion?
	 */
	public boolean usePartialCompletion() {
		return this.getConfig().getBoolean("use partial completion", true);
	}

	/**
	 * Get the plugin that is used to get the time a player played on this
	 * server. <br>
	 * This is only accounted for the local time. The global time is still
	 * calculated by Autorank.
	 * 
	 * @return {@link me.armar.plugins.autorank.hooks.DependencyManager.dependency}
	 *         object that is used
	 */
	public dependency useTimeOf() {

		final String timePlugin = this.getConfig().getString("use time of", "Autorank");

		if (timePlugin.equalsIgnoreCase("Stats"))
			return dependency.STATS;
		else if (timePlugin.equalsIgnoreCase("OnTime"))
			return dependency.ONTIME;
		else if (timePlugin.equals("Statz"))
			return dependency.STATZ;
		else
			return dependency.AUTORANK;
	}

}
