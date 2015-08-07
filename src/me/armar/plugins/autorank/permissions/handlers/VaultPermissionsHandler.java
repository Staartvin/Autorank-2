package me.armar.plugins.autorank.permissions.handlers;

import java.util.ArrayList;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author Staartvin
 *         VaultPermissionsHandler tackles all work that has to be done with
 *         Vault. (Most of the permissions plugins
 *         are supported with Vault)
 */
public class VaultPermissionsHandler implements PermissionsHandler {

	// TODO Vault and PEX cannot work together. Vault does not get the world groups properly and can't set the world groups properly.
	// FIX YOUR GOD DAMN PLUGIN, MILKBOWL.
	// TODO Fix the Vault issue with PermissionsEx.

	private static Permission permission = null;

	public VaultPermissionsHandler(final Autorank plugin) {
		if (!setupPermissions(plugin)) {

			// Only shutdown Autorank when Vault is needed and not found.
			// Delay shutdown so Autorank can start successfully.
			plugin.getServer().getScheduler()
					.runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {

							plugin.getLogger().severe(
									"Disabling Autorank: Vault was not found");
							plugin.getServer().getPluginManager()
									.disablePlugin(plugin);
						}
					}, 60L);
		}
	}

	/**
	 * Add a player to group
	 * 
	 * @param player Player to add
	 * @param world On a specific world
	 * @param group Group to add the player to
	 * @return true if done, false if failed
	 */
	@SuppressWarnings("deprecation")
	public boolean addGroup(final Player player, final String world,
			final String group) {
		if (permission == null)
			return false;

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
		if (permission == null) {
			return new String[10];
		}

		return permission.getGroups();
	}

	@Override
	public String[] getPlayerGroups(final Player player) {
		if (permission == null)
			return new String[10];

		String[] groups = null;

		Autorank plugin = (Autorank) Bukkit.getPluginManager().getPlugin(
				"Autorank");

		UUID uuid = player.getUniqueId();

		// Let players choose.
		if (plugin.getConfigHandler().onlyUsePrimaryGroupVault()) {
			groups = new String[] { permission.getPrimaryGroup(player) };
		} else {
			groups = permission.getPlayerGroups(player);
		}
		
		// Checking if player changed group
		// Check if the latest known group is the current group. Otherwise, reset progress
		String currentGroup = groups[0];
		String latestKnownGroup = plugin.getPlayerDataHandler()
				.getLastKnownGroup(uuid);

		if (latestKnownGroup == null) {
			plugin.getPlayerDataHandler().setLastKnownGroup(uuid, currentGroup);

			latestKnownGroup = currentGroup;
		}
		if (!latestKnownGroup.equalsIgnoreCase(currentGroup)) {
			// Reset progress and update latest known group
			plugin.getPlayerDataHandler().setPlayerProgress(uuid,
					new ArrayList<Integer>());
			plugin.getPlayerDataHandler().setLastKnownGroup(uuid, currentGroup);
			plugin.getPlayerDataHandler().setChosenPath(uuid, null);
			
			plugin.debugMessage("Reset player data for " + player.getName());
		}

		return groups;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String[] getWorldGroups(final Player player, final String world) {
		if (permission == null) {
			return new String[10];
		}

		return permission.getPlayerGroups(world, player.getName());
	}

	/**
	 * Remove a player from a group
	 * 
	 * @param player Player to remove
	 * @param world On a specific world
	 * @param group Group to remove the player from
	 * @return true if done, false if failed
	 */
	@SuppressWarnings("deprecation")
	public boolean removeGroup(final Player player, final String world,
			final String group) {
		if (permission == null)
			return false;

		return permission.playerRemoveGroup(world, player.getName(), group);
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

	private boolean setupPermissions(final Autorank plugin) {

		final Plugin vPlugin = plugin.getServer().getPluginManager()
				.getPlugin("Vault");

		if (vPlugin == null || !(vPlugin instanceof Vault)) {
			return false;
		}

		final RegisteredServiceProvider<Permission> permissionProvider = plugin
				.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);

		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}

		return permission != null;
	}
}
