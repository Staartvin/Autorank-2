package me.armar.plugins.autorank.hooks.ultimatecoreapi;

import bammerbom.ultimatecore.bukkit.UltimateCore;
import bammerbom.ultimatecore.bukkit.api.UC;
import bammerbom.ultimatecore.bukkit.api.UPlayer;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Handles all connections with Ultimate Core 2.x
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 *
 * @author Staartvin
 *
 */
public class UltimateCore2Handler implements DependencyHandler {

    private UltimateCore api;
    private final Autorank plugin;

    public UltimateCore2Handler(final Autorank instance) {
        plugin = instance;
    }

    /* (non-Javadoc)
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
     */
    @Override
    public Plugin get() {
        final Plugin plugin = this.plugin.getServer().getPluginManager()
                .getPlugin("UltimateCore");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof UltimateCore)) {
            return null; // Maybe you want throw an exception instead
        }

        return plugin;
    }

    /* (non-Javadoc)
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
     */
    @Override
    public boolean isAvailable() {
        return api != null;
    }

    /* (non-Javadoc)
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
     */
    @Override
    public boolean isInstalled() {
        final UltimateCore plugin = (UltimateCore) get();

        return plugin != null && plugin.isEnabled();
    }

    /* (non-Javadoc)
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
     */
    @Override
    public boolean setup(final boolean verbose) {
        if (!isInstalled()) {
            if (verbose) {
                plugin.getLogger().info("UltimateCore has not been found!");
            }
            return false;
        } else {
            api = (UltimateCore) get();

            if (api != null) {
                if (verbose) {
                    plugin.getLogger().info(
                            "UltimateCore has been found and can be used!");
                }
                return true;
            } else {
                if (verbose) {
                    plugin.getLogger().info(
                            "UltimateCore has been found but cannot be used!");
                }
                return false;
            }
        }
    }

    public boolean isAFK(final Player player) {
        if (!isAvailable()) {
            return false;
        }

        if (!plugin.getConfigHandler().useAFKIntegration()) {
            return false;
        }

        final UPlayer user = UC.getPlayer(player);

        if (user == null) {
            return false;
        }

        return user.isAfk();
    }
}
