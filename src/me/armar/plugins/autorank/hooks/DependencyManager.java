package me.armar.plugins.autorank.hooks;

import java.util.HashMap;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.hooks.vaultapi.VaultHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.StatsPluginManager;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.AFKTerminatorHandler;
import me.staartvin.statz.hooks.handlers.EssentialsHandler;
import me.staartvin.statz.hooks.handlers.RoyalCommandsHandler;
import me.staartvin.statz.hooks.handlers.UltimateCoreHandler;

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
	// Test

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

		AUTORANK, STATS, STATZ, ONTIME, VAULT
	};

	private final HashMap<dependency, DependencyHandler> handlers = new HashMap<dependency, DependencyHandler>();

	private final Autorank plugin;

	private final StatsPluginManager statsPluginManager;

	public DependencyManager(final Autorank instance) {
		plugin = instance;

		// Register handlers
		handlers.put(dependency.STATZ, new StatzAPIHandler(instance));
		handlers.put(dependency.VAULT, new VaultHandler(instance));

		statsPluginManager = new StatsPluginManager(instance);
	}

	/**
	 * Gets a specific dependency.
	 * 
	 * @param dep Dependency to get.
	 * @return the {@linkplain DependencyHandler} that is associated with the
	 *         given {@linkplain dependency}, can be null.
	 */
	public DependencyHandler getDependency(final dependency dep) {

		if (!handlers.containsKey(dep)) {
			throw new IllegalArgumentException("Unknown dependency '" + dep.toString() + "'");
		} else {
			return handlers.get(dep);
		}
	}

	public StatsPlugin getStatsPlugin() {
		return statsPluginManager.getStatsPlugin();
	}

	/**
	 * Gets whether the given player is AFK.
	 * <br>
	 * Obeys the AFK setting in the Settings.yml.
	 * 
	 * @param player Player to check.
	 * @return true if the player is supspected of being AFK, false otherwise.
	 */
	public boolean isAFK(final Player player) {
		if (!plugin.getConfigHandler().useAFKIntegration() || !this.getDependency(dependency.STATZ).isAvailable()) {
			return false;
		}

		if (this.getDependencyHandler(Dependency.ESSENTIALS).isAvailable()) {
			plugin.debugMessage("Using Essentials for AFK");
			return ((EssentialsHandler) this.getDependencyHandler(Dependency.ESSENTIALS)).isAFK(player);
		} else if (this.getDependencyHandler(Dependency.ROYAL_COMMANDS).isAvailable()) {
			plugin.debugMessage("Using RoyalCommands for AFK");
			return ((RoyalCommandsHandler) this.getDependencyHandler(Dependency.ROYAL_COMMANDS)).isAFK(player);
		} else if (this.getDependencyHandler(Dependency.ULTIMATE_CORE).isAvailable()) {
			plugin.debugMessage("Using UltimateCore for AFK");
			return ((UltimateCoreHandler) this.getDependencyHandler(Dependency.ULTIMATE_CORE)).isAFK(player);
		} else if (this.getDependencyHandler(Dependency.AFKTERMINATOR).isAvailable()) {
			plugin.debugMessage("Using AfkTerminator for AFK");
			return ((AFKTerminatorHandler) this.getDependencyHandler(Dependency.AFKTERMINATOR)).isAFK(player);
		}

		// No suitable plugin found
		return false;
	}

	/**
	 * Loads all dependencies used for Autorank. <br>
	 * Autorank will check for dependencies and shows the output on the console.
	 * 
	 * @throws Exception This can be a multitude of exceptions
	 * 
	 */
	public void loadDependencies() throws Exception {

		// Make seperate loading bar
		if (plugin.getConfigHandler().useAdvancedDependencyLogs()) {
			plugin.getLogger().info("---------------[Autorank Dependencies]---------------");
			plugin.getLogger().info("Searching dependencies...");
		}

		// Load all dependencies
		for (final DependencyHandler depHandler : handlers.values()) {
			// Make sure to respect settings
			depHandler.setup(plugin.getConfigHandler().useAdvancedDependencyLogs());
		}

		if (plugin.getConfigHandler().useAdvancedDependencyLogs()) {
			plugin.getLogger().info("Searching stats plugin...");
			plugin.getLogger().info("");
		}

		// Search a stats plugin.
		statsPluginManager.searchStatsPlugin();

		if (plugin.getConfigHandler().useAdvancedDependencyLogs()) {
			// Make seperate stop loading bar
			plugin.getLogger().info("---------------[Autorank Dependencies]---------------");
		}

		plugin.getLogger().info("Loaded libraries and dependencies");

	}

	public me.staartvin.statz.hooks.DependencyHandler getDependencyHandler(Dependency dep) {
		StatzAPIHandler statz = (StatzAPIHandler) plugin.getDependencyManager().getDependency(dependency.STATZ);

		if (statz == null || !statz.isAvailable()) {
			return null;
		}

		return statz.getDependencyHandler(dep);
	}

	public StatzAPIHandler getStatzConnector() {
		StatzAPIHandler statz = (StatzAPIHandler) plugin.getDependencyManager().getDependency(dependency.STATZ);

		if (statz == null || !statz.isAvailable()) {
			return null;
		}

		return statz;
	}

}
