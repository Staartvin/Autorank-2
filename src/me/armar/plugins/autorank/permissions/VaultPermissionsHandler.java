package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author Staartvin
 * VaultPermissionsHandler tackles all work that has to be done with Vault. (Most of the permissions plugins
 * are supported with Vault)
 */
public class VaultPermissionsHandler implements PermissionsHandler {

	private static Permission permission = null;

	public VaultPermissionsHandler(Autorank plugin) {
		if (!setupPermissions(plugin)) {
			Autorank.logMessage("Vault not found, Autorank will not work!");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
	}

	private Boolean setupPermissions(Autorank plugin) {
		RegisteredServiceProvider<Permission> permissionProvider = plugin
				.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	public String[] getPlayerGroups(Player player) {
		return permission.getPlayerGroups(player);
	}

	public boolean replaceGroup(Player player, String world, String oldGroup,
			String newGroup) {
		// Temporary fix for bPermissions
		if (world == null && permission.getName().contains("bpermissions")) {
			world = player.getWorld().getName();
		}
		return (addGroup(player, world, newGroup) && removeGroup(player, world,
				oldGroup));
	}

	/**
	 * Remove a player from a group
	 * @param player Player to remove
	 * @param world On a specific world
	 * @param group Group to remove the player from
	 * @return true if done, false if failed
	 */
	public boolean removeGroup(Player player, String world, String group) {
		return permission.playerRemoveGroup(world, player.getName(), group);
	}

	/**
	 * Add a player to group
	 * @param player Player to add
	 * @param world On a specific world
	 * @param group Group to add the player to
	 * @return true if done, false if failed
	 */
	public boolean addGroup(Player player, String world, String group) {
		return permission.playerAddGroup(world, player.getName(), group);
	}

	/**
	 * Get all known groups
	 * @return an array of strings containing all setup groups of the permissions plugin.
	 */
	public String[] getGroups() {
		return permission.getGroups();
	}
}
