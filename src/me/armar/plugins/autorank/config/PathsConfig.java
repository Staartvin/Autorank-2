package me.armar.plugins.autorank.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;

import me.armar.plugins.autorank.Autorank;

/**
 * This class handles all data that can be retrieved from the paths.yml file.
 * <p>
 * Date created: 21:13:41
 * 7 dec. 2016
 * 
 * @author "Staartvin"
 *
 */
public class PathsConfig {

	private SimpleYamlConfiguration config;
	private String fileName = "Paths.yml";

	private Autorank plugin;

	public PathsConfig(Autorank instance) {
		this.plugin = instance;
	}

	public void createNewFile() {
		config = new SimpleYamlConfiguration(plugin, fileName, fileName);
		
		loadConfig();

		plugin.getLogger().info("Paths file loaded (" + fileName + ")");
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
	 * Check whether a certain path can be done over and over again.
	 * 
	 * @param pathName Name of path to check
	 * @return true if a player can do a path infinitely many times, false
	 *         otherwise.
	 */
	public boolean allowInfinitePathing(String pathName) {
		return this.getConfig().getBoolean(pathName + ".options.infinite pathing", false);
	}	

	public String getDisplayName(final String pathName) {
		return this.getConfig().getString(pathName + ".options.display name", pathName);
	}

	public List<String> getPaths() {
		return new ArrayList<String>(getConfig().getKeys(false));
	}

	/**
	 * Gets the requirement's id.
	 * 
	 * @param reqName Requirement name exactly as it is in the config
	 * @param pathName Path the requirement is from
	 * @return requirement id, -1 if nothing found
	 */
	public int getPrereqId(final String pathName, final String prereqName) {
		final Object[] reqs = getPrerequisites(pathName).toArray();

		for (int i = 0; i < reqs.length; i++) {
			final String prereqString = (String) reqs[i];

			if (prereqName.equalsIgnoreCase(prereqString)) {
				return i;
			}
		}

		return -1;
	}

	public List<String[]> getPrerequisiteOptions(final String pathName, final String prereqName) {
		// Grab options from string
		final String org = this.getPrerequisiteValue(pathName, prereqName);

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

	public List<String> getPrerequisites(String pathName) {
		return new ArrayList<String>(getConfig().getConfigurationSection(pathName + ".prerequisites").getKeys(false));
	}

	/**
	 * Gets the value string that is associated with the given requirement name
	 * in a given group.
	 * 
	 * @param pathName Name of the group.
	 * @param prereqName Name of the requirement.
	 * 
	 * @return the value string, can be null.
	 */
	public String getPrerequisiteValue(final String pathName, final String prereqName) {

		// Correct config
		String result;
		result = (this.getConfig().get(pathName + ".prerequisites." + prereqName + ".value") != null)
				? this.getConfig().get(pathName + ".prerequisites." + prereqName + ".value").toString()
				: this.getConfig().getString(pathName + ".prerequisites." + prereqName).toString();

		return result;
	}

	/**
	 * Gets the requirement's id.
	 * 
	 * @param reqName Requirement name exactly as it is in the config
	 * @param pathName Path the requirement is from
	 * @return requirement id, -1 if nothing found
	 */
	public int getReqId(final String pathName, final String reqName) {
		final Object[] reqs = getRequirements(pathName).toArray();

		for (int i = 0; i < reqs.length; i++) {
			final String reqString = (String) reqs[i];

			if (reqName.equalsIgnoreCase(reqString)) {
				return i;
			}
		}

		return -1;
	}

	public List<String[]> getRequirementOptions(final String pathName, final String reqName) {
		// Grab options from string
		final String org = this.getRequirementValue(pathName, reqName);

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

	public List<String> getRequirements(String pathName) {
		return new ArrayList<String>(getConfig().getConfigurationSection(pathName + ".requirements").getKeys(false));
	}

	/**
	 * Gets the value string that is associated with the given requirement name
	 * in a given group.
	 * 
	 * @param pathName Name of the group.
	 * @param reqName Name of the requirement.
	 * 
	 * @return the value string, can be null.
	 */
	public String getRequirementValue(final String pathName, final String reqName) {
		// Correct config
		String result;
		result = (this.getConfig().get(pathName + ".requirements." + reqName + ".value") != null)
				? this.getConfig().get(pathName + ".requirements." + reqName + ".value").toString()
				: this.getConfig().getString(pathName + ".requirements." + reqName).toString();

		return result;
	}

	public String getResultOfPath(String pathName, String resultName) {
		return getConfig().getString(pathName + ".results." + resultName);
	}

	public String getResultOfRequirement(final String pathName, final String reqName, final String resName) {
		return getConfig().getString(pathName + ".requirements." + reqName + ".results." + resName);
	}

	public List<String> getResults(String pathName) {
		return new ArrayList<String>(getConfig().getConfigurationSection(pathName + ".results").getKeys(false));
	}

	public List<String> getResultsOfRequirement(final String pathName, final String reqName) {
		Set<String> results = new HashSet<String>();

		results = (getConfig().getConfigurationSection(pathName + ".requirements." + reqName + ".results") != null)
				? getConfig().getConfigurationSection(pathName + ".requirements." + reqName + ".results").getKeys(false)
				: new HashSet<String>();

		return Lists.newArrayList(results);
	}

	public String getWorldOfRequirement(String pathName, String reqName) {
		return this.getConfig().getString(pathName + ".requirements." + reqName + ".options.world", null);
	}

	/**
	 * Gets whether a prerequisite is optional for a certain path
	 * 
	 * @param pathName
	 * @param prereqName
	 * @return true if optional; false otherwise
	 */
	public boolean isOptionalPrerequisite(final String pathName, final String prereqName) {
		final boolean optional = getConfig().getBoolean(pathName + ".prerequisites." + prereqName + ".options.optional",
				false);

		return optional;
	}

	/**
	 * Gets whether a requirement is optional for a certain path
	 * 
	 * @param pathName
	 * @param reqName
	 * @return true if optional; false otherwise
	 */
	public boolean isOptionalRequirement(final String pathName, final String reqName) {
		final boolean optional = getConfig().getBoolean(pathName + ".requirements." + reqName + ".options.optional",
				false);

		return optional;
	}

	/**
	 * Check whether a specific requirement of a group will derank a player if
	 * it is not met.
	 * 
	 * @param pathName Name of the path
	 * @param reqName Name of the requirement
	 * @return true if it is derankable, false otherwise.
	 */
	public boolean isRequirementDerankable(final String pathName, final String reqName) {
		return this.getConfig().getBoolean(pathName + ".requirements." + reqName + ".options.derankable", false);
	}

	public boolean isRequirementWorldSpecific(String pathName, String reqName) {
		return this.getWorldOfRequirement(pathName, reqName) != null;
	}

	public void loadConfig() {

		getConfig().options()
				.header("Paths file - This file will contain all paths that a player is allowed to take. Previously known as 'AdvancedConfig.yml'."
						+ "\nThis file contains (by default) a Test group that shows the basic syntax of this file. "
						+ "\nFor more information, go to <LINK HERE>.");

		// Add default values here

		String pathName = "Test";

		// Prerequisites before a player can choose this path
		//getConfig().addDefault(pathName + ".prerequisites.rank", "Test");
		getConfig().addDefault(pathName + ".prerequisites.world", "world_nether");
		getConfig().addDefault(pathName + ".prerequisites.money", 1000);

		// Requirements a player must meet to complete this path
		getConfig().addDefault(pathName + ".requirements.money", 10000);
		getConfig().addDefault(pathName + ".requirements.blocks broken", 50);

		// Results that will be fired when a player completes this path
		getConfig().addDefault(pathName + ".results.rank change", "TestGroup2");
		getConfig().addDefault(pathName + ".results.command", "broadcast &p has just completed path 'Default'!");
		getConfig().addDefault(pathName + ".results.command2", "give &p 1 100");
		getConfig().addDefault(pathName + ".results.message", "Congratulations, you completed the 'Default' path!");

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	/**
	 * Should we use auto completion for a certain requirement in a group.
	 * 
	 * @param pathName Path to check for.
	 * @param reqName Requirement to check for.
	 * @return true if we should, false otherwise.
	 */
	public boolean useAutoCompletion(final String pathName, final String reqName) {
		final boolean optional = isOptionalRequirement(pathName, reqName);

		if (optional) {
			// Not defined (Optional + not defined = false)
			if (this.getConfig().get(pathName + ".requirements." + reqName + ".options.auto complete") == null) {
				return false;
			} else {
				// Defined (Optional + defined = defined)
				return this.getConfig().getBoolean(pathName + ".requirements." + reqName + ".options.auto complete");
			}
		} else {
			// Not defined (Not optional + not defined = true)
			if (this.getConfig().get(pathName + ".requirements." + reqName + ".options.auto complete") == null) {

				// If partial completion is false, we do not auto complete
				/*if (!usePartialCompletion()) {
					return false;
				}*/
				return true;
			} else {
				// Defined (Not optional + defined = defined)
				return this.getConfig().getBoolean(pathName + ".requirements." + reqName + ".options.auto complete");
			}
		}
	}
}
