package me.armar.plugins.autorank.permissions;

import java.util.List;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/*
 * PermissionsPluginHandler sort the tasks of removing/adding a player to a group depending
 * on the permissions plugin. 
 * For now it supports Vault and explicit GroupManager.
 * 
 * 
 * 
 * 
 */

public class PermissionsPluginHandler {

	private Autorank plugin;
	private GroupManagerHandler groupManagerHandler;
	private VaultPermissionsHandler vPermissionsHandler;
	
	public PermissionsPluginHandler(Autorank plugin) {
		this.plugin = plugin;
	}
	public String[] getPlayerGroups(Player player) {
		if (plugin.getPermissionsHandler().findGroupManager(plugin))  {
			
			if (groupManagerHandler == null) {
				groupManagerHandler = new GroupManagerHandler(plugin);	
			}
			List<String> groups = groupManagerHandler.getGroups(player);
			String[] array = (String[]) groups.toArray();
			return array;
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
		// There is no GroupManager method for that yet.
		Bukkit.getLogger().info("Player: " + player);
		Bukkit.getLogger().info("World: " + world);
		Bukkit.getLogger().info("OldGroup: " + oldGroup);
		Bukkit.getLogger().info("NewGroup: " + newGroup);
		Bukkit.getLogger().info("vPermissionsHandler: " + vPermissionsHandler);
		
		if (plugin.getPermissionsHandler().findGroupManager(plugin))  {
			return groupManagerHandler.addPlayerToGroup(player, newGroup);
		} else {
			return vPermissionsHandler.replaceGroup(player, world, oldGroup, newGroup);
		}
	}

/*	public boolean removeGroup(Player player, String world, String group) {
		return vPermissionsHandler.removeGroup(player, world, group);
	}

	public boolean addGroup(Player player, String world, String group) {
		return vPermissionsHandler.addGroup(player, world, group);
	} */
	

}
