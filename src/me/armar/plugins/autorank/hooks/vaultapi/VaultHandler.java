package me.armar.plugins.autorank.hooks.vaultapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Handles all connections with Vault
 * <p>
 * Date created: 21:02:20 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class VaultHandler implements DependencyHandler {

	private final Autorank plugin;
	private Vault api;
	public static Economy economy = null;

	public VaultHandler(final Autorank instance) {
		plugin = instance;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin("Vault");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof Vault)) {
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
			plugin.getLogger().info("Vault has not been found!");
			return false;
		} else {
			api = (Vault) get();

			if (api != null && setupEconomy()) {
				plugin.getLogger()
						.info("Vault has been found and can be used!");
				return true;
			} else {
				plugin.getLogger().info(
						"Vault has been found but cannot be used!");
				return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		Vault plugin = (Vault) get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return api != null;
	}
	
	private boolean setupEconomy() {
		final RegisteredServiceProvider<Economy> economyProvider = Bukkit
				.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}
}
