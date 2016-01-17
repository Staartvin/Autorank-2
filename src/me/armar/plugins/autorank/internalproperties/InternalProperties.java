package me.armar.plugins.autorank.internalproperties;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

/**
 * This class manages the internalprop.yml that stores all internal properties of Autorank.
 * Think of the cached leaderboard, the last time something was updated, etc.
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
		
		propFile.options().header("This is the internal properties file of Autorank. \nYou should not touch any values here, unless instructed by a developer."
				+ "\nAutorank uses these to keep track of certain aspects of the plugin.");
		
		propFile.addDefault("cached leaderboard", new ArrayList<String>()); // A cached version of the leaderboard.
		propFile.addDefault("leaderboard last updated", 0); // When was the leaderboard updated for last time? In UNIX time.
		propFile.addDefault("has converted uuids", false); // Did it already convert uuids?
		
		propFile.options().copyDefaults(true);
		
		propFile.save();
	}
	
	public void setCachedLeaderboard(List<String> cachedLeaderboard) {
		propFile.set("cached leaderboard", cachedLeaderboard);
		
		propFile.save();
	}
	
	public List<String> getCachedLeaderboard() {
		return propFile.getStringList("cached leaderboard");
	}
	
	public void setLeaderboardLastUpdateTime(long time) {
		propFile.set("leaderboard last updated", time);
		
		propFile.save();
	}
	
	public long getLeaderboardLastUpdateTime() {
		return propFile.getLong("leaderboard last updated", 0);
	}
	
	public boolean hasTransferredUUIDs() {
		// Since Autorank 3.7.1, a new format of storing player names was introduced. If all values were properly converted, this method will return true.
		// If it hasn't been run before or did not successfully convert all names, it will return false.
		
		return propFile.getBoolean("has converted uuids", false);
	}
	
	public void hasTransferredUUIDs(boolean value) {
		propFile.set("has converted uuids", true);
		
		propFile.save();
	}
	
	
}
