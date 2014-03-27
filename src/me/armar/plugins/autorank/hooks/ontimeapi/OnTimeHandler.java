package me.armar.plugins.autorank.hooks.ontimeapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import me.edge209.OnTime.OnTime;
import me.edge209.OnTime.OnTimeAPI;
import me.edge209.OnTime.OnTimeAPI.data;

import org.bukkit.plugin.Plugin;

/**
 * Handles all connections with OnTime
 * <p>
 * Date created:  21:02:05
 * 15 mrt. 2014
 * @author Staartvin
 *
 */
public class OnTimeHandler implements DependencyHandler {

	private final Autorank plugin;
	private OnTime api;

	public OnTimeHandler(final Autorank instance) {
		plugin = instance;
	}
	
	public int getPlayTime(String playerName) {
		if (!isAvailable()) return 0;
		
		// Divide by 60000 because time is in milliseconds
		return (int) (OnTimeAPI.getPlayerTimeData(playerName, data.TOTALPLAY) / 60000);
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin("OnTime");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof OnTime)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup() {
		if (!isInstalled()) {
			plugin.getLogger().info("OnTime has not been found!");
			return false;
		} else {
			api = (OnTime) get();

			if (api != null) {
				plugin.getLogger().info(
						"OnTime has been found and can be used!");
				return true;
			} else {
				plugin.getLogger().info(
						"OnTime has been found but cannot be used!");
				return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		Plugin plugin = get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return api != null;
	}
}
