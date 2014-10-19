package me.armar.plugins.autorank.requirementhandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.RequirementCompleteEvent;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

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

	private FileConfiguration config;
	private File configFile;
	private boolean convertingData = false;

	private final Autorank plugin;

	public RequirementHandler(final Autorank instance) {
		this.plugin = instance;

		// Start requirement saver task
		//Run save task every 2 minutes
		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				saveConfig();
			}
		}, 1200, 2400);			
	}

	public void addCompletedRanks(final UUID uuid, final String rank) {
		final List<String> completed = getCompletedRanks(uuid);

		completed.add(rank);

		setCompletedRanks(uuid, completed);
	}

	public void addPlayerProgress(final UUID uuid, final int reqID) {
		final List<Integer> progress = getProgress(uuid);

		if (hasCompletedRequirement(reqID, uuid))
			return;

		progress.add(reqID);

		setPlayerProgress(uuid, progress);
	}

	public void convertNamesToUUIDs() {

		if (convertingData)
			return;

		convertingData = true;

		plugin.getLogger().info("Starting to convert playerdata.yml");

		// Run async to prevent problems.
		plugin.getServer().getScheduler()
				.runTaskAsynchronously(plugin, new Runnable() {

					@Override
					public void run() {
						for (final String name : getConfig().getKeys(false)) {

							// Probably UUID because names don't have dashes.
							if (name.contains("-"))
								continue;

							final UUID uuid = UUIDManager
									.getUUIDFromPlayer(name);

							if (uuid == null)
								continue;

							final List<Integer> progress = config
									.getIntegerList(name + ".progress");
							final String lastKnownGroup = config.getString(name
									+ ".last group");

							// Remove name
							config.set(name, null);

							// Replace name with UUID
							config.set(uuid.toString() + ".progress", progress);
							config.set(uuid.toString() + ".last group",
									lastKnownGroup);
						}

						plugin.getLogger().info(
								"Converted playerdata.yml to UUID format");
					}
				});
	}

	public void createNewFile() {
		reloadConfig();
		saveConfig();
		loadConfig();

		// Convert old format to new UUID storage format
		//convertNamesToUUIDs();

		plugin.getLogger().info("Loaded playerdata.");
	}

	private List<String> getCompletedRanks(final UUID uuid) {
		final List<String> completed = config.getStringList(uuid.toString()
				+ ".completed ranks");

		return completed;
	}

	public FileConfiguration getConfig() {
		if (config == null) {
			this.reloadConfig();
		}
		return config;
	}

	public String getLastKnownGroup(final UUID uuid) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);

		plugin.debugMessage("Config - Last known group: " + config);
		plugin.debugMessage("Config - Last known group (uuid): "
				+ uuid.toString());
		return config.getString(uuid.toString() + ".last group");
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getProgress(final UUID uuid) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);

		return (List<Integer>) config.getList(uuid.toString() + ".progress",
				new ArrayList<Integer>());
	}

	public boolean hasCompletedRank(final UUID uuid, final String rank) {
		return getCompletedRanks(uuid).contains(rank);
	}

	public boolean hasCompletedRequirement(final int reqID, final UUID uuid) {
		final List<Integer> progress = getProgress(uuid);

		return progress.contains(reqID);
	}

	public void loadConfig() {

		config.options().header(
				"This file saves all progress of players."
						+ "\nIt stores their progress of /ar complete");

		config.options().copyDefaults(true);
		saveConfig();
	}

	@SuppressWarnings("deprecation")
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

	public void setCompletedRanks(final UUID uuid,
			final List<String> completedRanks) {
		config.set(uuid.toString() + ".completed ranks", completedRanks);
	}

	public void setLastKnownGroup(final UUID uuid, final String group) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);
		config.set(uuid.toString() + ".last group", group);
	}

	public void setPlayerProgress(final UUID uuid, final List<Integer> progress) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);

		config.set(uuid.toString() + ".progress", progress);
	}
}
