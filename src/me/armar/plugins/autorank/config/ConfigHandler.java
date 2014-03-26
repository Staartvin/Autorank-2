package me.armar.plugins.autorank.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;

import com.google.common.collect.Lists;

/**
 * This class handles has all methods to get data from the config. This is now
 * handled by every class seperately, but should be organised soon.
 * 
 * @author Staartvin
 * 
 */
public class ConfigHandler {

	private final Autorank plugin;
	private final SimpleYamlConfiguration config;

	public ConfigHandler(final Autorank instance) {
		plugin = instance;
		this.config = plugin.getAdvancedConfig();
	}

	public boolean useAdvancedConfig() {
		return plugin.getAdvancedConfig().getBoolean("use advanced config");
	}

	/**
	 * Gets whether a requirement is optional for a certain group
	 * 
	 * @param requirement
	 * @param group
	 * @return true if optional; false otherwise
	 */
	public boolean isOptional(final String requirement, final String group) {
		final boolean optional = config.getBoolean("ranks." + group
				+ ".requirements." + requirement + ".options.optional", false);

		return optional;
	}

	public Set<String> getRequirements(final String group) {
		final Set<String> requirements = config.getConfigurationSection(
				"ranks." + group + ".requirements").getKeys(false);

		return requirements;
	}

	public Set<String> getResults(final String group) {
		final Set<String> results = config.getConfigurationSection(
				"ranks." + group + ".results").getKeys(false);

		return results;
	}

	public Set<String> getRanks() {
		return config.getConfigurationSection("ranks").getKeys(false);
	}

	public String getRequirement(final String requirement, final String group) {

		// Correct config
		String result;
		result = (config.get("ranks." + group + ".requirements." + requirement
				+ ".value") != null) ? config.get(
				"ranks." + group + ".requirements." + requirement + ".value")
				.toString() : config.getString(
				"ranks." + group + ".requirements." + requirement).toString();

		return result;
	}

	public String getResult(final String result, final String group) {
		return config.get("ranks." + group + ".results." + result).toString();
	}

	public String getRankChange(final String group) {
		return config.getString("ranks." + group + ".results.rank change");
	}

	public List<String> getResultsOfRequirement(final String requirement,
			final String group) {
		Set<String> results = new HashSet<String>();

		results = (config.getConfigurationSection("ranks." + group
				+ ".requirements." + requirement + ".results") != null) ? config
				.getConfigurationSection(
						"ranks." + group + ".requirements." + requirement
								+ ".results").getKeys(false)
				: new HashSet<String>();

		return Lists.newArrayList(results);
	}

	public String getResultOfRequirement(final String requirement,
			final String group, final String result) {
		return config.get(
				"ranks." + group + ".requirements." + requirement + ".results."
						+ result).toString();
	}

	public boolean usePartialCompletion() {
		return config.getBoolean("use partial completion", false);
	}

	public boolean useMySQL() {
		return config.getBoolean("sql.enabled");
	}

	public boolean useAutoCompletion(final String group,
			final String requirement) {
		final boolean optional = isOptional(requirement, group);

		if (optional) {
			// Not defined (Optional + not defined = false)
			if (config.get("ranks." + group + ".requirements." + requirement
					+ ".options.auto complete") == null) {
				//System.out.print("Return false for " + group + " requirement " + requirement);
				return false;
			} else {
				// Defined (Optional + defined = defined)
				//System.out.print("Return defined for " + group + " requirement " + requirement);
				return config.getBoolean("ranks." + group + ".requirements."
						+ requirement + ".options.auto complete");
			}
		} else {
			// Not defined (Not optional + not defined = true)
			if (config.get("ranks." + group + ".requirements." + requirement
					+ ".options.auto complete") == null) {
				//System.out.print("Return true for " + group + " requirement " + requirement);
				return true;
			} else {
				// Defined (Not optional + defined = defined)
				//System.out.print("Return defined for " + group + " requirement " + requirement);
				return config.getBoolean("ranks." + group + ".requirements."
						+ requirement + ".options.auto complete");
			}
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
	 * Get the plugin that is used to get the time a player played on this server.
	 * <br>This is only accounted for the local time. The global time is still calculated by Autorank.
	 * @return 
	 */
	public dependency useTimeOf() {
		
		String timePlugin = config.getString("use time of", "Autorank");
		
		if (timePlugin.equalsIgnoreCase("Stats")) return dependency.STATS;
		else if (timePlugin.equalsIgnoreCase("OnTime")) return dependency.ONTIME;
		else return dependency.AUTORANK;
	}

	/**
	 * Whether Autorank should care about players that are AFK or not.
	 * <br>If the SimpleConfig is used, this will always be true.
	 * @return true when AFK integration should be used; false otherwise.
	 */
	public boolean useAFKIntegration() {
		if (!useAdvancedConfig())
			return true;

		return config.getBoolean("afk integration", false);
	}
}
