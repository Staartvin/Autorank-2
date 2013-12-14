package me.armar.plugins.autorank.requirementhandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.RequirementCompleteEvent;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * RequirementHandler will keep track of the latest known group and progress a
 * player made (via /ar complete)
 * When the last known group is not equal to the current group of a player, all
 * progress should be reset as a player is not longer in the same group.
 * 
 * RequirementHandler uses a file (/playerdata/playerdata.yml) which keeps
 * tracks of these things.
 * 
 * @author Staartvin
 * 
 */
public class RequirementHandler {

	private final Autorank plugin;
	private FileConfiguration config;
	private File configFile;

	public RequirementHandler(final Autorank instance) {
		this.plugin = instance;
	}

	public void createNewFile() {
		reloadConfig();
		saveConfig();
		loadConfig();

		plugin.getLogger()
				.info("New playerdata.yml file loaded. AR can keep track of player progress now!");
	}

	public void reloadConfig() {
		if (configFile == null) {
			configFile = new File(plugin.getDataFolder() + "/playerdata",
					"playerdata.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		// Look for defaults in the jar
		final InputStream defConfigStream = plugin
				.getResource("playerdata.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}

	public FileConfiguration getConfig() {
		if (config == null) {
			this.reloadConfig();
		}
		return config;
	}

	public void saveConfig() {
		if (config == null || configFile == null) {
			return;
		}
		try {
			getConfig().save(configFile);
		} catch (final IOException ex) {
			plugin.getLogger().log(Level.SEVERE,
					"Could not save config to " + configFile, ex);
		}
	}

	public void loadConfig() {

		config.options().header(
				"This file saves all progress of players."
						+ "\nIt stores their progress of /ar complete");

		config.options().copyDefaults(true);
		saveConfig();
	}

	public void setPlayerProgress(final String playerName,
			final List<Integer> progress) {
		config.set(playerName + ".progress", progress);

		saveConfig();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getProgress(final String playerName) {
		return (List<Integer>) config.getList(playerName + ".progress",
				new ArrayList<Integer>());
	}

	public void addPlayerProgress(final String playerName, final int reqID) {
		final List<Integer> progress = getProgress(playerName);

		if (hasCompletedRequirement(reqID, playerName))
			return;

		progress.add(reqID);

		setPlayerProgress(playerName, progress);
	}

	public String getLastKnownGroup(final String playerName) {
		return config.getString(playerName + ".last group");
	}

	public void setLastKnownGroup(final String playerName, final String group) {
		config.set(playerName + ".last group", group);

		saveConfig();
	}

	public boolean hasCompletedRequirement(final int reqID,
			final String playerName) {
		final List<Integer> progress = getProgress(playerName);

		return progress.contains(reqID);
	}

	public void runResults(final Requirement req, final Player player) {

		// Fire event so it can be cancelled
		// Create the event here
		final RequirementCompleteEvent event = new RequirementCompleteEvent(
				player, req);
		// Call the event
		Bukkit.getServer().getPluginManager().callEvent(event);

		// Check if event is cancelled.
		if (event.isCancelled())
			return;

		// Run results
		final List<Result> results = req.getResults();

		// Apply result
		for (final Result realResult : results) {
			realResult.applyResult(player);
		}
	}
}
