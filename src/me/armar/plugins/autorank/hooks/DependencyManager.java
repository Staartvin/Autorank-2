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
     * 
     * @author Staartvin
     * 
     */
    public enum AutorankDependency {

        AUTORANK, ONTIME, STATS, STATZ, VAULT
    };

    private final HashMap<AutorankDependency, DependencyHandler> handlers = new HashMap<AutorankDependency, DependencyHandler>();

    private final Autorank plugin;

    private final StatsPluginManager statsPluginManager;

    public DependencyManager(final Autorank instance) {
        plugin = instance;

        // Register handlers
        handlers.put(AutorankDependency.STATZ, new StatzAPIHandler(instance));
        handlers.put(AutorankDependency.VAULT, new VaultHandler(instance));

        statsPluginManager = new StatsPluginManager(instance);
    }

    /**
     * Get a specific AutorankDependency.
     * 
     * @param dep
     *            Dependency to get.
     * @return the {@linkplain DependencyHandler} that is associated with the
     *         given {@linkplain AutorankDependency}, can be null.
     */
    public DependencyHandler getDependency(final AutorankDependency dep) {

        if (!handlers.containsKey(dep)) {
            throw new IllegalArgumentException("Unknown AutorankDependency '" + dep.toString() + "'");
        } else {
            return handlers.get(dep);
        }
    }

    /**
     * Get a Statz dependency handler
     * 
     * @param dep
     *            Dependency to get
     * @return a Statz dependency or null if Statz is not installed or not
     *         properly enabled.
     */
    public me.staartvin.statz.hooks.DependencyHandler getDependencyHandler(Dependency dep) throws NoClassDefFoundError {
        StatzAPIHandler statz = (StatzAPIHandler) plugin.getDependencyManager().getDependency(AutorankDependency.STATZ);

        if (statz == null || !statz.isAvailable()) {
            return null;
        }

        return statz.getDependencyHandler(dep);
    }

    /**
     * Get the installed Stats plugin that Autorank uses.
     */
    public StatsPlugin getStatsPlugin() {
        return statsPluginManager.getStatsPlugin();
    }

    /**
     * Get the Statz plugin class to use as a connection between other
     * dependencies of Statz.
     */
    public StatzAPIHandler getStatzConnector() {
        StatzAPIHandler statz = (StatzAPIHandler) plugin.getDependencyManager().getDependency(AutorankDependency.STATZ);

        if (statz == null || !statz.isAvailable()) {
            return null;
        }

        return statz;
    }

    /**
     * Check whether the given player is AFK. <br>
     * Obeys the AFK setting in the Settings.yml.
     * 
     * @param player
     *            Player to check.
     * @return true if the player is suspected of being AFK, false otherwise.
     */
    public boolean isAFK(final Player player) {
        if (!plugin.getConfigHandler().useAFKIntegration()
                || !this.getDependency(AutorankDependency.STATZ).isAvailable()) {
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
     * Load all dependencies used for Autorank. <br>
     * Autorank will check for dependencies and shows the output on the console.
     * 
     * @throws Exception
     *             This can be a multitude of exceptions
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

        // After loading dependencies, search permissions plugin
        plugin.getPermPlugHandler().searchPermPlugin();
    }

}
