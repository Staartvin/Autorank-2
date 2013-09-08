package me.armar.plugins.autorank.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import me.armar.plugins.autorank.Autorank;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * @author Staartvin
 *	This file handles all work done with GroupManager.
 */
public class GroupManagerHandler implements PermissionsHandler {

	private Autorank plugin;
	private GroupManager groupManager;
	
	public GroupManagerHandler(Autorank plugin) {
		this.plugin = plugin;
		setupGroupManager();
	}

	public void setupGroupManager() {
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		Plugin GMplugin = pluginManager.getPlugin("GroupManager");
		
		if (GMplugin != null && GMplugin.isEnabled())
		{
			groupManager = (GroupManager)GMplugin;
 
		}
	}
	
	public String getGroup(Player player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player);
		if (handler == null)
		{
			return null;
		}
		return handler.getGroup(player.getName());
	}
	
	public boolean setGroup(Player player, String group, String world)
	{
		OverloadedWorldHolder handler;
		
		if (world != null) {
			handler = groupManager.getWorldsHolder().getWorldData(world);
		} else {
			handler = groupManager.getWorldsHolder().getWorldData(player);
		}
		if (handler == null)
		{
			return false;
		}
		handler.getUser(player.getName()).setGroup(handler.getGroup(group));
		return true;
	}
 
	public String[] getPlayerGroups(Player player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player);
		if (handler == null)
		{
			return null;
		}
		List<String> groups = Arrays.asList(handler.getPrimaryGroup(player.getName()));
		String[] array = (String[]) groups.toArray();
		
		return array;
	}
 
	public String getPrefix(Player player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player);
		if (handler == null)
		{
			return null;
		}
		return handler.getUserPrefix(player.getName());
	}
 
	public String getSuffix(Player player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player);
		if (handler == null)
		{
			return null;
		}
		return handler.getUserSuffix(player.getName());
	}
 
	public boolean hasPermission( Player player,  String node)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player);
		if (handler == null)
		{
			return false;
		}
		return handler.has(player, node);
	}
	
	public boolean replaceGroup(Player player, String world, String groupFrom, String groupTo) {
		return setGroup(player, groupTo, world);
		
	}
	
	public String[] getGroups() {
		List<String> groups = new ArrayList<String>();
		
		for (World world:plugin.getServer().getWorlds()) {
			String worldName = world.getName();
			Collection<Group> worldGroup = groupManager.getWorldsHolder().getWorldData(worldName).getGroupList();
			List<Group> list  = new ArrayList<Group>(worldGroup);
			for (Group group:list) {
				groups.add(group.getName());
			}
		}
		String[] groupArray = new String[groups.size()];
		
		// Repopulate the empty array.
		for (int i=0;i<groups.size();i++) {
			groupArray[i] = groups.get(i);
		}
		return groupArray;
	}

	@Override
	public String[] getWorldGroups(Player player, String world) {
		return groupManager.getWorldsHolder().getWorldPermissions(world).getGroups(player.getName());
	}
}
