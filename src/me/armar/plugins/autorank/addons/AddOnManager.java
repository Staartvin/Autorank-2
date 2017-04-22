package me.armar.plugins.autorank.addons;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

import me.armar.plugins.autorank.Autorank;

/**
 * This class is used for managing third party addons developers can build.
 * 
 * @author Staartvin
 * 
 */
public class AddOnManager {

    private final HashMap<String, JavaPlugin> loadedAddons = new HashMap<String, JavaPlugin>();

    private final Autorank plugin;

    /**
     * AddOnManager handlers all actions between addons and Autorank. You can
     * access this AddOnManager at any time you like.
     * 
     * @param instance
     *            instance of the main class of Autorank.
     */
    public AddOnManager(final Autorank instance) {
        plugin = instance;
    }

    /**
     * Get the main class of an Addon
     * 
     * @param addonName
     *            Name of the addon
     * @return Main class of the addon, or null if non-existent.
     */
    public JavaPlugin getLoadedAddon(final String addonName) {
        if (!isAddonLoaded(addonName))
            return null;

        return loadedAddons.get(addonName);
    }

    /**
     * Get all loaded Addons.
     * 
     * @return a list of loaded Addons
     */
    public Set<String> getLoadedAddons() {
        return loadedAddons.keySet();
    }

    /**
     * Check to see if a certain Addon is loaded
     * 
     * @param addonName
     *            Name of the Addon
     * @return true if loaded, false otherwise.
     */
    public boolean isAddonLoaded(final String addonName) {
        return loadedAddons.containsKey(addonName);
    }

    /**
     * Load an Addon so that it can be accessed later on.
     * 
     * @param addonName
     *            Name of the addon
     * @param addon
     *            Main class of the addon
     */
    public void loadAddon(final String addonName, final JavaPlugin addon) {
        // Register addon
        if (isAddonLoaded(addonName))
            return;

        loadedAddons.put(addonName, addon);

        // Announce addon
        plugin.getLogger().info("Loaded addon " + addonName);
    }

    /**
     * Unload an Addon so its reference is removed and can no longer be
     * accessed. <br>
     * <b>NOTE:</b> Autorank will not unload the Addon itself. It will only
     * remove the reference to it.
     * 
     * @param addonName
     */
    public void unloadAddon(final String addonName) {
        if (!isAddonLoaded(addonName))
            return;

        loadedAddons.remove(addonName);

        // Announce
        plugin.getLogger().info("Unloaded addon " + addonName);
    }

    /**
     * Unload all Addons that are currently loaded.
     */
    public void unloadAllAddons() {
        for (final String addon : loadedAddons.keySet()) {
            unloadAddon(addon);
        }
    }
}
