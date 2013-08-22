package me.armar.plugins.autorank.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

import com.google.common.collect.Lists;

/**
 * This class handles has all methods to get data from the config. This is now
 * handled by every class seperately, but should be organised soon.
 * 
 * @author Staartvin
 * 
 */
public class ConfigHandler {

	private Autorank plugin;
	private SimpleYamlConfiguration config;

	public ConfigHandler(Autorank instance) {
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
	public boolean isOptional(String requirement, String group) {
		boolean optional = config.getBoolean("ranks." + group
				+ ".requirements." + requirement + ".options.optional", false);

		return optional;
	}

	public Set<String> getRequirements(String group) {
		Set<String> requirements = config.getConfigurationSection(
				"ranks." + group + ".requirements").getKeys(false);

		return requirements;
	}

	public Set<String> getResults(String group) {
		Set<String> results = config.getConfigurationSection(
				"ranks." + group + ".results").getKeys(false);

		return results;
	}

	public Set<String> getRanks() {
		return config.getConfigurationSection("ranks").getKeys(false);
	}

	public String getRequirement(String requirement, String group) {

		// Correct config
		String result;
		result = (config.get("ranks." + group + ".requirements." + requirement
				+ ".value") != null) ? config.get(
				"ranks." + group + ".requirements." + requirement + ".value")
				.toString() : config.getString(
				"ranks." + group + ".requirements." + requirement).toString();

		return result;
	}

	public String getResult(String result, String group) {
		return config.get("ranks." + group + ".results." + result).toString();
	}

	public String getRankChange(String group) {
		return config.getString("ranks." + group + ".results.rank change");
	}

	public List<String> getResultsOfRequirement(String requirement, String group) {
		Set<String> results = new HashSet<String>();
		
		results = (Set<String>) ((config.getConfigurationSection("ranks." + group + ".requirements." + requirement + ".results") != null) ? config.getConfigurationSection("ranks." + group + ".requirements." + requirement + ".results").getKeys(false): new HashSet<String>());
		
		return Lists.newArrayList(results);
	}
	
	public String getResultOfRequirement(String requirement, String group, String result) {
		return config.get("ranks." + group + ".requirements." + requirement + ".results." + result).toString();
	}
	
	public boolean usePartialCompletion() {
		return config.getBoolean("use partial completion", false);
	}
	
	public boolean useMySQL() {
		return config.getBoolean("sql.enabled");
	}
	
	public boolean useAutoCompletion(String group, String requirement) {
		boolean optional = isOptional(requirement, group);
		
		if (optional) {
			// Not defined (Optional + not defined = false)
			if (config.get("ranks." + group + ".requirements." + requirement + ".options.auto complete") == null) {
				//System.out.print("Return false for " + group + " requirement " + requirement);
				return false;
			} else {
				// Defined (Optional + defined = defined)
				//System.out.print("Return defined for " + group + " requirement " + requirement);
				return config.getBoolean("ranks." + group + ".requirements." + requirement + ".options.auto complete");
			}
		} else {
			// Not defined (Not optional + not defined = true)
			if (config.get("ranks." + group + ".requirements." + requirement + ".options.auto complete") == null) {
				//System.out.print("Return true for " + group + " requirement " + requirement);
				return true;
			} else {
				// Defined (Not optional + defined = defined)
				//System.out.print("Return defined for " + group + " requirement " + requirement);
				return config.getBoolean("ranks." + group + ".requirements." + requirement + ".options.auto complete");
			}
		}
	}
	
	/**
	 * Gets the requirement's id.
	 * @param requirement Requirement name exactly as it is in the config
	 * @param group Group the requirement is from
	 * @return requirement id, -1 if nothing found
	 */
	public int getReqId(String requirement, String group) {
		Object[] reqs = getRequirements(group).toArray();
		
		for (int i=0;i<reqs.length;i++) {
			String req2 = (String) reqs[i];
			
			if (requirement.equalsIgnoreCase(req2)) {
				return i;
			}
		}
		
		return -1;
	}
}
