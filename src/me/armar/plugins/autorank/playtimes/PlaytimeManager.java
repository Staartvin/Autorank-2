package me.armar.plugins.autorank.playtimes;

import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.StatsPlugin.statTypes;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.OnTimeHandler;
import me.staartvin.statz.hooks.handlers.StatsAPIHandler;

public class PlaytimeManager {

	// Autorank keeps track of total time, time online on one day, time online
	// in a week and time online in a month.
	// There are all tracked in minutes.

	public static int INTERVAL_MINUTES = 5;
	
	private final Autorank plugin;

	// What plugin should Autorank use to check time?
	private final AutorankDependency timePlugin;

	public PlaytimeManager(final Autorank plugin) {
		this.plugin = plugin;

		INTERVAL_MINUTES = plugin.getConfigHandler().getIntervalTime();

		plugin.getLogger().info("Interval check every " + INTERVAL_MINUTES + " minutes.");

		timePlugin = plugin.getConfigHandler().useTimeOf();
	}

	/**
	 * Use this method to convert an old data.yml (that was storing player
	 * names) to the new format (storing UUIDs).
	 * 
	 */
	/*public void convertToUUIDStorage() {

		// Run async to prevent load-time problems.
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {

				final SimpleYamlConfiguration data = getDataFile(TimeType.TOTAL_TIME);

				// Before running, backup stuff.
				plugin.getBackupManager().backupFile("Data.yml", null);

				// First archive all names below 1
				archive(1);

				final Set<String> records = data.getKeys(false);

				final int size = records.size();

				// 9 items per second
				final int speed = 9;
				final int duration = (int) Math.floor(size / speed);
				final String timeName = getDurationString(duration);

				plugin.getLogger().warning("Starting converting data.yml");
				plugin.getLogger().warning("Conversion will take approx. " + timeName + "( guess for your data.yml)");

				for (final String record : records) {
					// UUID contains dashes and playernames do not, so if it
					// contains dashes
					// it is probably a UUID and thus we should skip it.
					if (record.contains("-"))
						continue;

					final UUID uuid = plugin.getUUIDStorage().getStoredUUID(record);

					// Could not convert this name to uuid
					if (uuid == null) {
						plugin.getLogger().severe("Could not find UUID of " + record);
						continue;
					}

					// Get the time that player has played.
					final int minutesPlayed = data.getInt(record, 0);

					// Remove the data from the file.
					data.set(record, null);

					// Add new data (in UUID form to the file)
					data.set(uuid.toString(), minutesPlayed);
				}

				saveFiles();

				plugin.getLogger().info("Converted data.yml to UUID format");
			}

		});
	}*/

	/**
	 * Get the time of a player. <br>
	 * This depends on what plugin is used to get the time from. <br>
	 * Time is seconds.
	 * 
	 * @param playerName
	 *            Player to get the time for
	 * @param cache
	 *            whether to only use cache or use real time values.
	 * @return play time of given player or 0 if not found.
	 */
	public int getTimeOfPlayer(final String playerName, final boolean cache) {

		int playTime = 0;

		UUID uuid = null;

		// If using cache, just get the latest stored uuid
		if (cache) {
			uuid = plugin.getUUIDStorage().getStoredUUID(playerName);
		} else {
			uuid = UUIDManager.getUUIDFromPlayer(playerName);
		}

		// Determine what plugin to use for getting the time.
		if (timePlugin.equals(AutorankDependency.STATS)) {
			final StatsPlugin stats = plugin.getHookedStatsPlugin();

			if (stats instanceof StatsHandler) {
				// In seconds
				playTime = ((StatsAPIHandler) plugin.getDependencyManager().getDependencyHandler(Dependency.STATS))
						.getTotalPlayTime(uuid, null);
			} else {

				if (uuid == null)
					return playTime;

				// Stats not found, using Autorank's system.
				playTime = plugin.getFlatFileManager().getLocalTime(uuid) * 60;
			}
		} else if (timePlugin.equals(AutorankDependency.ONTIME)) {
			playTime = ((OnTimeHandler) plugin.getDependencyManager().getDependencyHandler(Dependency.ON_TIME))
					.getPlayTime(playerName);
			// Time is in minutes, so convert to seconds
			playTime = playTime * 60;
		} else if (timePlugin.equals(AutorankDependency.STATZ)) {
			playTime = (int) ((StatzAPIHandler) plugin.getDependencyManager().getDependency(AutorankDependency.STATZ))
					.getTotalOf(uuid, statTypes.TIME_PLAYED, null);
			playTime = playTime * 60;
		} else {

			if (uuid == null)
				return playTime;

			// Use internal system of Autorank.
			playTime = plugin.getFlatFileManager().getLocalTime(uuid) * 60;
		}

		return playTime;
	}




}
