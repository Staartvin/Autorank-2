package me.armar.plugins.autorank.data.flatfile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SimpleYamlConfiguration;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playtimes.PlaytimeManager;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

/**
 * This class is used for getting and setting play time data of players.
 * 
 * @author "Staartvin"
 *
 */
/**
 * Type a nice description here
 *
 */
public class FlatFileManager {

	private Autorank plugin;

	/**
	 * This enum represents a specific time type (daily time, monthly time,
	 * etc.)
	 */
	public static enum TimeType {
		DAILY_TIME, MONTHLY_TIME, TOTAL_TIME, WEEKLY_TIME
	}

	public static HashMap<TimeType, String> dataTypePaths = new HashMap<>();

	private final HashMap<TimeType, SimpleYamlConfiguration> dataFiles = new HashMap<TimeType, SimpleYamlConfiguration>();

	public FlatFileManager(Autorank instance) {
		this.plugin = instance;

		// Load files
		this.loadDataFiles();
		// Then register tasks
		this.registerTasks();
	}

	/**
	 * Load all the data files (daily time, weekly time, etc.).
	 */
	public void loadDataFiles() {

		dataTypePaths.put(TimeType.TOTAL_TIME, "/data/Total_time.yml");
		dataTypePaths.put(TimeType.DAILY_TIME, "/data/Daily_time.yml");
		dataTypePaths.put(TimeType.WEEKLY_TIME, "/data/Weekly_time.yml");
		dataTypePaths.put(TimeType.MONTHLY_TIME, "/data/Monthly_time.yml");

		dataFiles.put(TimeType.TOTAL_TIME,
				new SimpleYamlConfiguration(plugin, dataTypePaths.get(TimeType.TOTAL_TIME), "Total data"));
		dataFiles.put(TimeType.DAILY_TIME,
				new SimpleYamlConfiguration(plugin, dataTypePaths.get(TimeType.DAILY_TIME), "Daily data"));
		dataFiles.put(TimeType.WEEKLY_TIME,
				new SimpleYamlConfiguration(plugin, dataTypePaths.get(TimeType.WEEKLY_TIME), "Weekly data"));
		dataFiles.put(TimeType.MONTHLY_TIME,
				new SimpleYamlConfiguration(plugin, dataTypePaths.get(TimeType.MONTHLY_TIME), "Monthly data"));
	}

	/**
	 * Register tasks for saving and updating time of players.
	 */
	public void registerTasks() {
		// Run save task every 30 seconds
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			public void run() {
				saveFiles();
			}
		}, 20L, 1200L);

		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new UpdatePlaytime(this, plugin),
				PlaytimeManager.INTERVAL_MINUTES * 20 * 60, PlaytimeManager.INTERVAL_MINUTES * 20 * 60);
	}

	/**
	 * Save all data files.
	 */
	public void saveFiles() {
		for (final Entry<TimeType, SimpleYamlConfiguration> entry : dataFiles.entrySet()) {
			entry.getValue().saveFile();
		}
	}

	/**
	 * Check whether all the data files are still correct or if they should be
	 * reset. Autorank stores what values were previously found for the day,
	 * week and month and compares these to the current values.
	 * If a new day has arrived, the daily time file has to be reset.
	 */
	public void doCalendarCheck() {
		// Check if all data files are still up to date.
		// Check if daily, weekly or monthly files should be reset.

		final Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		for (final TimeType type : TimeType.values()) {
			if (this.shouldResetDatafile(type)) {

				// We should reset it now, it has expired.
				this.resetDatafile(type);

				int value = 0;
				if (type == TimeType.DAILY_TIME) {
					value = cal.get(Calendar.DAY_OF_WEEK);

					if (plugin.getConfigHandler().shouldBroadcastDataReset()) {
						// Should we broadcast the reset?
						plugin.getServer().broadcastMessage(Lang.RESET_DAILY_TIME.getConfigValue());
					}

				} else if (type == TimeType.WEEKLY_TIME) {
					value = cal.get(Calendar.WEEK_OF_YEAR);

					if (plugin.getConfigHandler().shouldBroadcastDataReset()) {
						// Should we broadcast the reset?

						plugin.getServer().broadcastMessage(Lang.RESET_WEEKLY_TIME.getConfigValue());
					}
				} else if (type == TimeType.MONTHLY_TIME) {
					value = cal.get(Calendar.MONTH);

					if (plugin.getConfigHandler().shouldBroadcastDataReset()) {
						// Should we broadcast the reset?

						plugin.getServer().broadcastMessage(Lang.RESET_MONTHLY_TIME.getConfigValue());
					}
				}

				// Update tracked data type
				plugin.getInternalPropertiesConfig().setTrackedTimeType(type, value);
				// We reset leaderboard time so it refreshes again.
				plugin.getInternalPropertiesConfig().setLeaderboardLastUpdateTime(0);

				// Update leaderboard of reset time
				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
					public void run() {
						plugin.getLeaderboardManager().updateLeaderboard(type);
					}
				});
			}
		}
	}

	/**
	 * Get a data file for a specific time type.
	 * @param type Type of time
	 * @return a data file where the given time type is stored.
	 */
	public SimpleYamlConfiguration getDataFile(final TimeType type) {
		return dataFiles.get(type);
	}

	/**
	 * Set the local play time of a player.
	 * @param type Type of time
	 * @param value Value (in minutes) to set the play time to.
	 * @param uuid UUID of the player
	 */
	public void setLocalTime(final TimeType type, final int value, final UUID uuid) {
		// Set time of a player of a specific type

		final SimpleYamlConfiguration data = this.getDataFile(type);

		data.set(uuid.toString(), value);
	}

	/**
	 * Check whether Autorank should reset a specific data file.
	 * @param type Type of time
	 * @return true if Autorank should reset the file, false otherwise.
	 */
	public boolean shouldResetDatafile(final TimeType type) {
		// Should we reset a specific data file?
		// Compare date to last date in internal properties
		final Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		if (type == TimeType.DAILY_TIME) {
			if (cal.get(Calendar.DAY_OF_WEEK) != plugin.getInternalPropertiesConfig().getTrackedTimeType(type)) {
				return true;
			}
		} else if (type == TimeType.WEEKLY_TIME) {
			if (cal.get(Calendar.WEEK_OF_YEAR) != plugin.getInternalPropertiesConfig().getTrackedTimeType(type)) {
				return true;
			}
		} else if (type == TimeType.MONTHLY_TIME) {
			if (cal.get(Calendar.MONTH) != plugin.getInternalPropertiesConfig().getTrackedTimeType(type)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Add local play time of a player to the currently stored play time.
	 * @param uuid UUID of the player
	 * @param timeDifference Time (in minutes) to add
	 * @param type Type of time
	 */
	public void addLocalTime(final UUID uuid, final int timeDifference, final TimeType type) {

		final int time = this.getLocalTime(type, uuid);

		if (time >= 0) {
			setLocalTime(type, time + timeDifference, uuid);
		}
	}

	/**
	 * Archive old records. Records below the minimum value will be removed because
	 * they are 'inactive'.
	 * 
	 * @param minimum Lowest threshold to check for
	 * @return Number of records that were removed
	 */
	public int archive(final int minimum) {
		// Keep a counter of archived items
		int counter = 0;

		for (final UUID uuid : getUUIDKeys(TimeType.TOTAL_TIME)) {
			final int time = this.getLocalTime(TimeType.TOTAL_TIME, uuid);

			// Found a record to be archived
			if (time < minimum) {
				counter++;

				final SimpleYamlConfiguration data = this.getDataFile(TimeType.TOTAL_TIME);
				// Remove record
				data.set(uuid.toString(), null);
			}
		}

		saveFiles();
		return counter;
	}

	/**
	 * Get a list of all the player names that are stored in a data file
	 * @param type Type of time.
	 * @return a list of names of players that are stored in the given data file.
	 */
	public List<String> getPlayerKeys(final TimeType type) {
		final List<UUID> uuids = getUUIDKeys(type);

		final List<String> playerNames = new ArrayList<String>();

		final Map<UUID, String> foundPlayers = UUIDManager.getPlayers(uuids);

		for (final Entry<UUID, String> entry : foundPlayers.entrySet()) {
			playerNames.add(entry.getValue());
		}

		return playerNames;
	}

	
	/**
	 * Get the local play time of a player on this server as stored by Autorank.
	 * 
	 * @param uuid UUID of the player
	 * @param type Type of time
	 * @return play time of that player or 0 if not found.
	 */
	public int getLocalTime(final TimeType type, final UUID uuid) {
		// Get time of a player with specific type
		final SimpleYamlConfiguration data = this.getDataFile(type);

		return data.getInt(uuid.toString(), 0);
	}

	/**
	 * Reset the data file of certain time type.
	 * @param type Type of time
	 */
	public void resetDatafile(final TimeType type) {
		final SimpleYamlConfiguration data = this.getDataFile(type);

		plugin.debugMessage("Resetting data file '" + type + "'!");

		// Delete file
		final boolean deleted = data.getInternalFile().delete();

		// Don't create a new file if it wasn't deleted in the first place.
		if (!deleted) {
			plugin.debugMessage("Tried deleting data file, but could not delete!");
			return;
		}

		// Create a new file so it's empty
		if (type == TimeType.DAILY_TIME) {
			dataFiles.put(TimeType.DAILY_TIME,
					new SimpleYamlConfiguration(plugin, dataTypePaths.get(TimeType.DAILY_TIME), "Daily data"));
		} else if (type == TimeType.WEEKLY_TIME) {
			dataFiles.put(TimeType.WEEKLY_TIME,
					new SimpleYamlConfiguration(plugin, dataTypePaths.get(TimeType.WEEKLY_TIME), "Weekly data"));
		} else if (type == TimeType.MONTHLY_TIME) {
			dataFiles.put(TimeType.MONTHLY_TIME,
					new SimpleYamlConfiguration(plugin, dataTypePaths.get(TimeType.MONTHLY_TIME), "Monthly data"));
		} else if (type == TimeType.TOTAL_TIME) {
			dataFiles.put(TimeType.TOTAL_TIME,
					new SimpleYamlConfiguration(plugin, dataTypePaths.get(TimeType.TOTAL_TIME), "Total data"));
		}
	}
	
	/**
	 * Get a list of all the player UUIDs that are stored in a data file
	 * @param type Type of time.
	 * @return a list of UUIDs of players that are stored in the given data file.
	 */
	public List<UUID> getUUIDKeys(final TimeType type) {

		final List<UUID> uuids = new ArrayList<UUID>();

		final SimpleYamlConfiguration data = this.getDataFile(type);

		for (final String uuidString : data.getKeys(false)) {
			UUID uuid = null;
			try {
				uuid = UUID.fromString(uuidString);
			} catch (final IllegalArgumentException e) {
				continue;
			}

			// Invalid uuid
			if (uuid == null) {
				continue;
			}

			uuids.add(uuid);
		}

		return uuids;
	}

	/**
	 * Import total play time from the current {@link TimeType.TOTAL_TIME} data file.
	 */
	public void importData() {
		final SimpleYamlConfiguration data = this.getDataFile(TimeType.TOTAL_TIME);
		data.reloadFile();
	}

}
