package me.armar.plugins.autorank.hooks.vaultapi;

import me.armar.plugins.autorank.hooks.DependencyHandler;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.PluginLibrary;
import me.staartvin.plugins.pluginlibrary.hooks.LibraryHook;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * Handles all connections with Statz
 * <p>
 * Date created: 11:47:01 16 jun. 2016
 *
 * @author Staartvin
 */
public class PluginLibraryHandler extends DependencyHandler {

    private PluginLibrary pluginLibrary;

    public PluginLibraryHandler() {
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
     */
    @Override
    public Plugin get() {
        final Plugin plugin = this.getPlugin().getServer().getPluginManager()
                .getPlugin("PluginLibrary");

        try {
            // May not be loaded
            if (plugin == null || !(plugin instanceof PluginLibrary)) {
                return null; // Maybe you want throw an exception instead
            }
        } catch (NoClassDefFoundError e) {
            // Votifier was not found, maybe try NuVotifier
            return null;
            // e.printStackTrace();
        }

        return plugin;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailablePluginLibrary()
     */
    @Override
    public boolean isAvailable() {
        return pluginLibrary != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
     */
    @Override
    public boolean isInstalled() {
        Plugin plugin = get();

        return plugin != null && plugin.isEnabled();
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
     */
    @Override
    public boolean setup(final boolean verbose) {
        if (!isInstalled()) {
            if (verbose) {
                this.getPlugin().debugMessage(ChatColor.RED + "PluginLibrary has not been found!");
            }
            return false;
        } else {
            try {
                pluginLibrary = (PluginLibrary) get();
            } catch (NoClassDefFoundError e) {
                // Do nothing atm
            }

            if (pluginLibrary != null) {
                return true;
            } else {
                if (verbose) {
                    this.getPlugin().debugMessage(ChatColor.RED + "PluginLibrary has been found but cannot be used!");
                }
                return false;
            }
        }
    }

    /**
     * Get the library hook of PluginLibrary
     *
     * @param library Library to obtain
     * @return library hook that is used by PluginLibrary or null if not found.
     */
    public LibraryHook getLibraryHook(Library library) {
        if (!this.isAvailable()) return null;

        if (library == null) return null;

        return PluginLibrary.getLibrary(library);
    }
}
