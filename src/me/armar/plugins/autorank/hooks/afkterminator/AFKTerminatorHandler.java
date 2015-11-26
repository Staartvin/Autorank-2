package me.armar.plugins.autorank.hooks.afkterminator;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import me.edge209.afkTerminator.AfkTerminator;
import me.edge209.afkTerminator.AfkTerminatorAPI;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Handles all connections with AFKTerminator
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class AFKTerminatorHandler implements DependencyHandler {

	private final Autorank plugin;

	public AFKTerminatorHandler(final Autorank instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin("afkTerminator");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof AfkTerminator)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	public boolean isAFK(final Player player) {
		if (!isAvailable())
			return false;

		if (!plugin.getConfigHandler().useAFKIntegration())
			return false;

		return AfkTerminatorAPI.isAFKMachineDetected(player.getUniqueId());
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		// API is static class
		return isInstalled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		final AfkTerminator plugin = (AfkTerminator) get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.getLogger().info("AfkTerminator has not been found!");
			}
			return false;
		} else {
			if (isInstalled()) {
				if (verbose) {
					plugin.getLogger().info(
							"AfkTerminator has been found and can be used!");
				}
				return true;
			} else {
				if (verbose) {
					plugin.getLogger().info(
							"AfkTerminator has been found but cannot be used!");
				}
				return false;
			}
		}
	}
}
