package me.armar.plugins.autorank.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;

import com.google.common.collect.Lists;

/**
 * This class handles has all methods to get data from the configs. This is now
 * handled by every class seperately, but should be organised soon.
 * 
 * @author Staartvin
 * 
 */
public class ConfigHandler {

	public enum MySQLOptions {
		DATABASE, HOSTNAME, PASSWORD, TABLE, USERNAME
	}
	private final SimpleYamlConfiguration advancedConfig;
	private final Autorank plugin;

	private final SimpleYamlConfiguration settingsConfig;

	public ConfigHandler(final Autorank instance) {
		plugin = instance;
		this.settingsConfig = plugin.getSettingsConfig();
		this.advancedConfig = plugin.getAdvancedConfig();
	}

	public boolean doBaseHelpPageOnPermission() {
		return settingsConfig.getBoolean(
				"show help command based on permission", false);
	}

	public boolean doCheckForNewerVersion() {
		return settingsConfig.getBoolean("auto-updater.check-for-new-versions",
				true);
	}

	public String getCheckCommandLayout() {
		return settingsConfig
				.getString(
						"check command layout",
						"&p has played for &time and is in group(s) &groups. Requirements to be ranked up: &reqs");
	}

	public int getIntervalTime() {
		return settingsConfig.getInt("interval check", 5);
	}

	public String getLeaderboardLayout() {
		return settingsConfig.getString("leaderboard layout",
				"&6&r | &b&p - &7&d day(s), &h hour(s) and &m minute(s).");
	}

	public int getLeaderboardLength() {
		return settingsConfig.getInt("leaderboard length", 10);
	}

	public String getMySQLSettings(final MySQLOptions option) {
		switch (option) {
		case HOSTNAME:
			return settingsConfig.getString("sql.hostname");
		case USERNAME:
			return settingsConfig.getString("sql.username");
		case PASSWORD:
			return settingsConfig.getString("sql.password");
		case DATABASE:
			return settingsConfig.getString("sql.database");
		case TABLE:
			return settingsConfig.getString("sql.table");
		default:
			return null;
		}
	}

	public String getRankChange(final String group) {
		return advancedConfig.getString("ranks." + group
				+ ".results.rank change");
	}

	public Set<String> getRanks() {
		return advancedConfig.getConfigurationSection("ranks").getKeys(false);
	}

	/**
	 * Gets the requirement's id.
	 * 
	 * @param requirement Requirement name exactly as it is in the config
	 * @param group Group the requirement is from
	 * @return requirement id, -1 if nothing found
	 */
	public int getReqId(final String requirement, final String group) {
		final Object[] reqs = getRequirements(group).toArray();

		for (int i = 0; i < reqs.length; i++) {
			final String req2 = (String) reqs[i];

			if (requirement.equalsIgnoreCase(req2)) {
				return i;
			}
		}

		return -1;
	}

	public String getRequirement(final String requirement, final String group) {

		// Correct config
		String result;
		result = (advancedConfig.get("ranks." + group + ".requirements."
				+ requirement + ".value") != null) ? advancedConfig.get(
				"ranks." + group + ".requirements." + requirement + ".value")
				.toString() : advancedConfig.getString(
				"ranks." + group + ".requirements." + requirement).toString();

		return result;
	}

	public Set<String> getRequirements(final String group) {
		final Set<String> requirements = advancedConfig
				.getConfigurationSection("ranks." + group + ".requirements")
				.getKeys(false);

		return requirements;
	}

	public String getResult(final String result, final String group) {
		return advancedConfig.get("ranks." + group + ".results." + result)
				.toString();
	}

	public String getResultOfRequirement(final String requirement,
			final String group, final String result) {
		return advancedConfig.get(
				"ranks." + group + ".requirements." + requirement + ".results."
						+ result).toString();
	}

	public Set<String> getResults(final String group) {
		final Set<String> results = advancedConfig.getConfigurationSection(
				"ranks." + group + ".results").getKeys(false);

		return results;
	}

	public List<String> getResultsOfRequirement(final String requirement,
			final String group) {
		Set<String> results = new HashSet<String>();

		results = (advancedConfig.getConfigurationSection("ranks." + group
				+ ".requirements." + requirement + ".results") != null) ? advancedConfig
				.getConfigurationSection(
						"ranks." + group + ".requirements." + requirement
								+ ".results").getKeys(false)
				: new HashSet<String>();

		return Lists.newArrayList(results);
	}

	/**
	 * Gets whether a requirement is optional for a certain group
	 * 
	 * @param requirement
	 * @param group
	 * @return true if optional; false otherwise
	 */
	public boolean isOptional(final String requirement, final String group) {
		final boolean optional = advancedConfig.getBoolean("ranks." + group
				+ ".requirements." + requirement + ".options.optional", false);

		return optional;
	}

	public boolean useAdvancedConfig() {
		return settingsConfig.getBoolean("use advanced config");
	}

	/**
	 * Check whether Autorank should log detailed information about <br>
	 * the found dependencies.
	 * 
	 * @return true if has to, false otherwise.
	 */
	public boolean useAdvancedDependencyLogs() {
		if (!useAdvancedConfig())
			return false;

		return settingsConfig.getBoolean("advanced dependency output", false);
	}

	/**
	 * Whether Autorank should care about players that are AFK or not. <br>
	 * If the SimpleConfig is used, this will always be true.
	 * 
	 * @return true when AFK integration should be used; false otherwise.
	 */
	public boolean useAFKIntegration() {
		if (!useAdvancedConfig())
			return true;

		return settingsConfig.getBoolean("afk integration", false);
	}

	public boolean useAutoCompletion(final String group,
			final String requirement) {
		final boolean optional = isOptional(requirement, group);

		if (optional) {
			// Not defined (Optional + not defined = false)
			if (advancedConfig.get("ranks." + group + ".requirements."
					+ requirement + ".options.auto complete") == null) {
				//System.out.print("Return false for " + group + " requirement " + requirement);
				return false;
			} else {
				// Defined (Optional + defined = defined)
				//System.out.print("Return defined for " + group + " requirement " + requirement);
				return advancedConfig.getBoolean("ranks." + group
						+ ".requirements." + requirement
						+ ".options.auto complete");
			}
		} else {
			// Not defined (Not optional + not defined = true)
			if (advancedConfig.get("ranks." + group + ".requirements."
					+ requirement + ".options.auto complete") == null) {

				// If partial completion is false, we do not auto complete
				if (!usePartialCompletion()) {
					return false;
				}
				//System.out.print("Return true for " + group + " requirement " + requirement);
				return true;
			} else {
				// Defined (Not optional + defined = defined)
				//System.out.print("Return defined for " + group + " requirement " + requirement);
				return advancedConfig.getBoolean("ranks." + group
						+ ".requirements." + requirement
						+ ".options.auto complete");
			}
		}
	}

	public boolean useDebugOutput() {
		return settingsConfig.getBoolean("use debug", false);
	}

	public boolean useMySQL() {
		return settingsConfig.getBoolean("sql.enabled");
	}

	public boolean usePartialCompletion() {
		return settingsConfig.getBoolean("use partial completion", false);
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

		final String timePlugin = settingsConfig.getString("use time of",
				"Autorank");

		if (timePlugin.equalsIgnoreCase("Stats"))
			return dependency.STATS;
		else if (timePlugin.equalsIgnoreCase("OnTime"))
			return dependency.ONTIME;
		else
			return dependency.AUTORANK;
	}
}
