package me.armar.plugins.autorank.hooks;

import java.util.HashMap;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.afkterminator.AFKTerminatorHandler;
import me.armar.plugins.autorank.hooks.essentialsapi.EssentialsHandler;
import me.armar.plugins.autorank.hooks.factionsapi.FactionsHandler;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
import me.armar.plugins.autorank.hooks.ontimeapi.OnTimeHandler;
import me.armar.plugins.autorank.hooks.royalcommandsapi.RoyalCommandsHandler;
import me.armar.plugins.autorank.hooks.statisticsapi.StatisticsAPIHandler;
import me.armar.plugins.autorank.hooks.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.hooks.statsapi.customstats.FoodEatenStat;
import me.armar.plugins.autorank.hooks.statsapi.customstats.MobKilledStat;
import me.armar.plugins.autorank.hooks.ultimatecoreapi.UltimateCoreHandler;
import me.armar.plugins.autorank.hooks.vaultapi.VaultHandler;
import me.armar.plugins.autorank.hooks.worldguardapi.WorldGuardHandler;
import me.armar.plugins.autorank.listeners.PlayerEatsFoodListener;
import me.armar.plugins.autorank.listeners.PlayerKillsMobListener;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
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
	 * Some are optional, some not. This enumeration is used to dynamically load
	 * the dependencies.<br>
	 * Autorank is also included because this enum is used for methods that
	 * require the own plugin.
	 * <p>
	 * Date created: 16:48:08 6 mrt. 2014
	 * 
	 * @author Staartvin
	 * 
	 */
	public enum dependency {

		AUTORANK, ESSENTIALS, FACTIONS, MCMMO, ONTIME, ROYALCOMMANDS, STATS, VAULT, WORLDGUARD, ULTIMATECORE, STATISTICS, AFKTERMINATOR
	};

	private final HashMap<dependency, DependencyHandler> handlers = new HashMap<dependency, DependencyHandler>();

	private final Autorank plugin;

	private final StatsPluginManager statsPluginManager;

	public DependencyManager(final Autorank instance) {
		plugin = instance;

		// Register handlers
		handlers.put(dependency.FACTIONS, new FactionsHandler(instance));
		handlers.put(dependency.WORLDGUARD, new WorldGuardHandler(instance));
		handlers.put(dependency.MCMMO, new McMMOHandler(instance));
		handlers.put(dependency.ESSENTIALS, new EssentialsHandler(instance));
		handlers.put(dependency.VAULT, new VaultHandler(instance));
		handlers.put(dependency.ROYALCOMMANDS, new RoyalCommandsHandler(
				instance));
		handlers.put(dependency.ONTIME, new OnTimeHandler(instance));
		handlers.put(dependency.STATS, new StatsAPIHandler(instance));
		handlers.put(dependency.ULTIMATECORE, new UltimateCoreHandler(instance));
		handlers.put(dependency.STATISTICS, new StatisticsAPIHandler(instance));
		handlers.put(dependency.AFKTERMINATOR, new AFKTerminatorHandler(
				instance));

		statsPluginManager = new StatsPluginManager(instance);
	}

	public DependencyHandler getDependency(final dependency dep) {

		if (!handlers.containsKey(dep)) {
			throw new IllegalArgumentException("Unknown dependency '"
					+ dep.toString() + "'");
		} else {
			return handlers.get(dep);
		}
	}

	public StatsPlugin getStatsPlugin() {
		return statsPluginManager.getStatsPlugin();
	}

	public boolean isAFK(final Player player) {
		if (!plugin.getConfigHandler().useAFKIntegration()) {
			return false;
		}

		if (handlers.get(dependency.ESSENTIALS).isAvailable()) {
			plugin.debugMessage("Using Essentials for AFK");
			return ((EssentialsHandler) handlers.get(dependency.ESSENTIALS))
					.isAFK(player);
		} else if (handlers.get(dependency.ROYALCOMMANDS).isAvailable()) {
			plugin.debugMessage("Using RoyalCommands for AFK");
			return ((RoyalCommandsHandler) handlers
					.get(dependency.ROYALCOMMANDS)).isAFK(player);
		} else if (handlers.get(dependency.ULTIMATECORE).isAvailable()) {
			plugin.debugMessage("Using UltimateCore for AFK");
			return ((UltimateCoreHandler) handlers.get(dependency.ULTIMATECORE))
					.isAFK(player);
		} else if (handlers.get(dependency.AFKTERMINATOR).isAvailable()) {
			plugin.debugMessage("Using AFKTerminator for AFK");
			return ((AFKTerminatorHandler) handlers
					.get(dependency.AFKTERMINATOR)).isAFK(player);
		}
		// No suitable plugin found
		return false;
	}

	/**
	 * Loads all dependencies used for Autorank. <br>
	 * Autorank will check for dependencies and shows the output on the console.
	 * @throws Exception This can be a multitude of exceptions
	 * 
	 */
	public void loadDependencies() throws Exception {

		// Make seperate loading bar
		if (plugin.getConfigHandler().useAdvancedDependencyLogs()) {
			plugin.getLogger().info(
					"---------------[Autorank Dependencies]---------------");
			plugin.getLogger().info("Searching dependencies...");
		}

		// Load all dependencies
		for (final DependencyHandler depHandler : handlers.values()) {
			// Make sure to respect settings
			depHandler.setup(plugin.getConfigHandler()
					.useAdvancedDependencyLogs());
		}

		if (plugin.getConfigHandler().useAdvancedDependencyLogs()) {
			plugin.getLogger().info("Searching stats plugin...");
			plugin.getLogger().info("");
		}

		// Search a stats plugin.
		statsPluginManager.searchStatsPlugin();

		if (plugin.getConfigHandler().useAdvancedDependencyLogs()) {
			// Make seperate stop loading bar
			plugin.getLogger().info(
					"---------------[Autorank Dependencies]---------------");
		}

		plugin.getLogger().info("Loaded libraries and dependencies");
		
		if (this.getDependency(dependency.STATS).isAvailable()) {
			StatsAPIHandler handler = (StatsAPIHandler) this.getDependency(dependency.STATS);
			
			// Register stats to Stats plugin.
			
			handler.addStat(new MobKilledStat());
			plugin.debugMessage("Registered '" + MobKilledStat.statName + "' to Stats.");
			
			handler.addStat(new FoodEatenStat());
			plugin.debugMessage("Registered '" + FoodEatenStat.statName + "' to Stats.");
			
			// Register listeners
			plugin.getServer().getPluginManager().registerEvents(new PlayerEatsFoodListener(plugin), plugin);
			plugin.getServer().getPluginManager().registerEvents(new PlayerKillsMobListener(plugin), plugin);
		}
	}

}
