package me.armar.plugins.autorank.hooks;

import java.util.HashMap;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.factionsapi.FactionsHandler;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
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
		FACTIONS, STATS, WORLDGUARD, MCMMO
	};

	private Autorank plugin;

	private StatsPluginManager statsPluginManager;

	private HashMap<dependency, DependencyHandler> handlers = new HashMap<dependency, DependencyHandler>();

	public DependencyManager(Autorank instance) {
		plugin = instance;

		// Register handlers
		handlers.put(dependency.FACTIONS, new FactionsHandler(instance));
		handlers.put(dependency.WORLDGUARD, new WorldGuardHandler(instance));
		handlers.put(dependency.MCMMO, new McMMOHandler(instance));
		
		statsPluginManager = new StatsPluginManager(instance);
	}

	/**
	 * Loads all dependencies used for Autorank.
	 * <br>
	 * Autorank will check for dependencies and shows the output on the console.
	 * 
	 */
	public void loadDependencies() {

		// Make seperate loading bar
		plugin.getLogger().info(
				"---------------[Autorank Dependencies]---------------");
		plugin.getLogger().info("Searching dependencies...");

		// Search a stats plugin.
		statsPluginManager.searchStatsPlugin();

		// Load all dependencies
		for (DependencyHandler depHandler: handlers.values()) {
			depHandler.setup();
		}

		// Make seperate stop loading bar
		plugin.getLogger().info(
				"---------------[Autorank Dependencies]---------------");
	}

	public Object getDependency(dependency dep) {
		
		// Search for multiple stats plugins.
		if (dep.equals(dependency.STATS)) {
			return statsPluginManager.getStatsPlugin();
		}
		
		if (!handlers.containsKey(dep)) {
			throw new IllegalArgumentException("Unknown dependency '"
					+ dep.toString() + "'");
		} else {
			return handlers.get(dep);
		}
	}

}
