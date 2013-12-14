package me.armar.plugins.autorank.playtimes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

public class Playtimes {

	public static int INTERVAL_MINUTES = 5;

	private final SimpleYamlConfiguration data;
	private final PlaytimesSave save;
	private final PlaytimesUpdate update;
	private final Autorank plugin;

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

	}

	/**
	 * Returns playtime on this particular server
	 * It reads from the local data.yml
	 * 
	 * @param name Player to check for
	 * @return Local server playtime
	 */
	public int getLocalTime(final String name) {
		// This is done on purpose, for future work
		return data.getInt(name.toLowerCase(), 0);
	}

	/**
	 * Returns total playtime across all servers
	 * (Multiple servers write to 1 database and get the total playtime from
	 * there)
	 * 
	 * @param name Player to check for
	 * @return Global playtime across all servers or -1 if no time was found
	 */
	public int getGlobalTime(final String name) {
		return plugin.getMySQLWrapper().getDatabaseTime(name);
	}

	public void importData() {
		data.reload();
	}

	public void setLocalTime(final String name, final int time) {
		data.set(name.toLowerCase(), time);
	}

	public void setGlobalTime(final String name, final int time)
			throws SQLException {
		// Check for MySQL
		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			throw new SQLException(
					"MySQL database is not enabled so you can't set items to it!");
		}

		plugin.getMySQLWrapper().setGlobalTime(name, time);
	}

	public void modifyLocalTime(final String name, final int timeDifference)
			throws IllegalArgumentException {
		final int time = data.getInt(name, -1);
		if (time >= 0) {
			setLocalTime(name, time + timeDifference);
		} else {
			throw new IllegalArgumentException("No data stored for player");
		}
	}

	public void modifyGlobalTime(final String name, final int timeDifference)
			throws IllegalArgumentException {
		// Check for MySQL
		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			try {
				throw new SQLException(
						"MySQL database is not enabled so you can't modify database!");
			} catch (final SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}

		final int time = getGlobalTime(name);

		if (time >= 0) {
			try {
				setGlobalTime(name, time + timeDifference);
			} catch (final SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		} else {
			// First entry.
			try {
				setGlobalTime(name, timeDifference);
			} catch (final SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isMySQLEnabled() {
		return plugin.getMySQLWrapper().isMySQLEnabled();
	}

	public Set<String> getKeys() {
		return data.getKeys(false);
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
		final Object[] objectArray = getKeys().toArray();
		final List<String> records = new ArrayList<String>();

		// Convert ObjectArray to List of Strings
		for (final Object object : objectArray) {
			final String record = (String) object;

			records.add(record);
		}
		// Keep a counter of archived items
		int counter = 0;

		for (final String record : records) {
			final int time = getLocalTime(record);

			// Found a record to be archived
			if (time < minimum) {
				counter++;

				// Remove record
				data.set(record, null);
			}
		}

		save();
		return counter;
	}
}
