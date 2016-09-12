package me.armar.plugins.autorank.hooks.vaultapi;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * Handles all connections with Statz
 * <p>
 * Date created: 11:47:01 16 jun. 2016
 * 
 * @author Staartvin
 * 
 */
public class VaultHandler implements DependencyHandler {

	public static Economy economy = null;
	public static Permission permission = null;
	private Vault vault;
	private final Autorank plugin;

	public VaultHandler(final Autorank instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("Vault");

		try {
			// WorldGuard may not be loaded
			if (plugin == null || !(plugin instanceof Vault)) {
				return null; // Maybe you want throw an exception instead
			}

		} catch (final NoClassDefFoundError exception) {
			this.plugin.getLogger()
					.info("Could not find Vault because it's probably disabled! Does Vault properly enable?");
			return null;
		}

		return plugin;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return vault != null;
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
				plugin.getLogger().info("Vault has not been found!");
			}
			return false;
		} else {
			vault = (Vault) get();

			if (vault != null && setupPermissions() && setupEconomy()) {
				if (verbose) {
					plugin.getLogger().info("Vault has been found and can be used!");
				}
				return true;
			} else {
				
				if (verbose) {
					if (permission == null) {
						plugin.getLogger().info("Vault has been found but cannot be used! (no supported permissions plugin)");
					} else if (economy == null) {
						plugin.getLogger().info("Vault has been found but cannot be used! (no supported economy plugin)");
					} else {
						plugin.getLogger().info("Vault has been found but cannot be used! (unknown reason)");
					}
				}
				return false;
			}
		}
	}

	private boolean setupEconomy() {
		final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	public boolean setupPermissions() {
		final RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

}
