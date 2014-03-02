package me.armar.plugins.autorank.hooks.worldguardapi;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Handle all tasks with WorldGuard
 * <p>
 * Date created:  18:06:52
 * 21 feb. 2014
 * @author Staartvin
 *
 */
public class WorldGuardHandler {

	private Autorank plugin;
	private WorldGuardPlugin worldGuardAPI;
	
	public WorldGuardHandler(Autorank instance) {
		plugin = instance;
	}
	
	public boolean setupWorldGuard() {
		if (!isWorldGuardInstalled()) {
			plugin.getLogger().info("WorldGuard has not been found!");
			return false;
		} else {
			worldGuardAPI = getWorldGuard();
			plugin.getLogger().info("WorldGuard has been found and can be used!");
			return true;
		}
	}
	
	private WorldGuardPlugin getWorldGuard() {
	    Plugin wgPlugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (wgPlugin == null || !(wgPlugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) wgPlugin;
	}
	
	/**
	 * Check to see if WorldGuard is running on this server
	 * @return true if it is, false otherwise.
	 */
	public boolean isWorldGuardInstalled() {
		WorldGuardPlugin wg = getWorldGuard();
		
		return wg != null && wg.isEnabled();
	}
	
	/**
	 * Check whether Autorank has hooked WorldGuard and thus can use it.
	 * @return true if Autorank hooked into it, false otherwise.
	 */
	public boolean isWorldGuardAvailable() {
		return worldGuardAPI != null;
	}
	
	/**
	 * Check to see if a player is in a specific region
	 * @param player Player that needs to be checked
	 * @param regionName Name of the region to be checked
	 * @return true if the player is in that region; false otherwise.
	 */
	public boolean isInRegion(Player player, String regionName) {
		if (!isWorldGuardAvailable()) return false;
		
		if (player == null || regionName == null) return false;
		
		Location loc = player.getLocation();
		
		RegionManager regManager = worldGuardAPI.getRegionManager(loc.getWorld());
		
		if (regManager == null) return false;
		
		ApplicableRegionSet set = regManager.getApplicableRegions(loc);
		
		if (set == null) return false;
		
		for (ProtectedRegion region: set) {			
			String name = region.getId();
			
			if (name.equalsIgnoreCase(regionName)) {
				return true;
			}
		}
		
		return false;
	}
	
}
