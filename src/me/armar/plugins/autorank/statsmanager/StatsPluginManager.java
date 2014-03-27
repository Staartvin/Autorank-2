package me.armar.plugins.autorank.statsmanager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.statsmanager.handlers.DummyHandler;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * This class decides which Stats plugin will be used for getting stat data.
 * @author Staartvin
 *
 */
public class StatsPluginManager {

	private Autorank plugin;
	
	public StatsPluginManager(Autorank instance) {
		plugin = instance;
	}
	
	private StatsPlugin statsPlugin;
	
	public void searchStatsPlugin() {
		if (findStats()) {
			
			plugin.getLogger().info("Found Stats plugin: Stats (by Lolmewn)");
			
			
			statsPlugin = new StatsHandler(plugin, (StatsAPIHandler) plugin.getDependencyManager().getDependency(dependency.STATS));	
			
			if (statsPlugin == null) {
				plugin.getLogger().info("Couldn't hook into Stats! StatsHandler was unable to hook.");
				return;
			}
			
			if (!statsPlugin.isEnabled()) {
				plugin.getLogger().info("Couldn't hook into Stats! Make sure the version is correct.");
				return;
			}
			
			plugin.getLogger().info("Hooked into Stats (by Lolmewn)");
		} else {
			// Use dummy handler if no stats plugin was found
			statsPlugin = new DummyHandler();
			
			plugin.getLogger().info("No stats plugin found! Most requirements cannot be used!");
			
		}
	}
	
	public boolean findStats() {
		Plugin x = plugin.getServer().getPluginManager()
				.getPlugin("Stats");
		// Main == Stats main class
		if (x != null && x instanceof JavaPlugin) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get the current stats plugin running on this server
	 * @return returns the JavaPlugin that is responsible for stats. Null if there is none.
	 */
	public StatsPlugin getStatsPlugin() {
		return statsPlugin;
	}
}
