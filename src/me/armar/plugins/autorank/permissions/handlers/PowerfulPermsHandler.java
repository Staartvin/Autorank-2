package me.armar.plugins.autorank.permissions.handlers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.github.cheesesoftware.PowerfulPermsAPI.PermissionManager;
import com.github.cheesesoftware.PowerfulPermsAPI.PowerfulPermsPlugin;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;

/**
 * @author Staartvin
 *         This is a special permission handler that handles all work from
 *         PowerfulPerms
 */
public class PowerfulPermsHandler implements PermissionsHandler {

	private PowerfulPermsPlugin powerfulPerms;
	private final Autorank plugin;

	public PowerfulPermsHandler(final Autorank plugin) {
		this.plugin = plugin;
		setup();
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
		// No known way to set via API, hence we do it the ugly route (via commands).
		plugin.getServer()
				.dispatchCommand(
						plugin.getServer().getConsoleSender(),
						"pp user " + player.getName() + " setrank "
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
	@Override
	public String[] getGroups() {
		PermissionManager permManager = powerfulPerms.getPermissionManager();
		final Map<Integer, com.github.cheesesoftware.PowerfulPermsAPI.Group> groups = permManager.getGroups();
		final String[] newGroups = new String[groups.size()];

		int count = 0;
		
		for (Entry<Integer, com.github.cheesesoftware.PowerfulPermsAPI.Group> entry: groups.entrySet()) {
			newGroups[count] = entry.getValue().getName();
			count++;
		}

		return newGroups;
	}

	@Override
	public String[] getPlayerGroups(final Player player) {
		PermissionManager permManager = powerfulPerms.getPermissionManager();
		final List<com.github.cheesesoftware.PowerfulPermsAPI.Group> groups = permManager.getPermissionPlayer(player.getUniqueId()).getGroups();
		final String[] newGroups = new String[groups.size()];

		for (int i = 0; i < groups.size(); i++) {
			newGroups[i] = groups.get(0).getName();
		}

		return newGroups;
	}

	@Override
	public String[] getWorldGroups(final Player player, final String world) {
		return this.getPlayerGroups(player); // No known world conversion.
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
		// No known way to set via API, hence we do it the ugly route (via commands).
		plugin.getServer().dispatchCommand(
				plugin.getServer().getConsoleSender(),
				"pp user " + player.getName() + " removegroup "
						+ group);
		return true;
		// There is no way to check if the command was successful.
	}

	@Override
	public boolean replaceGroup(final Player player, final String world,
			final String oldGroup, final String newGroup) {
		return (addGroup(player, world, newGroup) && removeGroup(player, world,
				oldGroup));
	}

	private boolean setup() {
		final PluginManager pluginManager = plugin.getServer()
				.getPluginManager();
		final Plugin permPlugin = pluginManager.getPlugin("PowerfulPerms");

		if (permPlugin != null && permPlugin.isEnabled()) {
			powerfulPerms = (PowerfulPermsPlugin) permPlugin;
		}

		return powerfulPerms != null;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.permissions.PermissionsHandler#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "PowerfulPerms";
	}

	@Override
	public boolean demotePlayer(Player player, String world, String groupFrom, String groupTo) {
		return (addGroup(player, world, groupTo) && removeGroup(player, world,
				groupFrom));
	}
}
