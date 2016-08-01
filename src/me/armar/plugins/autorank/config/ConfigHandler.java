package me.armar.plugins.autorank.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;

/**
 * This class has all methods to get data from the configs.
 * <br>
 * The configurations of the Settings.yml, AdvancedConfig.yml and
 * SimpleConfig.yml can be reached from here.
 * 
 * @author Staartvin
 * 
 */
public class ConfigHandler {

	public enum MySQLOptions {
		DATABASE, HOSTNAME, PASSWORD, TABLE, USERNAME
	}

	private final Autorank plugin;

	public ConfigHandler(final Autorank instance) {
		plugin = instance;
	}

	/**
	 * Should we only show the commands on /ar help that a player has access to?
	 * 
	 * @return true if we should, false otherwise.
	 */
	public boolean doBaseHelpPageOnPermission() {
		return plugin.getSettingsConfig().getBoolean("show help command based on permission", false);
	}

	/**
	 * Should we check for a new version online?
	 * 
	 * @return true if we should, false otherwise.
	 */
	public boolean doCheckForNewerVersion() {
		return plugin.getSettingsConfig().getBoolean("auto-updater.check-for-new-versions", true);
	}

	public String getCheckCommandLayout() {
		return plugin.getSettingsConfig().getString("check command layout",
				"&p has played for &time and is in group(s) &groups. Requirements to be ranked up: &reqs");
	}

	/**
	 * How often should we check players?
	 * 
	 * @return how many minutes we should wait before checking players again.
	 */
	public int getIntervalTime() {
		return plugin.getSettingsConfig().getInt("interval check", 5);
	}

	public String getLeaderboardLayout() {
		return plugin.getSettingsConfig().getString("leaderboard layout",
				"&6&r | &b&p - &7&d day(s), &h hour(s) and &m minute(s).");
	}

	public int getLeaderboardLength() {
		return plugin.getSettingsConfig().getInt("leaderboard length", 10);
	}

	public String getMySQLSettings(final MySQLOptions option) {
		switch (option) {
			case HOSTNAME:
				return plugin.getSettingsConfig().getString("sql.hostname");
			case USERNAME:
				return plugin.getSettingsConfig().getString("sql.username");
			case PASSWORD:
				return plugin.getSettingsConfig().getString("sql.password");
			case DATABASE:
				return plugin.getSettingsConfig().getString("sql.database");
			case TABLE:
				return plugin.getSettingsConfig().getString("sql.table");
			default:
				return null;
		}
	}

	public String getRankChange(final String group) {
		return plugin.getAdvancedConfig().getString("ranks." + group + ".results.rank change");
	}

	public Set<String> getRanks() {
		if (this.useAdvancedConfig()) {
			return plugin.getAdvancedConfig().getConfigurationSection("ranks").getKeys(false);
		} else {
			return plugin.getSimpleConfig().getKeys(false);
		}
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

	/**
	 * Gets the value string that is associated with the given requirement name
	 * in a given group.
	 * 
	 * @param requirement Name of the requirement.
	 * @param group Name of the group.
	 * @return the value string, can be null.
	 */
	public String getRequirement(final String requirement, final String group) {

		// Correct config
		String result;
		result = (plugin.getAdvancedConfig().get("ranks." + group + ".requirements." + requirement + ".value") != null)
				? plugin.getAdvancedConfig().get("ranks." + group + ".requirements." + requirement + ".value")
						.toString()
				: plugin.getAdvancedConfig().getString("ranks." + group + ".requirements." + requirement).toString();

		return result;
	}

	public Set<String> getRequirements(final String group) {
		Set<String> requirements;
		try {
			requirements = plugin.getAdvancedConfig().getConfigurationSection("ranks." + group + ".requirements")
					.getKeys(false);
		} catch (final NullPointerException e) {
			plugin.getLogger().severe("Error occured when trying to get requirements for group '" + group + "'!");
			return Collections.emptySet();
		}

		return requirements;
	}

	public String getResult(final String result, final String group) {
		return plugin.getAdvancedConfig().get("ranks." + group + ".results." + result).toString();
	}

	public String getResultOfRequirement(final String requirement, final String group, final String result) {
		return plugin.getAdvancedConfig().get("ranks." + group + ".requirements." + requirement + ".results." + result)
				.toString();
	}

	public String getWorldOfRequirement(final String requirement, final String group) {
		return plugin.getAdvancedConfig()
				.getString("ranks." + group + ".requirements." + requirement + ".options.world", null);
	}

	public boolean isRequirementWorldSpecific(final String requirement, final String group) {
		return this.getWorldOfRequirement(requirement, group) != null;
	}

	public Set<String> getResults(final String group) {
		final Set<String> results = plugin.getAdvancedConfig().getConfigurationSection("ranks." + group + ".results")
				.getKeys(false);

		return results;
	}

	public List<String> getResultsOfRequirement(final String requirement, final String group) {
		Set<String> results = new HashSet<String>();

		results = (plugin.getAdvancedConfig()
				.getConfigurationSection("ranks." + group + ".requirements." + requirement + ".results") != null)
						? plugin.getAdvancedConfig()
								.getConfigurationSection("ranks." + group + ".requirements." + requirement + ".results")
								.getKeys(false)
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
		final boolean optional = plugin.getAdvancedConfig()
				.getBoolean("ranks." + group + ".requirements." + requirement + ".options.optional", false);

		return optional;
	}

	/**
	 * Should we use the AdvancedConfig?
	 */
	public boolean useAdvancedConfig() {
		return plugin.getSettingsConfig().getBoolean("use advanced config");
	}

	/**
	 * Check whether Autorank should log detailed information about <br>
	 * the found dependencies.
	 * 
	 * @return true if has to, false otherwise.
	 */
	public boolean useAdvancedDependencyLogs() {
		return plugin.getSettingsConfig().getBoolean("advanced dependency output", false);
	}

	/**
	 * Whether Autorank should care about players that are AFK or not. <br>
	 * If the SimpleConfig is used, this will always be true.
	 * 
	 * @return true when AFK integration should be used; false otherwise.
	 */
	public boolean useAFKIntegration() {
		return plugin.getSettingsConfig().getBoolean("afk integration", false);
	}

	/**
	 * Should we use auto completion for a certain requirement in a group.
	 * 
	 * @param group Group to check for.
	 * @param requirement Requirement to check for.
	 * @return true if we should, false otherwise.
	 */
	public boolean useAutoCompletion(final String group, final String requirement) {
		final boolean optional = isOptional(requirement, group);

		if (optional) {
			// Not defined (Optional + not defined = false)
			if (plugin.getAdvancedConfig()
					.get("ranks." + group + ".requirements." + requirement + ".options.auto complete") == null) {
				return false;
			} else {
				// Defined (Optional + defined = defined)
				return plugin.getAdvancedConfig()
						.getBoolean("ranks." + group + ".requirements." + requirement + ".options.auto complete");
			}
		} else {
			// Not defined (Not optional + not defined = true)
			if (plugin.getAdvancedConfig()
					.get("ranks." + group + ".requirements." + requirement + ".options.auto complete") == null) {

				// If partial completion is false, we do not auto complete
				if (!usePartialCompletion()) {
					return false;
				}
				return true;
			} else {
				// Defined (Not optional + defined = defined)
				return plugin.getAdvancedConfig()
						.getBoolean("ranks." + group + ".requirements." + requirement + ".options.auto complete");
			}
		}
	}

	/**
	 * Should we output debug messages?
	 */
	public boolean useDebugOutput() {
		return plugin.getSettingsConfig().getBoolean("use debug", false);
	}

	/**
	 * Should we use the MySQL database?
	 */
	public boolean useMySQL() {
		return plugin.getSettingsConfig().getBoolean("sql.enabled");
	}

	/**
	 * Are we using partial completion?
	 */
	public boolean usePartialCompletion() {
		return plugin.getSettingsConfig().getBoolean("use partial completion", false);
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

		final String timePlugin = plugin.getSettingsConfig().getString("use time of", "Autorank");

		if (timePlugin.equalsIgnoreCase("Stats"))
			return dependency.STATS;
		else if (timePlugin.equalsIgnoreCase("OnTime"))
			return dependency.ONTIME;
		else if (timePlugin.equals("Statz"))
			return dependency.STATZ;
		else
			return dependency.AUTORANK;
	}

	public boolean allowInfiniteRanking() {
		return plugin.getSettingsConfig().getBoolean("allow infinite ranking", false);
	}

	public List<String[]> getOptions(final String requirement, final String group) {
		// Grab options from string
		final String org = this.getRequirement(requirement, group);

		final List<String[]> list = new ArrayList<String[]>();

		final String[] split = org.split(",");

		for (final String sp : split) {
			final StringBuilder builder = new StringBuilder(sp);

			if (builder.charAt(0) == '(') {
				builder.deleteCharAt(0);
			}

			if (builder.charAt(builder.length() - 1) == ')') {
				builder.deleteCharAt(builder.length() - 1);
			}

			final String[] splitArray = builder.toString().trim().split(";");
			list.add(splitArray);
		}

		return list;
	}

	public boolean showWarnings() {
		return plugin.getSettingsConfig().getBoolean("show warnings", true);
	}

	public boolean onlyUsePrimaryGroupVault() {
		return plugin.getSettingsConfig().getBoolean("use primary group for vault", true);
	}

	// A display name for a changegroup is when the changegroup is a copy of another group.
	public String getDisplayName(final String group) {
		final String displayName = plugin.getAdvancedConfig().getString("ranks." + group + ".options.display name",
				group);

		return displayName;
	}

	/**
	 * Should we broadcast in the server when any time gets reset?
	 */
	public boolean shouldBroadcastDataReset() {
		return plugin.getSettingsConfig().getBoolean("broadcast resetting of data files", true);
	}

	/**
	 * Does this server allow deranking for certain groups?
	 */
	public boolean allowDeranking() {
		return plugin.getSettingsConfig().getBoolean("allow deranking", false);
	}

	/**
	 * Check whether a specific requirement of a group will derank a player if
	 * it is not met.
	 * 
	 * @param groupName Name of the group
	 * @param requirement Name of the requirement
	 * @return true if it is derankable, false otherwise.
	 */
	public boolean isRequirementDerankable(final String groupName, final String requirement) {
		return plugin.getAdvancedConfig()
				.getBoolean("ranks." + groupName + ".requirements." + requirement + ".options.derankable", false);
	}

	/**
	 * Get the commands (as strings) that have to be performed when a player
	 * deranks due to a certain requirement in a group.
	 * 
	 * @param groupName Name of the group the player is in
	 * @param requirement Name of the requirement that triggered the derank.
	 * @return list of commands to run.
	 */
	public List<String> getCommandsOnDerank(final String groupName, final String requirement) {
		return plugin.getAdvancedConfig()
				.getStringList("ranks." + groupName + ".requirements." + requirement + ".options.commands on derank");
	}

	/**
	 * Get the name of a requirement (in the config) from a group with a
	 * specific requirement id.
	 * 
	 * @param groupName Name of the group the requirement belongs to.
	 * @param reqID ID of the requirement
	 * @return Name of the requirement (as specified in the config) or null if
	 *         not found.
	 */
	public String getRequirementNameOfId(final String groupName, final int reqID) {
		for (final String req : this.getRequirements(groupName)) {
			if (this.getReqId(req, groupName) == reqID) {
				return req;
			}
		}

		return null;
	}
	
	public boolean useGlobalTimeInLeaderboard() {
		return plugin.getSettingsConfig().getBoolean("use global time in leaderboard", false);
	}

}
