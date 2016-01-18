package me.armar.plugins.autorank.internalproperties;

import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.playtimes.Playtimes.dataType;

/**
 * This class manages the internalprop.yml that stores all internal properties
 * of Autorank. Think of the cached leaderboard, the last time something was
 * updated, etc.
 * 
 * @author Staartvin
 *
 */
public class InternalProperties {

	private final Autorank plugin;
	private SimpleYamlConfiguration propFile;

	public InternalProperties(Autorank instance) {
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

		propFile.options().copyDefaults(true);

		propFile.save();
	}

	public void setCachedLeaderboard(dataType type, List<String> cachedLeaderboard) {
		propFile.set("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard", cachedLeaderboard);

		propFile.save();
	}

	public List<String> getCachedLeaderboard(dataType type) {
		// Type is the leaderboard type you want to get (all time, daily, weekly or monthly)
		return propFile.getStringList("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard");
	}

	public void setLeaderboardLastUpdateTime(long time) {
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

	public void hasTransferredUUIDs(boolean value) {
		propFile.set("has converted uuids", true);

		propFile.save();
	}

	public int getTrackedDataType(dataType type) {
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
	
	public void setTrackedDataType(dataType type, int value) {
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
