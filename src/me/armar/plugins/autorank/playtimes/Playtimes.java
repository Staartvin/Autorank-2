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
	private final PlaytimesSave save;
	private final PlaytimesUpdate update;
	private final Autorank plugin;

	// Used to store what plugin Autorank uses for checking the time
	private dependency timePlugin;

	public Playtimes(final Autorank plugin) {
		this.plugin = plugin;

		INTERVAL_MINUTES = plugin.getAdvancedConfig().getInt("interval check",
				5);
		plugin.getLogger().info(
				"Interval check every " + INTERVAL_MINUTES + " minutes.");

		this.data = new SimpleYamlConfiguration(plugin, "Data.yml", null,
				"Data");
		this.save = new PlaytimesSave(this);
		this.update = new PlaytimesUpdate(this, plugin);

		plugin.getServer().getScheduler()
				.runTaskTimer(plugin, save, 12000, 12000);
		plugin.getServer().getScheduler().runTaskTimer(plugin, save, 600, 600);
		plugin.getServer()
				.getScheduler()
				.runTaskTimer(plugin, update, INTERVAL_MINUTES * 20 * 60,
						INTERVAL_MINUTES * 20 * 60);

		timePlugin = plugin.getConfigHandler().useTimeOf();
	}
	
	/**
	 * Returns playtime on this particular server
	 * It reads from the local data.yml
	 * @param uuid UUID to get the time for
	 * @return play time of that account or -1 if not found.
	 */
	public int getLocalTime(UUID uuid) {
		if (uuid == null) return -1;
		return data.getInt(uuid.toString(), 0);
	}
	
	/**
	 * Get the time of a player. <br>
	 * This depends on what plugin is used to get the time from.
	 * 
	 * @param playerName Player to get the time for
	 * @return play time of given player or 0 if not found.
	 */
	public int getTimeOfPlayer(String playerName) {

		int playTime = 0;

		UUID uuid = null;

		// Determine what plugin to use for getting the time.
		if (timePlugin.equals(dependency.STATS)) {
			StatsPlugin stats = plugin.getHookedStatsPlugin();

			if (stats instanceof StatsHandler) {
				playTime = ((StatsAPIHandler) plugin.getDependencyManager()
						.getDependency(dependency.STATS)).getTotalPlayTime(
						playerName, null);
			} else {
				
				uuid = UUIDManager.getUUIDFromPlayer(playerName);
				
				if (uuid == null)
					return playTime;
				
				// Stats not found, using Autorank's system.
				playTime = data.getInt(uuid.toString(), 0);
			}
		} else if (timePlugin.equals(dependency.ONTIME)) {
			playTime = ((OnTimeHandler) plugin.getDependencyManager()
					.getDependency(dependency.ONTIME)).getPlayTime(playerName);
		} else {

			uuid = UUIDManager.getUUIDFromPlayer(playerName);
			
			if (uuid == null)
				return playTime;

			// Use internal system of Autorank.
			playTime = data.getInt(uuid.toString(), 0);
		}

		return playTime;
	}

	
	/**
	 * Returns total playtime across all servers
	 * (Multiple servers write to 1 database and get the total playtime from
	 * there)
	 * 
	 * @param uuid UUID to check for
	 * @return Global playtime across all servers or -1 if no time was found
	 */
	public int getGlobalTime(UUID uuid) {
		if (uuid == null) return -1;
		return plugin.getMySQLWrapper().getDatabaseTime(uuid);
	}

	public void importData() {
		data.reload();
	}
	
	public void setLocalTime(UUID uuid, int time) {
		data.set(uuid.toString(), time);
	}
	
	public void setGlobalTime(UUID uuid, final int time)
			throws SQLException {
		// Check for MySQL
		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			throw new SQLException(
					"MySQL database is not enabled so you can't set items to it!");
		}

		plugin.getMySQLWrapper().setGlobalTime(uuid, time);
	}
	
	public void modifyLocalTime(UUID uuid, final int timeDifference)
			throws IllegalArgumentException {

		final int time = this.getLocalTime(uuid);

		if (time >= 0) {
			setLocalTime(uuid, time + timeDifference);
		}
	}
	
	public void modifyGlobalTime(UUID uuid, final int timeDifference)
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

	public boolean isMySQLEnabled() {
		return plugin.getMySQLWrapper().isMySQLEnabled();
	}

	public List<UUID> getUUIDKeys() {
		List<UUID> uuids = new ArrayList<UUID>();

		for (String uuidString : data.getKeys(false)) {
			uuids.add(UUID.fromString(uuidString));
		}

		return uuids;
	}

	public List<String> getPlayerKeys() {
		List<UUID> uuids = getUUIDKeys();

		List<String> playerNames = new ArrayList<String>();

		Map<UUID, String> foundPlayers = UUIDManager.getPlayers(
				uuids);

		for (Entry<UUID, String> entry : foundPlayers.entrySet()) {
			playerNames.add(entry.getValue());
		}

		return playerNames;
	}

	public void save() {
		data.save();
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

		for (UUID uuid: getUUIDKeys()) {
			int time = this.getLocalTime(uuid);

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

		Set<String> records = data.getKeys(false);

		for (String record : records) {
			// UUID contains dashes and playernames do not, so if it contains dashes
			// it is probably a UUID and thus we should skip it.
			if (record.contains("-"))
				continue;

			UUID uuid = UUIDManager.getUUIDFromPlayer(record);

			// Could not convert this name to uuid
			if (uuid == null)
				continue;

			// Get the time that player has played.
			int minutesPlayed = data.getInt(record);

			// Remove the data from the file.
			data.set(record, null);

			// Add new data (in UUID form to the file)
			data.set(uuid.toString(), minutesPlayed);
		}
	}
}
