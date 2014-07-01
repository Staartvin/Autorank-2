package me.armar.plugins.autorank.hooks.worldguardapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Handle all connections with WorldGuard
 * <p>
 * Date created: 18:06:52 21 feb. 2014
 * 
 * @author Staartvin
 * 
 */
public class WorldGuardHandler implements DependencyHandler {

	private Autorank plugin;
	private WorldGuardPlugin worldGuardAPI;

	public WorldGuardHandler(Autorank instance) {
		plugin = instance;
	}

	/**
	 * Check to see if a player is in a specific region
	 * 
	 * @param player Player that needs to be checked
	 * @param regionName Name of the region to be checked
	 * @return true if the player is in that region; false otherwise.
	 */
	public boolean isInRegion(Player player, String regionName) {
		if (!isAvailable())
			return false;

		if (player == null || regionName == null)
			return false;

		Location loc = player.getLocation();

		RegionManager regManager = worldGuardAPI.getRegionManager(loc
				.getWorld());

		if (regManager == null)
			return false;

		ApplicableRegionSet set = regManager.getApplicableRegions(loc);

		if (set == null)
			return false;

		for (ProtectedRegion region : set) {
			String name = region.getId();

			if (name.equalsIgnoreCase(regionName)) {
				return true;
			}
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.getLogger().info("WorldGuard has not been found!");
			}
			return false;
		} else {
			worldGuardAPI = (WorldGuardPlugin) get();
			if (worldGuardAPI != null) {
				if (verbose) {
					plugin.getLogger().info(
							"WorldGuard has been found and can be used!");
				}
				return true;
			} else {
				if (verbose) {
					plugin.getLogger().info(
							"WorldGuard has been found but cannot be used!");
				}
				return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		WorldGuardPlugin wg = (WorldGuardPlugin) get();

		return wg != null && wg.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return worldGuardAPI != null;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		Plugin wgPlugin = plugin.getServer().getPluginManager()
				.getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (wgPlugin == null || !(wgPlugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return wgPlugin;
	}

}
