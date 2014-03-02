package me.armar.plugins.autorank.hooks;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.factionsapi.FactionsHandler;
import me.armar.plugins.autorank.hooks.worldguardapi.WorldGuardHandler;
import me.armar.plugins.autorank.statsmanager.StatsPluginManager;

/**
 * This class is used for loading all the dependencies Autorank has. <br>
 * Not all dependencies are required, some are optional.
 * <p>
 * Date created: 18:18:43 2 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class DependencyManager {

	public enum dependency {
		FACTIONS, STATS, WORLDGUARD
	};

	private Autorank plugin;
	
	private StatsPluginManager statsPluginManager;
	private WorldGuardHandler worldGuardAPIHandler;
	private FactionsHandler factionsHandler;

	public DependencyManager(Autorank instance) {
		plugin = instance;
		
		statsPluginManager = new StatsPluginManager(instance);
		factionsHandler = new FactionsHandler(instance);
		worldGuardAPIHandler = new WorldGuardHandler(instance);
	}

	public void loadDependencies() {
		
		// Make seperate loading bar
		plugin.getLogger().info("---------------[Autorank Dependencies]---------------");
		plugin.getLogger().info("Searching dependencies...");
		
		
		// Search a stats plugin.
		statsPluginManager.searchStatsPlugin();

		// Setup Factions
		factionsHandler.setupFactions();

		// Setup WorldGuard
		worldGuardAPIHandler.setupWorldGuard();
		
		// Make seperate stop loading bar
		plugin.getLogger().info("---------------[Autorank Dependencies]---------------");
	}
	
	public Object getDependency(dependency dep) {
		switch (dep) {
		case FACTIONS:
			return factionsHandler;
		case WORLDGUARD:
			return worldGuardAPIHandler;
		case STATS:
			return statsPluginManager.getStatsPlugin();
		default:
			throw new IllegalArgumentException("Unknown dependency '" + dep.toString() + "'");
		}
	}

}
