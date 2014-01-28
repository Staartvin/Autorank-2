/**
 * 
 */
package me.armar.plugins.autorank.addons;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;

/**
 * This will class will take care of all the handling of addons for Autorank.
 * <p>
 * Date created:  14:11:25
 * 28 jan. 2014
 * @author Staartvin
 *
 */
public class AddOnManager {

	private Autorank plugin;
	
	// Keep track of all loaded addons
	private List<String> loadedAddons = new ArrayList<String>();
	
	public AddOnManager(Autorank instance) {
		plugin = instance;
	}
	
	public List<String> getLoadedAddons() {
		return loadedAddons;
	}
	
	public boolean isAddonLoaded(String addonName) {
		return loadedAddons.contains(addonName);
	}
	
	public void loadAddon(String addonName) {
		// Register addon
		if (isAddonLoaded(addonName)) return;
		
		loadedAddons.add(addonName);
		
		// Announce addon
		plugin.getLogger().info("Loaded addon " + addonName);
	}
	
	public void unloadAddon(String addonName) {
		if (!isAddonLoaded(addonName)) return;
		
		loadedAddons.remove(addonName);
		
		// Announce
		plugin.getLogger().info("Unloaded addon " + addonName);
	}
	
	public void unloadAllAddons() {
		for (String addon: loadedAddons) {
			unloadAddon(addon);
		}
	}
	
	
	
}
