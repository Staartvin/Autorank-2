package me.armar.plugins.autorank.permissions.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;

/**
 * @author Staartvin
 *         This file handles all work done with GroupManager.
 */
public class GroupManagerHandler implements PermissionsHandler {

	private GroupManager groupManager;
	private final Autorank plugin;

	public GroupManagerHandler(final Autorank plugin) {
		this.plugin = plugin;
		setupGroupManager();
	}

	public String getGroup(final Player player) {
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder()
				.getWorldPermissions(player);
		if (handler == null) {
			return null;
		}
		return handler.getGroup(player.getName());
	}

	@Override
	public String[] getGroups() {
		final List<String> groups = new ArrayList<String>();

		for (final World world : plugin.getServer().getWorlds()) {
			final String worldName = world.getName();
			final Collection<Group> worldGroup = groupManager.getWorldsHolder()
					.getWorldData(worldName).getGroupList();
			final List<Group> list = new ArrayList<Group>(worldGroup);
			for (final Group group : list) {
				groups.add(group.getName());
			}
		}
		final String[] groupArray = new String[groups.size()];

		// Repopulate the empty array.
		for (int i = 0; i < groups.size(); i++) {
			groupArray[i] = groups.get(i);
		}
		return groupArray;
	}

	@Override
	public String[] getPlayerGroups(final Player player) {
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder()
				.getWorldPermissions(player);
		if (handler == null) {
			return null;
		}
		final List<String> groups = Arrays.asList(handler
				.getPrimaryGroup(player.getName()));
		final String[] array = (String[]) groups.toArray();

		final UUID uuid = player.getUniqueId();

		// Checking if player changed group
		// Check if the latest known group is the current group. Otherwise, reset progress
		final String currentGroup = array[0];
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

		return array;
	}

	public String getPrefix(final Player player) {
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder()
				.getWorldPermissions(player);
		if (handler == null) {
			return null;
		}
		return handler.getUserPrefix(player.getName());
	}

	public String getSuffix(final Player player) {
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder()
				.getWorldPermissions(player);
		if (handler == null) {
			return null;
		}
		return handler.getUserSuffix(player.getName());
	}

	@Override
	public String[] getWorldGroups(final Player player, final String world) {
		return groupManager.getWorldsHolder().getWorldPermissions(world)
				.getGroups(player.getName());
	}

	public boolean hasPermission(final Player player, final String node) {
		final AnjoPermissionsHandler handler = groupManager.getWorldsHolder()
				.getWorldPermissions(player);
		if (handler == null) {
			return false;
		}
		return handler.has(player, node);
	}

	@Override
	public boolean replaceGroup(final Player player, final String world,
			final String groupFrom, final String groupTo) {
		return setGroup(player, groupTo, world);
	}

	public boolean setGroup(final Player player, final String group,
			final String world) {
		OverloadedWorldHolder handler;

		if (world != null) {
			handler = groupManager.getWorldsHolder().getWorldData(world);
		} else {
			handler = groupManager.getWorldsHolder().getWorldData(player);
		}
		if (handler == null) {
			return false;
		}
		handler.getUser(player.getName()).setGroup(handler.getGroup(group));
		return true;
	}

	public boolean setupGroupManager() {
		final PluginManager pluginManager = plugin.getServer()
				.getPluginManager();
		final Plugin GMplugin = pluginManager.getPlugin("GroupManager");

		if (GMplugin != null && GMplugin.isEnabled()) {
			groupManager = (GroupManager) GMplugin;

		}

		return groupManager != null;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.permissions.PermissionsHandler#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "GroupManager";
	}

	@Override
	public boolean demotePlayer(Player player, String world, String groupFrom, String groupTo) {
		return setGroup(player, groupTo, world);
	}
}
