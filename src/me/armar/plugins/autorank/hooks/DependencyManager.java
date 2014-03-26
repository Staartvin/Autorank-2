package me.armar.plugins.autorank.hooks;

import java.util.HashMap;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.essentialsapi.EssentialsHandler;
import me.armar.plugins.autorank.hooks.factionsapi.FactionsHandler;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
import me.armar.plugins.autorank.hooks.ontimeapi.OnTimeHandler;
import me.armar.plugins.autorank.hooks.royalcommandsapi.RoyalCommandsHandler;
import me.armar.plugins.autorank.hooks.vaultapi.VaultHandler;
import me.armar.plugins.autorank.hooks.worldguardapi.WorldGuardHandler;
import me.armar.plugins.autorank.statsmanager.StatsPluginManager;

import org.bukkit.entity.Player;

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

	/**
	 * Enum containing all dependencies Autorank has.<br>  
	 * Some are optional, some not. This enumeration is used to dynamically load the dependencies.<br>
	 * Autorank is also included because this enum is used for methods that require the own plugin.
	 * <p>
	 * Date created:  16:48:08
	 * 6 mrt. 2014
	 * @author Staartvin
	 *
	 */
	public enum dependency {
		AUTORANK, FACTIONS, STATS, WORLDGUARD, MCMMO, ESSENTIALS, VAULT, ROYALCOMMANDS, ONTIME
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
		handlers.put(dependency.ESSENTIALS, new EssentialsHandler(instance));
		handlers.put(dependency.VAULT, new VaultHandler(instance));
		handlers.put(dependency.ROYALCOMMANDS, new RoyalCommandsHandler(instance));
		handlers.put(dependency.ONTIME, new OnTimeHandler(instance));
		
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
	
	public boolean isAFK(Player player) {
		if (!plugin.getConfigHandler().useAFKIntegration()) return false;
		
		if (handlers.get(dependency.ESSENTIALS).isAvailable()) {
			return ((EssentialsHandler) handlers.get(dependency.ESSENTIALS)).isAFK(player);
		} else if (handlers.get(dependency.ROYALCOMMANDS).isAvailable()) {
			return ((RoyalCommandsHandler) handlers.get(dependency.ROYALCOMMANDS)).isAFK(player);
		}
		
		// No suitable plugin found
		return false;		
	}

}
