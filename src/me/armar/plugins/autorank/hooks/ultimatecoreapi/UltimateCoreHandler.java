package me.armar.plugins.autorank.hooks.ultimatecoreapi;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;

/**
 * Handles all connections with Ultimate Core
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class UltimateCoreHandler implements DependencyHandler {

	private Plugin api;
	private final Autorank instance;

	public UltimateCoreHandler(final Autorank instance) {
		this.instance = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		final Plugin plugin = this.instance.getServer().getPluginManager().getPlugin("UltimateCore");

		// UltimateCore may not be loaded
		try { //Avoid ClassNotFound
			if (plugin != null
			/*&& plugin instanceof bammerbom.ultimatecore.UltimateCore*/) {
				return plugin;
			}
		} catch (final Exception ex) {
		}
		try { //Avoid ClassNotFound
			if (plugin != null && plugin instanceof bammerbom.ultimatecore.bukkit.UltimateCore) {
				return plugin;
			}
		} catch (final Exception ex) {
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
				instance.getLogger().info("UltimateCore has not been found!");
			}
			return false;
		} else {
			api = get();

			if (api != null) {

				if (verbose) {
					instance.getLogger().info("UltimateCore has been found and can be used!");
				}
				return true;
			} else {
				if (verbose) {
					instance.getLogger().info("UltimateCore has been found but cannot be used!");
				}
				return false;
			}
		}
	}

	public Integer getVersion() {
		if (api.getDescription().getVersion().startsWith("1")) {
			return 1;
		}
		if (api.getDescription().getVersion().startsWith("2")) {
			return 2;
		}
		return null;
	}

	public boolean isAFK(final Player player) {
		if (!isAvailable()) {
			return false;
		}

		if (!instance.getConfigHandler().useAFKIntegration()) {
			return false;
		}

		// Remove support of UltimateCore 2.0 -- too old

		/*		if (getVersion().equals(1)) {
					final UCplayer user = UC.getPlayer(player);
		
					if (user == null) {
						return false;
					}
		
					return user.isAFK();
				}*/
		if (getVersion().equals(2)) {
			final bammerbom.ultimatecore.bukkit.api.UPlayer user = bammerbom.ultimatecore.bukkit.api.UC
					.getPlayer(player);

			if (user == null) {
				return false;
			}

			return user.isAfk();
		}
		return false;

	}
}
