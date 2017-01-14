package me.armar.plugins.autorank.config;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.dataType;

/**
 * This class manages the internalprop.yml that stores all internal properties
 * <br>
 * of Autorank. Think of the cached leaderboard, the last time something was
 * updated, etc.
 * <br>
 * This file should not be altered by the server owner, since Autorank manages
 * this.
 * 
 * @author Staartvin
 *
 */
public class InternalPropertiesConfig {

	private final Autorank plugin;
	private SimpleYamlConfiguration config;

	public InternalPropertiesConfig(final Autorank instance) {
		this.plugin = instance;
	}

	public List<String> getCachedLeaderboard(final dataType type) {
		// Type is the leaderboard type you want to get (all time, daily, weekly or monthly)
		return config.getStringList("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard");
	}

	public long getLeaderboardLastUpdateTime() {
		return config.getLong("leaderboard last updated", 0);
	}

	public int getTrackedDataType(final dataType type) {
		if (type == dataType.DAILY_TIME) {
			return config.getInt("tracked day", 1);
		} else if (type == dataType.WEEKLY_TIME) {
			return config.getInt("tracked week", 1);
		} else if (type == dataType.MONTHLY_TIME) {
			return config.getInt("tracked month", 1);
		} else {
			return 0;
		}
	}

	public boolean hasTransferredUUIDs() {
		// Since Autorank 3.7.1, a new format of storing player names was
		// introduced. If all values were properly converted, this method will
		// return true.
		// If it hasn't been run before or did not successfully convert all
		// names, it will return false.

		return config.getBoolean("has converted uuids", false);
	}

	public void hasTransferredUUIDs(final boolean value) {
		config.set("has converted uuids", true);

		config.saveFile();
	}

	public void loadFile() {
		config = new SimpleYamlConfiguration(plugin, "internalprops.yml", "Internal properties");

		config.options()
				.header("This is the internal properties file of Autorank. \nYou should not touch any values here, unless instructed by a developer."
						+ "\nAutorank uses these to keep track of certain aspects of the plugin.");

		config.addDefault("leaderboard last updated", 0); // When was the
															// leaderboard
															// updated for last
															// time? In UNIX
															// time.
		config.addDefault("has converted uuids", false); // Did it already
														// convert uuids?

		config.addDefault("tracked month", 1); // This is used to keep track of what month we are checking the data for. If this is changed, the montly_data.yml gets reset.
		config.addDefault("tracked week", 1); // This is used to keep track of what week we are checking the data for. If this is changed, the weekly_data.yml gets reset.
		config.addDefault("tracked day", 1); // This is used to keep track of what day we are checking the data for. If this is changed, the daily_data.yml gets reset.

		final List<String> newList = new ArrayList<String>();
		newList.add("&cThis leaderboard wasn't set up yet.");

		config.addDefault("leaderboards.total_time.cached leaderboard", newList);
		config.addDefault("leaderboards.daily_time.cached leaderboard", newList);
		config.addDefault("leaderboards.weekly_time.cached leaderboard", newList);
		config.addDefault("leaderboards.monthly_time.cached leaderboard", newList);

		config.options().copyDefaults(true);

		config.saveFile();
	}

	public void setCachedLeaderboard(final dataType type, final List<String> cachedLeaderboard) {
		config.set("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard", cachedLeaderboard);

		config.saveFile();
	}

	public void setLeaderboardLastUpdateTime(final long time) {
		config.set("leaderboard last updated", time);

		config.saveFile();
	}

	public void setTrackedDataType(final dataType type, final int value) {
		if (type == dataType.DAILY_TIME) {
			config.set("tracked day", value);
		} else if (type == dataType.WEEKLY_TIME) {
			config.set("tracked week", value);
		} else if (type == dataType.MONTHLY_TIME) {
			config.set("tracked month", value);
		} else {
			return;
		}

		config.saveFile();
	}
}
