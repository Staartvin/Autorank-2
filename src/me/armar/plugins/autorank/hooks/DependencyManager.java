package me.armar.plugins.autorank.hooks;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.StatsPluginManager;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.PluginLibrary;
import me.staartvin.utils.pluginlibrary.hooks.LibraryHook;
import me.staartvin.utils.pluginlibrary.hooks.afkmanager.AFKManager;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * This class is used for loading all the dependencies Autorank has. <br>
 * Not all dependencies are required, some are optional.
 *
 * @author Staartvin
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
     */
    public enum AutorankDependency {
        AUTORANK,
        ONTIME,
        STATS,
        STATZ,
    }

    private final HashMap<AutorankDependency, DependencyHandler> handlers = new HashMap<AutorankDependency,
            DependencyHandler>();

    private final Autorank plugin;

    private final StatsPluginManager statsPluginManager;

    private PluginLibrary pluginLibrary;

    public DependencyManager(final Autorank instance) {
        plugin = instance;

        // Register handlers
        handlers.put(AutorankDependency.STATZ, new StatzAPIHandler(instance));

        this.loadPluginLibrary();

        statsPluginManager = new StatsPluginManager(instance);
    }

    /**
     * Get a specific AutorankDependency.
     *
     * @param dep Dependency to get.
     * @return the {@linkplain DependencyHandler} that is associated with the
     * given {@linkplain AutorankDependency}, can be null.
     */
    public DependencyHandler getDependency(final AutorankDependency dep) {

        if (!handlers.containsKey(dep)) {
            throw new IllegalArgumentException("Unknown AutorankDependency '" + dep.toString() + "'");
        } else {
            return handlers.get(dep);
        }
    }

    /**
     * Get the installed Stats plugin that Autorank uses.
     */
    public StatsPlugin getStatsPlugin() {
        return statsPluginManager.getStatsPlugin();
    }

    /**
     * Check whether the given player is AFK. <br>
     * Obeys the AFK setting in the Settings.yml.
     *
     * @param player Player to check.
     * @return true if the player is suspected of being AFK, false otherwise.
     */
    public boolean isAFK(final Player player) {
        if (!plugin.getSettingsConfig().useAFKIntegration()) {
            return false;
        }

        for (Library library : Library.values()) {
            LibraryHook libraryHook = this.getLibraryHook(library);

            // It seems that the library is not loaded.
            if (libraryHook == null) continue;

            // If this plugin cannot check for AFK, skip it.
            if (!(libraryHook instanceof AFKManager)) continue;

            // Check if the library is available.
            if (!libraryHook.isHooked()) continue;

            plugin.debugMessage("Using " + library.getHumanPluginName() + " for AFK");

            // We found a library that supports checking AFK, so we use it.
            return ((AFKManager) libraryHook).isAFK(player.getUniqueId());
        }

        // No suitable plugin found
        return false;
    }

    /**
     * Load all dependencies used for Autorank. <br>
     * Autorank will check for dependencies and shows the output on the console.
     *
     * @throws Exception This can be a multitude of exceptions
     */
    public void loadDependencies() throws Exception {

        // Make seperate loading bar
        if (plugin.getSettingsConfig().useAdvancedDependencyLogs()) {
            plugin.getLogger().info("---------------[Autorank Dependencies]---------------");
            plugin.getLogger().info("Searching dependencies...");
        }

        // Load all dependencies
        for (final DependencyHandler depHandler : handlers.values()) {
            // Make sure to respect settings
            depHandler.setup(plugin.getSettingsConfig().useAdvancedDependencyLogs());
        }

        if (plugin.getSettingsConfig().useAdvancedDependencyLogs()) {
            plugin.getLogger().info("Searching stats plugin...");
            plugin.getLogger().info("");
        }

        // Search a stats plugin.
        statsPluginManager.searchStatsPlugin();

        if (plugin.getSettingsConfig().useAdvancedDependencyLogs()) {
            // Make seperate stop loading bar
            plugin.getLogger().info("---------------[Autorank Dependencies]---------------");
        }

        plugin.getLogger().info("Loaded libraries and dependencies");

        // After loading dependencies, search permissions plugin
        plugin.getPermPlugHandler().searchPermPlugin();
    }

    /**
     * Check whether a dependency of Autorank is available.
     *
     * @param dep Dependency to check.
     * @return true if it is available, false otherwise.
     */
    public boolean isAvailable(AutorankDependency dep) {
        DependencyHandler handler = getDependency(dep);

        if (handler == null)
            return false;

        return handler.isAvailable();
    }

    /**
     * Get library hook of PluginLibrary
     *
     * @param library library to get
     * @return hook used by PluginLibrary (if available) or null if not found.
     */
    public LibraryHook getLibraryHook(Library library) {
        if (library == null) return null;

        if (!this.isPluginLibraryLoaded()) return null;

        return PluginLibrary.getLibrary(library);
    }

    /**
     * Check whether a plugin is available using PluginLibrary.
     *
     * @param library Library to check
     * @return true if it is available, false otherwise.
     */
    public boolean isAvailable(Library library) {

        if (!isPluginLibraryLoaded()) return false;

        if (library == null) return false;

        LibraryHook hook = this.getLibraryHook(library);

        if (hook == null) {
            return false;
        }

        return hook.isAvailable() && LibraryHook.isPluginAvailable(library) && hook.isHooked();
    }

    private boolean loadPluginLibrary() {
        pluginLibrary = PluginLibrary.getPluginLibrary(this.plugin);

        return pluginLibrary.enablePluginLibrary() > 0;
    }

    public boolean isPluginLibraryLoaded() {
        return pluginLibrary != null;
    }

}
