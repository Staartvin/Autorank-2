package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import net.milkbowl.vault.Vault;
import org.bukkit.plugin.Plugin;

/*
 * PermissionsPluginHandler sort the tasks of removing/adding a player to a group depending
 * on the permissions plugin. 
 * For now it supports Vault and explicit GroupManager.
 *  
 */

public class PermissionsPluginHandler {

	private Autorank plugin;
	private PermissionsHandler permissionPlugin;
	
	public PermissionsPluginHandler(Autorank plugin) {
		this.plugin = plugin;
		if (findVault(plugin)) {
			Autorank.logMessage("Vault Hooked!");
		} else {
			Autorank.logMessage("WARNING Vault was not found!");
		}
		searchPermPlugin();
	}
	
	private void searchPermPlugin() {
		if (findGroupManager(plugin)) {
			// use Groupmanager
			permissionPlugin = new GroupManagerHandler(plugin);
		} else {
			permissionPlugin = new VaultPermissionsHandler(plugin);
			// use Vault
		}
	}
	
	public PermissionsHandler getPermissionPlugin() {
		return permissionPlugin;
	}
	
	protected boolean findVault(Autorank plugin) {
		Plugin x = plugin.getServer().getPluginManager().getPlugin("Vault");
		if (x != null & x instanceof Vault) {
			return true;
		}
		return false;
	}
	
	protected boolean findGroupManager(Autorank plugin) {
		Plugin x = plugin.getServer().getPluginManager().getPlugin("GroupManager");
		if (x != null) {
			return true;
		}
		return false;
	}
}
