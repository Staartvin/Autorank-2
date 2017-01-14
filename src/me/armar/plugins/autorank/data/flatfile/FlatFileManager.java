package me.armar.plugins.autorank.data.flatfile;

import java.sql.SQLException;
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
 * Class description
 * <p>
 * Date created: 20:27:16
 * 11 jan. 2017
 * 
 * @author "Staartvin"
 *
 */
public class FlatFileManager {

	private Autorank plugin;

	public static enum dataType {
		DAILY_TIME, MONTHLY_TIME, TOTAL_TIME, WEEKLY_TIME
	}

	public static HashMap<dataType, String> dataTypePaths = new HashMap<>();

	private final HashMap<dataType, SimpleYamlConfiguration> dataFiles = new HashMap<dataType, SimpleYamlConfiguration>();

	public FlatFileManager(Autorank instance) {
		this.plugin = instance;

		// Load files
		this.loadDataFiles();
		// Then register tasks
		this.registerTasks();
	}

	public void loadDataFiles() {

		dataTypePaths.put(dataType.TOTAL_TIME, "/data/Total_time.yml");
		dataTypePaths.put(dataType.DAILY_TIME, "/data/Daily_time.yml");
		dataTypePaths.put(dataType.WEEKLY_TIME, "/data/Weekly_time.yml");
		dataTypePaths.put(dataType.MONTHLY_TIME, "/data/Monthly_time.yml");

		dataFiles.put(dataType.TOTAL_TIME,
				new SimpleYamlConfiguration(plugin, dataTypePaths.get(dataType.TOTAL_TIME), "Total data"));
		dataFiles.put(dataType.DAILY_TIME,
				new SimpleYamlConfiguration(plugin, dataTypePaths.get(dataType.DAILY_TIME), "Daily data"));
		dataFiles.put(dataType.WEEKLY_TIME,
				new SimpleYamlConfiguration(plugin, dataTypePaths.get(dataType.WEEKLY_TIME), "Weekly data"));
		dataFiles.put(dataType.MONTHLY_TIME,
				new SimpleYamlConfiguration(plugin, dataTypePaths.get(dataType.MONTHLY_TIME), "Monthly data"));
	}

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

	public void saveFiles() {
		for (final Entry<dataType, SimpleYamlConfiguration> entry : dataFiles.entrySet()) {
			entry.getValue().saveFile();
		}
	}

	/**
	 * Checks whether all the /data files are still correct or if they should be
	 * reset.
	 */
	public void doCalendarCheck() {
		// Check if all data files are still up to date.
		// Check if daily, weekly or monthly files should be reset.

		final Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		for (final dataType type : dataType.values()) {
			if (this.shouldResetDatafile(type)) {

				// We should reset it now, it has expired.
				this.resetDatafile(type);

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
				plugin.getInternalPropertiesConfig().setTrackedDataType(type, value);
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

	public SimpleYamlConfiguration getDataFile(final dataType type) {
		return dataFiles.get(type);
	}

	public void setGlobalTime(final UUID uuid, final int time) throws SQLException {
		// Check for MySQL
		if (!plugin.getMySQLManager().isMySQLEnabled()) {
			throw new SQLException("MySQL database is not enabled so you can't set items to it!");
		}

		plugin.getMySQLManager().setGlobalTime(uuid, time);
	}

	public void setLocalTime(final dataType type, final int value, final UUID uuid) {
		// Set time of a player of a specific type

		final SimpleYamlConfiguration data = this.getDataFile(type);

		data.set(uuid.toString(), value);
	}

	public boolean shouldResetDatafile(final dataType type) {
		// Should we reset a specific data file?
		// Compare date to last date in internal properties
		final Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		if (type == dataType.DAILY_TIME) {
			if (cal.get(Calendar.DAY_OF_WEEK) != plugin.getInternalPropertiesConfig().getTrackedDataType(type)) {
				return true;
			}
		} else if (type == dataType.WEEKLY_TIME) {
			if (cal.get(Calendar.WEEK_OF_YEAR) != plugin.getInternalPropertiesConfig().getTrackedDataType(type)) {
				return true;
			}
		} else if (type == dataType.MONTHLY_TIME) {
			if (cal.get(Calendar.MONTH) != plugin.getInternalPropertiesConfig().getTrackedDataType(type)) {
				return true;
			}
		}

		return false;
	}

	public void addGlobalTime(final UUID uuid, final int timeDifference) throws IllegalArgumentException {
		// Check for MySQL
		if (!plugin.getMySQLManager().isMySQLEnabled()) {
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

	public void addLocalTime(final UUID uuid, final int timeDifference, final dataType type) {

		final int time = this.getLocalTime(type, uuid);

		if (time >= 0) {
			setLocalTime(type, time + timeDifference, uuid);
		}
	}

	public int getFreshGlobalTime(final UUID uuid) {
		if (uuid == null)
			return 0;
		return plugin.getMySQLManager().getFreshDatabaseTime(uuid);
	}

	/**
	 * Returns total playtime across all servers (Multiple servers write to 1
	 * database and get the total playtime from there)
	 * 
	 * @param uuid
	 *            UUID to check for
	 * @return Global playtime across all servers or 0 if no time was found
	 */
	public int getGlobalTime(final UUID uuid) {
		if (uuid == null)
			return 0;
		return plugin.getMySQLManager().getDatabaseTime(uuid);
	}

	/**
	 * Returns playtime on this particular server It reads from the local
	 * data.yml
	 * 
	 * @param uuid
	 *            UUID to get the time for
	 * @return play time of that account or 0 if not found.
	 */
	public int getLocalTime(final UUID uuid) {
		if (uuid == null)
			return 0;

		final SimpleYamlConfiguration data = this.getDataFile(dataType.TOTAL_TIME);

		return data.getInt(uuid.toString(), 0);
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

		saveFiles();
		return counter;
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

	public int getLocalTime(final dataType type, final UUID uuid) {
		// Get time of a player with specific type
		final SimpleYamlConfiguration data = this.getDataFile(type);

		return data.getInt(uuid.toString(), 0);
	}

	public void resetDatafile(final dataType type) {
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
		if (type == dataType.DAILY_TIME) {
			dataFiles.put(dataType.DAILY_TIME,
					new SimpleYamlConfiguration(plugin, dataTypePaths.get(dataType.DAILY_TIME), "Daily data"));
		} else if (type == dataType.WEEKLY_TIME) {
			dataFiles.put(dataType.WEEKLY_TIME,
					new SimpleYamlConfiguration(plugin, dataTypePaths.get(dataType.WEEKLY_TIME), "Weekly data"));
		} else if (type == dataType.MONTHLY_TIME) {
			dataFiles.put(dataType.MONTHLY_TIME,
					new SimpleYamlConfiguration(plugin, dataTypePaths.get(dataType.MONTHLY_TIME), "Monthly data"));
		} else if (type == dataType.TOTAL_TIME) {
			dataFiles.put(dataType.TOTAL_TIME,
					new SimpleYamlConfiguration(plugin, dataTypePaths.get(dataType.TOTAL_TIME), "Total data"));
		}
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
		data.reloadFile();
	}

}
