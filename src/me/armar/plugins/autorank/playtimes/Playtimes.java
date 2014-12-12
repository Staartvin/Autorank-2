package me.armar.plugins.autorank.playtimes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.ontimeapi.OnTimeHandler;
import me.armar.plugins.autorank.hooks.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

public class Playtimes {

	public static int INTERVAL_MINUTES = 5;

	private final SimpleYamlConfiguration data;
	private final Autorank plugin;
	private final PlaytimesSave save;
	// Used to store what plugin Autorank uses for checking the time
	private final dependency timePlugin;

	private final PlaytimesUpdate update;

	public Playtimes(final Autorank plugin) {
		this.plugin = plugin;

		INTERVAL_MINUTES = plugin.getConfigHandler().getIntervalTime();

		plugin.getLogger().info(
				"Interval check every " + INTERVAL_MINUTES + " minutes.");

		this.data = new SimpleYamlConfiguration(plugin, "Data.yml", null,
				"Data");
		this.save = new PlaytimesSave(this);
		this.update = new PlaytimesUpdate(this, plugin);

		// Run save task every 30 seconds
		plugin.getServer().getScheduler()
				.runTaskTimerAsynchronously(plugin, save, 20L, 1200L);

		// Run update timer every x minutes
		plugin.getServer()
				.getScheduler()
				.runTaskTimerAsynchronously(plugin, update,
						INTERVAL_MINUTES * 20 * 60, INTERVAL_MINUTES * 20 * 60);

		timePlugin = plugin.getConfigHandler().useTimeOf();
	}

	/**
	 * Archive old records. Records below the minimum will be removed because
	 * they are 'inactive'.
	 * 
	 * @param minimum Lowest threshold to check for
	 * @return Amount of records removed
	 */
	public int archive(final int minimum) {
		// Keep a counter of archived items
		int counter = 0;

		for (final UUID uuid : getUUIDKeys()) {
			final int time = this.getLocalTime(uuid);

			// Found a record to be archived
			if (time < minimum) {
				counter++;

				// Remove record
				data.set(uuid.toString(), null);
			}
		}

		save();
		return counter;
	}

	/**
	 * Use this method to convert an old data.yml (that was storing player
	 * names) to the new format (storing UUIDs).
	 * 
	 */
	public void convertToUUIDStorage() {
		
		// Run async to prevent load-time problems.
		plugin.getServer().getScheduler()
				.runTaskAsynchronously(plugin, new Runnable() {

					@Override
					public void run() {

						
						// Before running, backup stuff.
						plugin.getBackupManager().backupFile("Data.yml");
						
						
						// First archive all names below 1
						archive(1);

						final Set<String> records = data.getKeys(false);

						final int size = records.size();

						// 9 items per second
						final int speed = 9;
						final int duration = (int) Math.floor(size / speed);
						final String timeName = getDurationString(duration);

						plugin.getLogger().warning(
								"Starting converting data.yml");
						plugin.getLogger().warning(
								"Conversion will take approx. " + timeName
										+ "( guess for your data.yml)");

						for (final String record : records) {
							// UUID contains dashes and playernames do not, so if it contains dashes
							// it is probably a UUID and thus we should skip it.
							if (record.contains("-"))
								continue;

							final UUID uuid = UUIDManager
									.getUUIDFromPlayer(record);

							// Could not convert this name to uuid
							if (uuid == null) {
								plugin.getLogger().severe(
										"Could not find UUID of " + record);
								continue;
							}

							// Get the time that player has played.
							final int minutesPlayed = data.getInt(record, 0);

							// Remove the data from the file.
							data.set(record, null);

							// Add new data (in UUID form to the file)
							data.set(uuid.toString(), minutesPlayed);
						}

						save();

						plugin.getLogger().info(
								"Converted data.yml to UUID format");
					}

				});
	}

	public Autorank getAutorank() {
		return plugin;
	}

	private String getDurationString(int seconds) {

		final int hours = seconds / 3600;
		final int minutes = (seconds % 3600) / 60;
		seconds = seconds % 60;

		final StringBuilder builder = new StringBuilder("");

		if (hours > 0) {
			if (hours == 1) {
				builder.append("1 hour");
			} else {
				builder.append(hours + " hours");
			}

			if (minutes > 0 || seconds > 0) {
				builder.append(", ");
			} else {
				builder.append(".");
			}

		}

		if (minutes > 0) {
			if (minutes == 1) {
				builder.append("1 minute");
			} else {
				builder.append(minutes + " minutes");
			}

			if (seconds > 0) {
				builder.append(" and ");
			} else {
				builder.append(".");
			}
		}

		if (seconds > 0) {
			if (seconds == 1) {
				builder.append("1 second");
			} else {
				builder.append(seconds + " seconds");
			}
			builder.append(".");
		}

		return builder.toString();
	}

	/**
	 * Returns total playtime across all servers
	 * (Multiple servers write to 1 database and get the total playtime from
	 * there)
	 * 
	 * @param uuid UUID to check for
	 * @return Global playtime across all servers or -1 if no time was found
	 */
	public int getGlobalTime(final UUID uuid) {
		if (uuid == null)
			return -1;
		return plugin.getMySQLWrapper().getDatabaseTime(uuid);
	}

	/**
	 * Returns playtime on this particular server
	 * It reads from the local data.yml
	 * 
	 * @param uuid UUID to get the time for
	 * @return play time of that account or -1 if not found.
	 */
	public int getLocalTime(final UUID uuid) {
		if (uuid == null)
			return -1;
		return data.getInt(uuid.toString(), 0);
	}

	public List<String> getPlayerKeys() {
		final List<UUID> uuids = getUUIDKeys();

		final List<String> playerNames = new ArrayList<String>();

		final Map<UUID, String> foundPlayers = UUIDManager.getPlayers(uuids);

		for (final Entry<UUID, String> entry : foundPlayers.entrySet()) {
			playerNames.add(entry.getValue());
		}

		return playerNames;
	}

	/**
	 * Get the time of a player. <br>
	 * This depends on what plugin is used to get the time from. <br>
	 * Time is seconds.
	 * 
	 * @param playerName Player to get the time for
	 * @return play time of given player or 0 if not found.
	 */
	public int getTimeOfPlayer(final String playerName) {

		int playTime = 0;

		UUID uuid = null;

		// Determine what plugin to use for getting the time.
		if (timePlugin.equals(dependency.STATS)) {
			final StatsPlugin stats = plugin.getHookedStatsPlugin();

			if (stats instanceof StatsHandler) {
				// In seconds
				playTime = ((StatsAPIHandler) plugin.getDependencyManager()
						.getDependency(dependency.STATS)).getTotalPlayTime(
						playerName, null);
			} else {

				uuid = UUIDManager.getUUIDFromPlayer(playerName);

				if (uuid == null)
					return playTime;

				// Stats not found, using Autorank's system.
				playTime = data.getInt(uuid.toString(), 0) * 60;
			}
		} else if (timePlugin.equals(dependency.ONTIME)) {
			playTime = ((OnTimeHandler) plugin.getDependencyManager()
					.getDependency(dependency.ONTIME)).getPlayTime(playerName);
			// Time is in minutes, so convert to seconds
			playTime = playTime * 60;
		} else {

			uuid = UUIDManager.getUUIDFromPlayer(playerName);

			if (uuid == null)
				return playTime;

			// Use internal system of Autorank.
			playTime = data.getInt(uuid.toString(), 0) * 60;
		}

		return playTime;
	}

	public List<UUID> getUUIDKeys() {

		final List<UUID> uuids = new ArrayList<UUID>();

		for (final String uuidString : data.getKeys(false)) {
			UUID uuid = null;
			try {
				uuid = UUID.fromString(uuidString);
			} catch (final IllegalArgumentException e) {
				/*plugin.getLogger().severe(
						"Player '" + uuidString + "' is not converted yet!");*/
				continue;
			}

			// Invalid uuid
			if (uuid == null) {
				/*plugin.getLogger().severe(
						"Player '" + uuidString + "' is not converted yet!");*/
				continue;
			}

			uuids.add(uuid);
		}

		return uuids;
	}

	public void importData() {
		data.reload();
	}

	public boolean isMySQLEnabled() {
		return plugin.getMySQLWrapper().isMySQLEnabled();
	}

	public void modifyGlobalTime(final UUID uuid, final int timeDifference)
			throws IllegalArgumentException {
		// Check for MySQL
		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			try {
				throw new SQLException(
						"MySQL database is not enabled so you can't modify database!");
			} catch (final SQLException e) {
				e.printStackTrace();
				return;
			}
		}

		final int time = getGlobalTime(uuid);

		if (time >= 0) {
			try {
				setGlobalTime(uuid, time + timeDifference);
			} catch (final SQLException e) {
				e.printStackTrace();
				return;
			}
		} else {
			// First entry.
			try {
				setGlobalTime(uuid, timeDifference);
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void modifyLocalTime(final UUID uuid, final int timeDifference)
			throws IllegalArgumentException {

		final int time = this.getLocalTime(uuid);

		if (time >= 0) {
			setLocalTime(uuid, time + timeDifference);
		}
	}

	public void save() {
		data.save();
	}

	public void setGlobalTime(final UUID uuid, final int time)
			throws SQLException {
		// Check for MySQL
		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			throw new SQLException(
					"MySQL database is not enabled so you can't set items to it!");
		}

		plugin.getMySQLWrapper().setGlobalTime(uuid, time);
	}

	public void setLocalTime(final UUID uuid, final int time) {
		data.set(uuid.toString(), time);
	}
}
