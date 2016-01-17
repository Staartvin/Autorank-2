package me.armar.plugins.autorank.hooks.ontimeapi;

import org.bukkit.plugin.Plugin;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import me.edge209.OnTime.OnTime;
import me.edge209.OnTime.OnTimeAPI;
import me.edge209.OnTime.OnTimeAPI.data;

/**
 * Handles all connections with OnTime
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class OnTimeHandler implements DependencyHandler {

	private OnTime api;
	private final Autorank plugin;

	public OnTimeHandler(final Autorank instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin("OnTime");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof OnTime)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	public int getPlayTime(final String playerName) {
		if (!isAvailable())
			return 0;

		// Divide by 60000 because time is in milliseconds
		return (int) (OnTimeAPI.getPlayerTimeData(playerName, data.TOTALPLAY) / 60000);
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
		final Plugin plugin = get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.getLogger().info("OnTime has not been found!");
			}
			return false;
		} else {
			api = (OnTime) get();

			if (api != null) {
				if (verbose) {
					plugin.getLogger().info(
							"OnTime has been found and can be used!");
				}
				return true;
			} else {
				if (verbose) {
					plugin.getLogger().info(
							"OnTime has been found but cannot be used!");
				}
				return false;
			}
		}
	}
}
