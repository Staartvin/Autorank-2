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
public class VaultPermissionsHandler {

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
		return (addGroup(player, world, newGroup) && removeGroup(player, world,
				oldGroup));
	}

	public boolean removeGroup(Player player, String world, String group) {
		return permission.playerRemoveGroup(world, player.getName(), group);
	}

	public boolean addGroup(Player player, String world, String group) {
		return permission.playerAddGroup(world, player.getName(), group);
	}

}
