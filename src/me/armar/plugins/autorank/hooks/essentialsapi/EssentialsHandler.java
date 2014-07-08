package me.armar.plugins.autorank.hooks.essentialsapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

/**
 * Handles all connections with Essentials
 * <p>
 * Date created: 21:02:05 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class EssentialsHandler implements DependencyHandler {

	private final Autorank plugin;
	private Essentials api;

	public EssentialsHandler(final Autorank instance) {
		plugin = instance;
	}

	public boolean isAFK(final Player player) {
		if (!isAvailable())
			return false;

		if (!plugin.getConfigHandler().useAFKIntegration())
			return false;

		final User user = api.getUser(player);

		if (user == null) {
			return false;
		}

		return user.isAfk();
	}

	public boolean isJailed(final Player player) {
		if (!isAvailable())
			return false;

		final User user = api.getUser(player);

		if (user == null) {
			return false;
		}

		return user.isJailed();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin("Essentials");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof Essentials)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.getLogger().info("Essentials has not been found!");
			}
			return false;
		} else {
			api = (Essentials) get();

			if (api != null) {
				if (verbose) {
					plugin.getLogger().info(
							"Essentials has been found and can be used!");
				}
				return true;
			} else {
				if (verbose) {
					plugin.getLogger().info(
							"Essentials has been found but cannot be used!");
				}
				return false;
			}
		}
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
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return api != null;
	}
}
