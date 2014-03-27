package me.armar.plugins.autorank.permissions.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author Staartvin
 *         VaultPermissionsHandler tackles all work that has to be done with
 *         Vault. (Most of the permissions plugins
 *         are supported with Vault)
 */
public class VaultPermissionsHandler implements PermissionsHandler {

	// TODO Vault and PEX cannot work together. Vault does not get the world groups properly and can't set the world groups properly.
	// FIX YOUR GOD DAMN PLUGIN, MILKBOW. 
	// TODO Fix the Vault issue with PermissionsEx.

	private static Permission permission = null;

	public VaultPermissionsHandler(final Autorank plugin) {
		if (!setupPermissions(plugin)) {
			plugin.getLogger().severe("Vault not found, Autorank will not work!");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
	}

	private Boolean setupPermissions(final Autorank plugin) {
		final RegisteredServiceProvider<Permission> permissionProvider = plugin
				.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	@Override
	public String[] getPlayerGroups(final Player player) {
		return permission.getPlayerGroups(player);
	}

	@Override
	public boolean replaceGroup(final Player player, String world,
			final String oldGroup, final String newGroup) {
		// Temporary fix for bPermissions
		if (world == null
				&& permission.getName().toLowerCase().contains("bpermissions")) {
			world = player.getWorld().getName();
		}
		/*System.out.print("Group To: " + newGroup);
		System.out.print("Group From: " + oldGroup);
		System.out.print("World: " + world);
		System.out.print("Player: " + player);*/

		final boolean worked1 = addGroup(player, world, newGroup);
		final boolean worked2 = removeGroup(player, world, oldGroup);

		//System.out.print("Worked1: " + worked1);
		//System.out.print("Worked2: " + worked2);

		//System.out.print("In group: " + permission.playerInGroup(world, player.getName(), newGroup));
		return worked1 && worked2;
	}

	/**
	 * Remove a player from a group
	 * 
	 * @param player Player to remove
	 * @param world On a specific world
	 * @param group Group to remove the player from
	 * @return true if done, false if failed
	 */
	public boolean removeGroup(final Player player, final String world,
			final String group) {
		return permission.playerRemoveGroup(world, player.getName(), group);
	}

	/**
	 * Add a player to group
	 * 
	 * @param player Player to add
	 * @param world On a specific world
	 * @param group Group to add the player to
	 * @return true if done, false if failed
	 */
	public boolean addGroup(final Player player, final String world,
			final String group) {
		return permission.playerAddGroup(world, player.getName(), group);
	}

	/**
	 * Get all known groups
	 * 
	 * @return an array of strings containing all setup groups of the
	 *         permissions plugin.
	 */
	@Override
	public String[] getGroups() {
		return permission.getGroups();
	}

	@Override
	public String[] getWorldGroups(final Player player, final String world) {
		return permission.getPlayerGroups(world, player.getName());
	}
}
