package me.armar.plugins.autorank.addons;

import java.util.HashMap;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * This will class will take care of all the handling of addons for Autorank.
 * <p>
 * Date created: 14:11:25 28 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class AddOnManager {

	private final HashMap<String, JavaPlugin> loadedAddons = new HashMap<String, JavaPlugin>();

	private final Autorank plugin;

	/**
	 * AddOnManager handlers all actions between addons and Autorank. You can
	 * access this AddOnManager on any time you like.
	 * 
	 * @param instance instance of the main class of Autorank.
	 */
	public AddOnManager(final Autorank instance) {
		plugin = instance;
	}

	/**
	 * Get the main class of an addon
	 * 
	 * @param addonName name of the addon
	 * @return main class of the addon, or null if non-existent.
	 */
	public JavaPlugin getLoadedAddon(final String addonName) {
		if (!isAddonLoaded(addonName))
			return null;

		return loadedAddons.get(addonName);
	}

	/**
	 * Get all loaded addons.
	 * 
	 * @return a list of loaded addons
	 */
	public Set<String> getLoadedAddons() {
		return loadedAddons.keySet();
	}

	/**
	 * Check to see if a certain addon is loaded
	 * 
	 * @param addonName name of the addon
	 * @return true if loaded, false otherwise.
	 */
	public boolean isAddonLoaded(final String addonName) {
		return loadedAddons.containsKey(addonName);
	}

	/**
	 * Load an addon so that it can be accessed later on.
	 * 
	 * @param addonName name of the addon
	 * @param addon Main class of the addon
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
	 * Unload an addon so its reference is removed and can no longer be
	 * accessed. <br>
	 * <b>NOTE:</b> Autorank will not unload the addon itself. It will only
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
	 * Unload all addons that are currently loaded.
	 */
	public void unloadAllAddons() {
		for (final String addon : loadedAddons.keySet()) {
			unloadAddon(addon);
		}
	}
}
