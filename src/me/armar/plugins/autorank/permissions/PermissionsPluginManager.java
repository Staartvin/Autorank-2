package me.armar.plugins.autorank.permissions;

import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.handlers.GroupManagerHandler;
import me.armar.plugins.autorank.permissions.handlers.PermissionsBukkitHandler;
import me.armar.plugins.autorank.permissions.handlers.VaultPermissionsHandler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/*
 * PermissionsPluginManager sort the tasks of removing/adding a player to a group depending
 * on the permissions plugin. 
 * For now it supports Vault and explicit GroupManager.
 *  
 */

/**
 * PermissionsPluginManager manages what permission handler should be given. It
 * just does basic checks of availability and calculates what permissions plugin
 * suits best.
 * 
 * It can choose from GroupManager, PermissionsBukkit and Vault.
 * 
 * @author Staartvin
 * 
 */
public class PermissionsPluginManager {

	private PermissionsHandler permissionPlugin;
	private final Autorank plugin;

	public PermissionsPluginManager(final Autorank plugin) {
		this.plugin = plugin;
		/*if (findVault(plugin)) {
			plugin.getLogger().info("Vault found and hooked!");
		} else {
			plugin.getLogger().severe("Vault was not found!");
		}*/
		searchPermPlugin();
	}

	protected boolean findGroupManager(final Autorank plugin) {
		final Plugin x = plugin.getServer().getPluginManager()
				.getPlugin("GroupManager");
		if (x != null) {
			return true;
		}
		return false;
	}

	protected boolean findPermissionsBukkit(final Autorank plugin) {
		final Plugin x = plugin.getServer().getPluginManager()
				.getPlugin("PermissionsBukkit");
		if (x != null) {
			return true;
		}
		return false;
	}

	public PermissionsHandler getPermissionPlugin() {
		return permissionPlugin;
	}

	private void searchPermPlugin() {
		if (findGroupManager(plugin)) {
			// use Groupmanager
			permissionPlugin = new GroupManagerHandler(plugin);
		} else if (findPermissionsBukkit(plugin)) {
			// Use PermissionsBukkit
			permissionPlugin = new PermissionsBukkitHandler(plugin);
		} else {
			// use Vault
			permissionPlugin = new VaultPermissionsHandler(plugin);
		}
	}

	/**
	 * Gets the primary permissions group a player is in.
	 * 
	 * @param player Player to get the group for.
	 * @return the primary permissions group.
	 */
	public String getPrimaryGroup(Player player) {
		// All groups of the player
		String[] groups = this.getPermissionPlugin().getPlayerGroups(player);
		// All ranks defined in the config
		Set<String> ranks = plugin.getConfigHandler().getRanks();

		if (groups.length == 1) {

			// Match with correct rank from config file.
			for (String rank : ranks) {
				if (rank.equalsIgnoreCase(groups[0])) {
					return rank;
				}
			}

			return groups[0];
		}

		// In no groups
		if (groups.length == 0)
			return null;

		for (String group : groups) {
			// Check for every group if it is defined in the config -> if so, it probably is the primary group

			for (String rank : ranks) {
				if (group.equalsIgnoreCase(rank)) {
					// Return rank name as in config
					return rank;
				}
			}
		}

		// Nothing found
		return null;
	}
}
