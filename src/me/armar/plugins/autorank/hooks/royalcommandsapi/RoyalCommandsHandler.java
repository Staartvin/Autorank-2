package me.armar.plugins.autorank.hooks.royalcommandsapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.royaldev.royalcommands.RoyalCommands;

/**
 * Handles all connections with RoyalCommands
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class RoyalCommandsHandler implements DependencyHandler {

	private RoyalCommands api;
	private final Autorank plugin;

	public RoyalCommandsHandler(final Autorank instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin("RoyalCommands");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof RoyalCommands)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	public boolean isAFK(final Player player) {
		if (!isAvailable())
			return false;

		if (!plugin.getConfigHandler().useAFKIntegration())
			return false;

		return api.getAPI().getPlayerAPI().isAfk(player);
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
		final RoyalCommands plugin = (RoyalCommands) get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.getLogger().info("RoyalCommands has not been found!");
			}
			return false;
		} else {
			api = (RoyalCommands) get();

			if (api != null) {
				if (verbose) {
					plugin.getLogger().info(
							"RoyalCommands has been found and can be used!");
				}
				return true;
			} else {
				if (verbose) {
					plugin.getLogger().info(
							"RoyalCommands has been found but cannot be used!");
				}
				return false;
			}
		}
	}
}
