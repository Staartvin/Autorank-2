package me.armar.plugins.autorank.config;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.RequirementCompleteEvent;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.result.Result;

/**
 * PlayerDataConfig will keep track of the latest known group and progress a
 * player made (via /ar complete)
 * When the last known group is not equal to the current group of a player, all
 * progress should be reset as a player is not longer in the same group.
 * 
 * PlayerDataConfig uses a file (/playerdata/playerdata.yml) which keeps
 * tracks of these things.
 * 
 * @author Staartvin
 * 
 */
public class PlayerDataConfig {

	private final Autorank plugin;
	
	private SimpleYamlConfiguration config;

	private String fileName = "PlayerData.yml";
	
	private boolean convertingData = false;

	public PlayerDataConfig(final Autorank instance) {
		this.plugin = instance;

		// Start requirement saver task
		// Run save task every 2 minutes
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				saveConfig();
			}
		}, 1200, 2400);
	}

	public void createNewFile() {
		config = new SimpleYamlConfiguration(plugin, "/playerdata/" + fileName, fileName);

		plugin.getLogger().info("PlayerData file loaded (" + fileName + ")");
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

	public void addCompletedRequirement(final UUID uuid, final int reqID) {
		final List<Integer> progress = getCompletedRequirements(uuid);

		if (hasCompletedRequirement(reqID, uuid))
			return;

		progress.add(reqID);

		setCompletedRequirements(uuid, progress);
	}

	public void setCompletedRequirements(final UUID uuid, final List<Integer> requirements) {
		config.set(uuid.toString() + ".completed requirements", requirements);
	}
	
	public List<Integer> getCompletedRequirements(final UUID uuid) {
		return config.getIntegerList(uuid.toString() + ".completed requirements");
	}
	
	public boolean hasCompletedRequirement(final int reqID, final UUID uuid) {
		final List<Integer> completedRequirement = getCompletedRequirements(uuid);

		return completedRequirement.contains(reqID);
	}
	
	public void addCompletedPrerequisite(final UUID uuid, final int preReqID) {
		final List<Integer> progress = getCompletedPrerequisites(uuid);

		if (hasCompletedPrerequisite(preReqID, uuid))
			return;

		progress.add(preReqID);

		setCompletedPrerequisites(uuid, progress);
	}

	public void setCompletedPrerequisites(final UUID uuid, final List<Integer> prerequisites) {
		config.set(uuid.toString() + ".completed prerequisites", prerequisites);
	}
	
	public List<Integer> getCompletedPrerequisites(final UUID uuid) {
		return config.getIntegerList(uuid.toString() + ".completed prerequisites");
	}
	
	public boolean hasCompletedPrerequisite(final int reqID, final UUID uuid) {
		final List<Integer> completedPrerequisites = getCompletedPrerequisites(uuid);

		return completedPrerequisites.contains(reqID);
	}

	public void addCompletedPath(final UUID uuid, final String pathName) {
		final List<String> completed = getCompletedPaths(uuid);

		completed.add(pathName);

		setCompletedPaths(uuid, completed);
	}

	public boolean checkValidChosenPath(final Player player) {

		final String chosenPath = this.getChosenPath(player.getUniqueId());

		final List<Path> definedPaths = plugin.getPathManager().getPaths();

		boolean validChosenPath = false;

		// Check whether the chosen path equals one of the change groups
		for (final Path definedPath : definedPaths) {
			if (definedPath.getDisplayName().equals(chosenPath)) {
				validChosenPath = true;
			}
		}

		if (!validChosenPath) {
			// Somehow there wrong chosen path was still left over. Remove it.
			this.setChosenPath(player.getUniqueId(), null);
		}

		return validChosenPath;
	}

	public void convertNamesToUUIDs() {

		if (convertingData)
			return;

		convertingData = true;

		plugin.getLogger().info("Starting to convert playerdata.yml");

		// Run async to prevent problems.
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				// Backup beforehand
				plugin.getBackupManager().backupFile("/playerdata/playerdata.yml", null);

				for (final String name : getConfig().getKeys(false)) {

					// Probably UUID because names don't have dashes.
					if (name.contains("-"))
						continue;

					final UUID uuid = plugin.getUUIDStorage().getStoredUUID(name);

					if (uuid == null)
						continue;

					final List<Integer> progress = config.getIntegerList(name + ".progress");
					final String lastKnownGroup = config.getString(name + ".last group");

					// Remove name
					config.set(name, null);

					// Replace name with UUID
					config.set(uuid.toString() + ".progress", progress);
					config.set(uuid.toString() + ".last group", lastKnownGroup);
				}

				plugin.getLogger().info("Converted playerdata.yml to UUID format");
			}
		});
	}


	public String getChosenPath(final UUID uuid) {
		return config.getString(uuid.toString() + ".chosen path", "unknown");
	}

	private List<String> getCompletedPaths(final UUID uuid) {
		final List<String> completed = config.getStringList(uuid.toString() + ".completed paths");

		return completed;
	}

	public String getLastKnownGroup(final UUID uuid) {
		//Validate.notNull(uuid, "UUID of a player is null!");

		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);
		return config.getString(uuid.toString() + ".last group");
	}

	public boolean hasCompletedPath(final UUID uuid, final String pathName) {
		// If player can rank up forever on the same rank, we will always return false.
		if (plugin.getPathsConfig().allowInfinitePathing(pathName)) {
			return false;
		}

		return getCompletedPaths(uuid).contains(pathName);
	}


	public boolean hasLeaderboardExemption(final UUID uuid) {
		//Validate.notNull(uuid, "UUID of a player is null!");
		return config.getBoolean(uuid.toString() + ".exempt leaderboard", false);
	}

	public void hasLeaderboardExemption(final UUID uuid, final boolean value) {
		config.set(uuid.toString() + ".exempt leaderboard", value);
	}

	public void runResults(final RequirementsHolder holder, final Player player) {

		// Fire event so it can be cancelled
		// Create the event here/
		// TODO Implement logic for events with RequirementHolder
		final RequirementCompleteEvent event = new RequirementCompleteEvent(player, holder);
		// Call the event
		Bukkit.getServer().getPluginManager().callEvent(event);

		// Check if event is cancelled.
		if (event.isCancelled())
			return;

		// Run results
		final List<Result> results = holder.getResults();

		// Apply result
		for (final Result realResult : results) {
			realResult.applyResult(player);
		}
	}

	public void setChosenPath(final UUID uuid, final String path) {
		config.set(uuid.toString() + ".chosen path", path);
	}

	public void setCompletedPaths(final UUID uuid, final List<String> completedPaths) {
		config.set(uuid.toString() + ".completed paths", completedPaths);
	}

	public void setLastKnownGroup(final UUID uuid, final String group) {
		//UUID uuid = UUIDManager.getUUIDFromPlayer(playerName);
		config.set(uuid.toString() + ".last group", group);
	}
}
