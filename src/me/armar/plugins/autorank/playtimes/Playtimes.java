package me.armar.plugins.autorank.playtimes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

public class Playtimes {

	public static int INTERVAL_MINUTES;

	private final Autorank plugin;
	private final PlaytimesSave save;
	// Used to store what plugin Autorank uses for checking the time
	private final dependency timePlugin;

	// Autorank keeps track of total time, time online on one day, time online
	// in a week and time online in a month.
	// There are all tracked in minutes.
	public static enum dataType {
		TOTAL_TIME, DAILY_TIME, WEEKLY_TIME, MONTHLY_TIME
	};

	private final HashMap<dataType, SimpleYamlConfiguration> dataFiles = new HashMap<dataType, SimpleYamlConfiguration>();

	private final PlaytimesUpdate update;

	public Playtimes(final Autorank plugin) {
		this.plugin = plugin;

		INTERVAL_MINUTES = plugin.getConfigHandler().getIntervalTime();

		plugin.getLogger().info("Interval check every " + INTERVAL_MINUTES + " minutes.");

		dataFiles.put(dataType.TOTAL_TIME, new SimpleYamlConfiguration(plugin, "Data.yml", null, "Total data"));
		dataFiles.put(dataType.DAILY_TIME,
				new SimpleYamlConfiguration(plugin, "/data/daily_time.yml", null, "Daily data"));
		dataFiles.put(dataType.WEEKLY_TIME,
				new SimpleYamlConfiguration(plugin, "/data/weekly_time.yml", null, "Weekly data"));
		dataFiles.put(dataType.MONTHLY_TIME,
				new SimpleYamlConfiguration(plugin, "/data/monthly_time.yml", null, "Monthly data"));
		// this.data = new SimpleYamlConfiguration(plugin, "Data.yml", null,
		// "Data");

		this.save = new PlaytimesSave(this);
		this.update = new PlaytimesUpdate(this, plugin);

		// Run save task every 30 seconds
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, save, 20L, 1200L);

		// Run update timer every x minutes
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, update, INTERVAL_MINUTES * 20 * 60,
				INTERVAL_MINUTES * 20 * 60);

		timePlugin = plugin.getConfigHandler().useTimeOf();
	}

	/**
	 * Archive old records. Records below the minimum will be removed because
	 * they are 'inactive'.
	 * 
	 * @param minimum
	 *            Lowest threshold to check for
	 * @return Amount of records removed
	 */
	public int archive(final int minimum) {
		// Keep a counter of archived items
		int counter = 0;

		for (final UUID uuid : getUUIDKeys(dataType.TOTAL_TIME)) {
			final int time = this.getLocalTime(uuid);

			// Found a record to be archived
			if (time < minimum) {
				counter++;

				final SimpleYamlConfiguration data = this.getDataFile(dataType.TOTAL_TIME);
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
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {

				final SimpleYamlConfiguration data = getDataFile(dataType.TOTAL_TIME);

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

				save();

				plugin.getLogger().info("Converted data.yml to UUID format");
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
	 * Returns total playtime across all servers (Multiple servers write to 1
	 * database and get the total playtime from there)
	 * 
	 * @param uuid
	 *            UUID to check for
	 * @return Global playtime across all servers or -1 if no time was found
	 */
	public int getGlobalTime(final UUID uuid) {
		if (uuid == null)
			return -1;
		return plugin.getMySQLWrapper().getDatabaseTime(uuid);
	}

	private int getFreshGlobalTime(final UUID uuid) {
		if (uuid == null)
			return -1;
		return plugin.getMySQLWrapper().getFreshDatabaseTime(uuid);
	}

	/**
	 * Returns playtime on this particular server It reads from the local
	 * data.yml
	 * 
	 * @param uuid
	 *            UUID to get the time for
	 * @return play time of that account or -1 if not found.
	 */
	public int getLocalTime(final UUID uuid) {
		if (uuid == null)
			return -1;

		final SimpleYamlConfiguration data = this.getDataFile(dataType.TOTAL_TIME);

		return data.getInt(uuid.toString(), 0);
	}

	public List<String> getPlayerKeys(final dataType type) {
		final List<UUID> uuids = getUUIDKeys(type);

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
		if (timePlugin.equals(dependency.STATS)) {
			final StatsPlugin stats = plugin.getHookedStatsPlugin();

			if (stats instanceof StatsHandler) {
				// In seconds
				playTime = ((StatsAPIHandler) plugin.getDependencyManager().getDependency(dependency.STATS))
						.getTotalPlayTime(uuid, null);
			} else {

				if (uuid == null)
					return playTime;

				// Stats not found, using Autorank's system.
				playTime = this.getLocalTime(uuid) * 60;
			}
		} else if (timePlugin.equals(dependency.ONTIME)) {
			playTime = ((OnTimeHandler) plugin.getDependencyManager().getDependency(dependency.ONTIME))
					.getPlayTime(playerName);
			// Time is in minutes, so convert to seconds
			playTime = playTime * 60;
		} else {

			if (uuid == null)
				return playTime;

			// Use internal system of Autorank.
			playTime = this.getLocalTime(uuid) * 60;
		}

		return playTime;
	}

	public List<UUID> getUUIDKeys(final dataType type) {

		final List<UUID> uuids = new ArrayList<UUID>();

		final SimpleYamlConfiguration data = this.getDataFile(type);

		for (final String uuidString : data.getKeys(false)) {
			UUID uuid = null;
			try {
				uuid = UUID.fromString(uuidString);
			} catch (final IllegalArgumentException e) {
				/*
				 * plugin.getLogger().severe( "Player '" + uuidString +
				 * "' is not converted yet!");
				 */
				continue;
			}

			// Invalid uuid
			if (uuid == null) {
				/*
				 * plugin.getLogger().severe( "Player '" + uuidString +
				 * "' is not converted yet!");
				 */
				continue;
			}

			uuids.add(uuid);
		}

		return uuids;
	}

	public void importData() {
		final SimpleYamlConfiguration data = this.getDataFile(dataType.TOTAL_TIME);
		data.reload();
	}

	public boolean isMySQLEnabled() {
		return plugin.getMySQLWrapper().isMySQLEnabled();
	}

	public void modifyGlobalTime(final UUID uuid, final int timeDifference) throws IllegalArgumentException {
		// Check for MySQL
		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			try {
				throw new SQLException("MySQL database is not enabled so you can't modify database!");
			} catch (final SQLException e) {
				e.printStackTrace();
				return;
			}
		}

		final int time = getFreshGlobalTime(uuid);

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

	public void modifyLocalTime(final UUID uuid, final int timeDifference) throws IllegalArgumentException {

		final int time = this.getLocalTime(uuid);

		if (time >= 0) {
			setLocalTime(uuid, time + timeDifference);
		}
	}

	public void save() {
		for (final Entry<dataType, SimpleYamlConfiguration> entry : dataFiles.entrySet()) {
			entry.getValue().save();
		}
	}

	public void setGlobalTime(final UUID uuid, final int time) throws SQLException {
		// Check for MySQL
		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			throw new SQLException("MySQL database is not enabled so you can't set items to it!");
		}

		plugin.getMySQLWrapper().setGlobalTime(uuid, time);
	}

	public void setLocalTime(final UUID uuid, final int time) {
		final SimpleYamlConfiguration data = this.getDataFile(dataType.TOTAL_TIME);
		data.set(uuid.toString(), time);
	}

	public SimpleYamlConfiguration getDataFile(final dataType type) {
		return dataFiles.get(type);
	}

	public void setTime(final dataType type, final int value, final UUID uuid) {
		// Set time of a player of a specific type

		final SimpleYamlConfiguration data = this.getDataFile(type);

		data.set(uuid.toString(), value);
	}

	public int getTime(final dataType type, final UUID uuid) {
		// Get time of a player with specific type
		final SimpleYamlConfiguration data = this.getDataFile(type);

		return data.getInt(uuid.toString(), 0);
	}

	public boolean shouldResetDatafile(final dataType type) {
		// Should we reset a specific data file?
		// Compare date to last date in internal properties
		final Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		if (type == dataType.DAILY_TIME) {
			if (cal.get(Calendar.DAY_OF_WEEK) != plugin.getInternalProps().getTrackedDataType(type)) {
				return true;
			}
		} else if (type == dataType.WEEKLY_TIME) {
			if (cal.get(Calendar.WEEK_OF_YEAR) != plugin.getInternalProps().getTrackedDataType(type)) {
				return true;
			}
		} else if (type == dataType.MONTHLY_TIME) {
			if (cal.get(Calendar.MONTH) != plugin.getInternalProps().getTrackedDataType(type)) {
				return true;
			}
		}

		return false;
	}

	public void resetDatafile(final dataType type) {
		final SimpleYamlConfiguration data = this.getDataFile(type);

		plugin.debugMessage("Resetting data file '" + type + "'!");

		// Delete file
		final boolean deleted = data.getInternalFile().delete();

		// Don't create a new file if it wasn't deleted in the first place.
		if (!deleted)
			return;

		// Create a new file so it's empty
		if (type == dataType.DAILY_TIME) {
			dataFiles.put(dataType.DAILY_TIME,
					new SimpleYamlConfiguration(plugin, "/data/daily_time.yml", null, "Daily data"));
		} else if (type == dataType.WEEKLY_TIME) {
			dataFiles.put(dataType.WEEKLY_TIME,
					new SimpleYamlConfiguration(plugin, "/data/weekly_time.yml", null, "Weekly data"));
		} else if (type == dataType.MONTHLY_TIME) {
			dataFiles.put(dataType.MONTHLY_TIME,
					new SimpleYamlConfiguration(plugin, "/data/monthly_time.yml", null, "Monthly data"));
		} else if (type == dataType.TOTAL_TIME) {
			dataFiles.put(dataType.TOTAL_TIME, new SimpleYamlConfiguration(plugin, "Data.yml", null, "Total data"));
		}
	}

	public void modifyTime(final UUID uuid, final int timeDifference, final dataType type) {

		final int time = this.getTime(type, uuid);

		if (time >= 0) {
			setTime(type, time + timeDifference, uuid);
		}
	}

	public void doCalendarCheck() {
		// Check if all data files are still up to date.
		// Check if daily, weekly or monthly files should be reset.

		final Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		for (final dataType type : Playtimes.dataType.values()) {
			if (plugin.getPlaytimes().shouldResetDatafile(type)) {
				// We should reset it now, it has expired.
				plugin.getPlaytimes().resetDatafile(type);

				int value = 0;
				if (type == dataType.DAILY_TIME) {
					value = cal.get(Calendar.DAY_OF_WEEK);

					if (plugin.getConfigHandler().shouldBroadcastDataReset()) {
						// Should we broadcast the reset?

						plugin.getServer().broadcastMessage(Lang.RESET_DAILY_TIME.getConfigValue());
					}

				} else if (type == dataType.WEEKLY_TIME) {
					value = cal.get(Calendar.WEEK_OF_YEAR);

					if (plugin.getConfigHandler().shouldBroadcastDataReset()) {
						// Should we broadcast the reset?

						plugin.getServer().broadcastMessage(Lang.RESET_WEEKLY_TIME.getConfigValue());
					}
				} else if (type == dataType.MONTHLY_TIME) {
					value = cal.get(Calendar.MONTH);

					if (plugin.getConfigHandler().shouldBroadcastDataReset()) {
						// Should we broadcast the reset?

						plugin.getServer().broadcastMessage(Lang.RESET_MONTHLY_TIME.getConfigValue());
					}
				}

				// Update tracked data type
				plugin.getInternalProps().setTrackedDataType(type, value);
				// We reset leaderboard time so it refreshes again.
				plugin.getInternalProps().setLeaderboardLastUpdateTime(0);
			}
		}
	}
}
