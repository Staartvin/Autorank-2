package me.armar.plugins.autorank.permissions;

import java.util.List;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

/**
 * @author Staartvin
 *         This is a special permission handler that handles all work from
 *         PermissionsBukkit
 */
public class PermissionsBukkitPermissionsHandler implements PermissionsHandler {

	private PermissionsPlugin permissionsBukkit;
	private Autorank plugin;

	public PermissionsBukkitPermissionsHandler(Autorank plugin) {
		this.plugin = plugin;
		setupPermissionsBukkit();
	}

	private Boolean setupPermissionsBukkit() {
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		Plugin permBukkit = pluginManager.getPlugin("PermissionsBukkit");

		if (permBukkit != null && permBukkit.isEnabled()) {
			permissionsBukkit = (PermissionsPlugin) permBukkit;
		}
		
		return permissionsBukkit != null;
	}

	public String[] getPlayerGroups(Player player) {
		List<Group> groups = permissionsBukkit.getGroups(player.getName());
		String[] newGroups = new String[groups.size()];

		for (int i = 0; i < groups.size(); i++) {
			newGroups[i] = groups.get(i).getName();
		}

		return newGroups;
	}

	public boolean replaceGroup(Player player, String world, String oldGroup,
			String newGroup) {
		return (addGroup(player, world, newGroup) && removeGroup(player, world,
				oldGroup));
	}

	/**
	 * Remove a player from a group
	 * 
	 * @param player Player to remove
	 * @param world On a specific world
	 * @param group Group to remove the player from
	 * @return true if done, false if failed
	 */
	public boolean removeGroup(Player player, String world, String group) {
		// PermissionsBukkit doesn't have a method to set the actual group. Therefore we need to do it with commands...
		// Come on PermBukkit. Fix your API..
		plugin.getServer().dispatchCommand(
				plugin.getServer().getConsoleSender(),
				"permissions player removegroup " + player.getName() + " "
						+ group);
		return true;
		// There is no way to check if the command was successful.
	}

	/**
	 * Add a player to group
	 * 
	 * @param player Player to add
	 * @param world On a specific world
	 * @param group Group to add the player to
	 * @return true if done, false if failed
	 */
	public boolean addGroup(Player player, String world, String group) {
		// PermissionsBukkit doesn't have a method to set the actual group. Therefore we need to do it with commands...
		// Come on PermBukkit. Fix your API..
		plugin.getServer().dispatchCommand(
				plugin.getServer().getConsoleSender(),
				"permissions player addgroup " + player.getName() + " "
						+ group);
		return true;
		// There is no way to check if the command was successful.
	}

	/**
	 * Get all known groups
	 * 
	 * @return an array of strings containing all setup groups of the
	 *         permissions plugin.
	 */
	public String[] getGroups() {
		List<Group> groups = permissionsBukkit.getAllGroups();
		String[] newGroups = new String[groups.size()];

		for (int i = 0; i < groups.size(); i++) {
			newGroups[i] = groups.get(i).getName();
		}
		
		return newGroups;
	}

	@Override
	public String[] getWorldGroups(Player player, String world) {
		List<Group> groups = permissionsBukkit.getGroups(player.getName());
		String[] arrayGroups = new String[groups.size()];
		
		for (int i=0;i<groups.size(); i++) {
			arrayGroups[i] = groups.get(i).getName();
		}
		
		return arrayGroups;
	}
}
