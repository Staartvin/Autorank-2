package me.armar.plugins.autorank.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
public class PathsFileHandler {

	private Autorank plugin;

	private FileConfiguration pathsConfig;
	private File pathsConfigFile;

	public PathsFileHandler(Autorank instance) {
		this.plugin = instance;
	}

	public void createNewFile() {
		reloadConfig();
		saveConfig();
		loadConfig();

		plugin.getLogger().info("Paths file loaded (paths.yml)");
	}

	public FileConfiguration getConfig() {
		if (pathsConfig == null) {
			this.reloadConfig();
		}
		return pathsConfig;
	}

	public void loadConfig() {

		pathsConfig.options()
				.header("Paths file - This file will contain all paths that a player is allowed to take. Previously known as 'AdvancedConfig.yml'."
						+ "\nThis file contains (by default) a Test group that shows the basic syntax of this file. "
						+ "\nFor more information, go to <LINK HERE>.");

		// Add default values here

		String pathName = "Test";

		// Prerequisites before a player can choose this path
		pathsConfig.addDefault(pathName + ".prerequisites.rank", "Test");
		pathsConfig.addDefault(pathName + ".prerequisites.world", "Good olde world");
		pathsConfig.addDefault(pathName + ".prerequisites.money", 1000);

		// Requirements a player must meet to complete this path
		pathsConfig.addDefault(pathName + ".requirements.damage taken", 500);
		pathsConfig.addDefault(pathName + ".requirements.votes", 120);
		pathsConfig.addDefault(pathName + ".requirements.money", 10000);
		pathsConfig.addDefault(pathName + ".requirements.players killed", 25);
		pathsConfig.addDefault(pathName + ".requirements.in biome", "RIVER");
		pathsConfig.addDefault(pathName + ".requirements.fish caught", 50);

		// Results that will be fired when a player completes this path
		pathsConfig.addDefault(pathName + ".results.rank change", "TestGroup2");
		pathsConfig.addDefault(pathName + ".results.command", "broadcast &p has just completed path 'Default'!");
		pathsConfig.addDefault(pathName + ".results.command2", "give &p 1 100");
		pathsConfig.addDefault(pathName + ".results.message", "Congratulations, you completed the 'Default' path!");

		pathsConfig.options().copyDefaults(true);
		saveConfig();
	}

	@SuppressWarnings("deprecation")
	public void reloadConfig() {
		if (pathsConfigFile == null) {
			pathsConfigFile = new File(plugin.getDataFolder(), "paths.yml");
		}
		pathsConfig = YamlConfiguration.loadConfiguration(pathsConfigFile);

		// Look for defaults in the jar
		final InputStream defConfigStream = plugin.getResource("paths.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			pathsConfig.setDefaults(defConfig);
		}
	}

	public void saveConfig() {
		if (pathsConfig == null || pathsConfigFile == null) {
			return;
		}
		try {
			getConfig().save(pathsConfigFile);
		} catch (final IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + pathsConfigFile, ex);
		}
	}

	public List<String> getPrerequisites(String pathName) {
		return new ArrayList<String>(pathsConfig.getConfigurationSection(pathName + ".prerequisites").getKeys(false));
	}

	public List<String> getRequirements(String pathName) {
		return new ArrayList<String>(pathsConfig.getConfigurationSection(pathName + ".requirements").getKeys(false));
	}

	public List<String> getResults(String pathName) {
		return new ArrayList<String>(pathsConfig.getConfigurationSection(pathName + ".results").getKeys(false));
	}
	
	public String getResultOfPath(String pathName, String resultName) {
		return pathsConfig.getString(pathName + ".results." + resultName);
	}
	
	/**
	 * Gets whether a requirement is optional for a certain path
	 * 
	 * @param pathName
	 * @param reqName
	 * @return true if optional; false otherwise
	 */
	public boolean isOptionalRequirement(final String pathName, final String reqName) {
		final boolean optional = pathsConfig
				.getBoolean(pathName + ".requirements." + reqName + ".options.optional", false);

		return optional;
	}
	
	/**
	 * Gets whether a prerequisite is optional for a certain path
	 * 
	 * @param pathName
	 * @param prereqName
	 * @return true if optional; false otherwise
	 */
	public boolean isOptionalPrerequisites(final String pathName, final String prereqName) {
		final boolean optional = pathsConfig
				.getBoolean(pathName + ".prerequisites." + prereqName + ".options.optional", false);

		return optional;
	}
	
	public List<String> getResultsOfRequirement(final String pathName, final String reqName) {
		Set<String> results = new HashSet<String>();

		results = (pathsConfig
				.getConfigurationSection(pathName + ".requirements." + reqName + ".results") != null)
						? pathsConfig
								.getConfigurationSection(pathName + ".requirements." + reqName + ".results")
								.getKeys(false)
						: new HashSet<String>();

		return Lists.newArrayList(results);
	}
	
	public List<String> getResultsOfPrerequisites(final String pathName, final String prereqName) {
		Set<String> results = new HashSet<String>();

		results = (pathsConfig
				.getConfigurationSection(pathName + ".prerequisites." + prereqName + ".results") != null)
						? pathsConfig
								.getConfigurationSection(pathName + ".prerequisites." + prereqName + ".results")
								.getKeys(false)
						: new HashSet<String>();

		return Lists.newArrayList(results);
	}
	
	public String getResultOfRequirement(final String pathName, final String reqName, final String resName) {
		return pathsConfig.getString(pathName + ".requirements." + reqName + ".results." + resName);
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
		result = (plugin.getAdvancedConfig().get(pathName + ".requirements." + reqName + ".value") != null)
				? plugin.getAdvancedConfig().get(pathName + ".requirements." + reqName + ".value")
						.toString()
				: plugin.getAdvancedConfig().getString(pathName + ".requirements." + reqName).toString();

		return result;
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
		result = (plugin.getAdvancedConfig().get(pathName + ".requisites." + prereqName + ".value") != null)
				? plugin.getAdvancedConfig().get(pathName + ".requisites." + prereqName + ".value")
						.toString()
				: plugin.getAdvancedConfig().getString(pathName + ".requisites." + prereqName).toString();

		return result;
	}
	
	public List<String[]> getRequirementOptions(final String pathName, final String reqName) {
		// Grab options from string
		final String org = this.getRequirementValue(reqName, pathName);

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
	
	public List<String[]> getPrerequisiteOptions(final String pathName, final String prereqName) {
		// Grab options from string
		final String org = this.getPrerequisiteValue(prereqName, pathName);

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
			if (this.pathsConfig
					.get(pathName + ".requirements." + reqName + ".options.auto complete") == null) {
				return false;
			} else {
				// Defined (Optional + defined = defined)
				return this.pathsConfig
						.getBoolean(pathName + ".requirements." + reqName + ".options.auto complete");
			}
		} else {
			// Not defined (Not optional + not defined = true)
			if (this.pathsConfig
					.get(pathName + ".requirements." + reqName + ".options.auto complete") == null) {

				// If partial completion is false, we do not auto complete
				/*if (!usePartialCompletion()) {
					return false;
				}*/
				return true;
			} else {
				// Defined (Not optional + defined = defined)
				return this.pathsConfig
						.getBoolean(pathName + ".requirements." + reqName + ".options.auto complete");
			}
		}
	}
	
	public boolean isRequirementWorldSpecific(String pathName, String reqName) {
		return this.getWorldOfRequirement(pathName, reqName) != null;
	}
	
	public String getWorldOfRequirement(String pathName, String reqName) {
		return this.pathsConfig
				.getString(pathName + ".requirements." + reqName + ".options.world", null);
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
		return this.pathsConfig
				.getBoolean(pathName + ".requirements." + reqName + ".options.derankable", false);
	}
	
	public List<String> getPaths() {
		return new ArrayList<String>(pathsConfig.getKeys(false));
	}
}
