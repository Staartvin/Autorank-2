package me.armar.plugins.autorank.statsmanager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.statsmanager.handlers.StatzHandler;
import me.armar.plugins.autorank.statsmanager.handlers.vanilla.VanillaHandler;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.StatsHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * This class decides which Stats plugin will be used for getting stat storage.
 *
 * @author Staartvin
 */
public class StatsPluginManager {

    private final Autorank plugin;

    private StatsPlugin statsPlugin;

    public StatsPluginManager(final Autorank instance) {
        plugin = instance;
    }

    private boolean isPluginAvailable(String pluginName) {
        final Plugin x = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        return x != null;
    }

    /**
     * Get the current stats plugin running on this server
     *
     * @return returns the JavaPlugin that is responsible for stats. Null if
     * there is none.
     */
    public StatsPlugin getStatsPlugin() {
        return statsPlugin;
    }

    public void searchStatsPlugin() {
        if (isPluginAvailable("Stats")) {

            plugin.getLogger().info("Found Stats plugin: Stats (by Lolmewn)");

            statsPlugin = new StatsHandler(plugin,
                    (StatsHook) plugin.getDependencyManager().getLibraryHook(Library.STATS));

            if (statsPlugin == null) {
                plugin.getLogger().info("Couldn't hook into Stats! StatsHandler was unable to hook.");
                return;
            }

            if (!statsPlugin.isEnabled()) {
                plugin.getLogger().info(
                        "Couldn't hook into Stats! Make sure the version is correct and Stats properly connects to " +
                                "your MySQL database.");
                return;
            }

            plugin.getLogger().info("Hooked into Stats (by Lolmewn)");
        } else if (isPluginAvailable("Statz")) {

            plugin.getLogger().info("Found Statz plugin: Statz (by Staartvin)");

            statsPlugin = new StatzHandler(plugin,
                    (StatzAPIHandler) plugin.getDependencyManager().getDependency(AutorankDependency.STATZ));

            if (statsPlugin == null) {
                plugin.getLogger().info("Couldn't hook into Statz! StatzHandler was unable to hook.");
                return;
            }

            if (!statsPlugin.isEnabled()) {
                plugin.getLogger().info("Couldn't hook into Statz! Make sure the version is correct!");
                return;
            }

            plugin.getLogger().info("Hooked into Statz (by Staartvin)");
        } else {
            // Use dummy handler if no stats plugin was found
            statsPlugin = new VanillaHandler(plugin);

            plugin.getLogger().info("No stats plugin found! Using statistics of vanilla Minecraft!");

        }
    }
}
