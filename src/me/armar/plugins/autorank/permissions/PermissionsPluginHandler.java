package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import net.milkbowl.vault.Vault;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/*
 * PermissionsPluginHandler sort the tasks of removing/adding a player to a group depending
 * on the permissions plugin. 
 * For now it supports Vault and explicit GroupManager.
 *  
 */

public class PermissionsPluginHandler {

	private Autorank plugin;
	private GroupManagerHandler groupManagerHandler;
	private VaultPermissionsHandler vPermissionsHandler;
	
	public PermissionsPluginHandler(Autorank plugin) {
		this.plugin = plugin;
		if (findVault(plugin)) {
			Autorank.logMessage("Vault Hooked!");
		} else {
			Autorank.logMessage("WARNING Vault was not found!");
			//this.plugin = plugin;
		}
	}
	public String[] getPlayerGroups(Player player) {
		if (findGroupManager(plugin))  {
			
			if (groupManagerHandler == null) {
				groupManagerHandler = new GroupManagerHandler(plugin);	
			}
			return groupManagerHandler.getPlayerGroups(player);
		}
		else {
			if (vPermissionsHandler == null) {
				vPermissionsHandler =  new VaultPermissionsHandler(plugin);	
			}
			return vPermissionsHandler.getPlayerGroups(player);
		}
	}

	public boolean replaceGroup(Player player, String world, String oldGroup,
			String newGroup) {
		if (findGroupManager(plugin))  {
			if (groupManagerHandler == null) {
				groupManagerHandler = new GroupManagerHandler(plugin);	
			}
			return groupManagerHandler.addPlayerToGroup(player, newGroup);
		} else {
			if (vPermissionsHandler == null) {
				vPermissionsHandler =  new VaultPermissionsHandler(plugin);	
			}
			return vPermissionsHandler.replaceGroup(player, world, oldGroup, newGroup);
		}
	}
	
	public String[] getGroups() {
		if (findGroupManager(plugin))  {
			if (groupManagerHandler == null) {
				groupManagerHandler = new GroupManagerHandler(plugin);	
			}
			return groupManagerHandler.getGroups();
		} else {
			if (vPermissionsHandler == null) {
				vPermissionsHandler =  new VaultPermissionsHandler(plugin);	
			}
			return vPermissionsHandler.getGroups();
		}
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
	
/*	public boolean removeGroup(Player player, String world, String group) {
		return vPermissionsHandler.removeGroup(player, world, group);
	}

	public boolean addGroup(Player player, String world, String group) {
		return vPermissionsHandler.addGroup(player, world, group);
	} */
	

}
