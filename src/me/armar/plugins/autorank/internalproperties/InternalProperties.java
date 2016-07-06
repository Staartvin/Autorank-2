package me.armar.plugins.autorank.internalproperties;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.playtimes.Playtimes.dataType;

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
public class InternalProperties {

	private final Autorank plugin;
	private SimpleYamlConfiguration propFile;

	public InternalProperties(final Autorank instance) {
		this.plugin = instance;
	}

	public void loadFile() {
		propFile = new SimpleYamlConfiguration(plugin, "internalprops.yml", null, "Internal properties");

		propFile.options()
				.header("This is the internal properties file of Autorank. \nYou should not touch any values here, unless instructed by a developer."
						+ "\nAutorank uses these to keep track of certain aspects of the plugin.");

		propFile.addDefault("leaderboard last updated", 0); // When was the
															// leaderboard
															// updated for last
															// time? In UNIX
															// time.
		propFile.addDefault("has converted uuids", false); // Did it already
															// convert uuids?

		propFile.addDefault("tracked month", 1); // This is used to keep track of what month we are checking the data for. If this is changed, the montly_data.yml gets reset.
		propFile.addDefault("tracked week", 1); // This is used to keep track of what week we are checking the data for. If this is changed, the weekly_data.yml gets reset.
		propFile.addDefault("tracked day", 1); // This is used to keep track of what day we are checking the data for. If this is changed, the daily_data.yml gets reset.

		final List<String> newList = new ArrayList<String>();
		newList.add("&cThis leaderboard wasn't set up yet.");

		propFile.addDefault("leaderboards.total_time.cached leaderboard", newList);
		propFile.addDefault("leaderboards.daily_time.cached leaderboard", newList);
		propFile.addDefault("leaderboards.weekly_time.cached leaderboard", newList);
		propFile.addDefault("leaderboards.monthly_time.cached leaderboard", newList);

		propFile.options().copyDefaults(true);

		propFile.save();
	}

	public void setCachedLeaderboard(final dataType type, final List<String> cachedLeaderboard) {
		propFile.set("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard", cachedLeaderboard);

		propFile.save();
	}

	public List<String> getCachedLeaderboard(final dataType type) {
		// Type is the leaderboard type you want to get (all time, daily, weekly or monthly)
		return propFile.getStringList("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard");
	}

	public void setLeaderboardLastUpdateTime(final long time) {
		propFile.set("leaderboard last updated", time);

		propFile.save();
	}

	public long getLeaderboardLastUpdateTime() {
		return propFile.getLong("leaderboard last updated", 0);
	}

	public boolean hasTransferredUUIDs() {
		// Since Autorank 3.7.1, a new format of storing player names was
		// introduced. If all values were properly converted, this method will
		// return true.
		// If it hasn't been run before or did not successfully convert all
		// names, it will return false.

		return propFile.getBoolean("has converted uuids", false);
	}

	public void hasTransferredUUIDs(final boolean value) {
		propFile.set("has converted uuids", true);

		propFile.save();
	}

	public int getTrackedDataType(final dataType type) {
		if (type == dataType.DAILY_TIME) {
			return propFile.getInt("tracked day", 1);
		} else if (type == dataType.WEEKLY_TIME) {
			return propFile.getInt("tracked week", 1);
		} else if (type == dataType.MONTHLY_TIME) {
			return propFile.getInt("tracked month", 1);
		} else {
			return 0;
		}
	}

	public void setTrackedDataType(final dataType type, final int value) {
		if (type == dataType.DAILY_TIME) {
			propFile.set("tracked day", value);
		} else if (type == dataType.WEEKLY_TIME) {
			propFile.set("tracked week", value);
		} else if (type == dataType.MONTHLY_TIME) {
			propFile.set("tracked month", value);
		} else {
			return;
		}

		propFile.save();
	}
}
