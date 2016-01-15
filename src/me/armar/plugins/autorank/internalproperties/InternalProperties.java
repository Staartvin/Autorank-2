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
		
		propFile.options().copyDefaults(true);
		
		propFile.save();
	}
	
	public void setCachedLeaderboard(List<String> cachedLeaderboard) {
		propFile.set("cached leaderboard", cachedLeaderboard);
		
		propFile.save();
	}
	
	public List<String> getCachedLeaderboard() {
		return (ArrayList<String>) propFile.getStringList("cached leaderboard");
	}
	
	public void setLeaderboardLastUpdateTime(long time) {
		propFile.set("leaderboard last updated", time);
		
		propFile.save();
	}
	
	public long getLeaderboardLastUpdateTime() {
		return propFile.getLong("leaderboard last updated", 0);
	}
	
	
}
