package me.armar.plugins.autorank.permissions;

import java.util.Arrays;
import java.util.List;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.armar.plugins.autorank.Autorank;

public class GroupManagerHandler {

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
	
	public boolean setGroup(Player player,  String group)
	{
		OverloadedWorldHolder handler = groupManager.getWorldsHolder().getWorldData(player);
		if (handler == null)
		{
			return false;
		}
		handler.getUser(player.getName()).setGroup(handler.getGroup(group));
		return true;
	}
 
	public List<String> getGroups( Player player)
	{
		AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(player);
		if (handler == null)
		{
			return null;
		}
		return Arrays.asList(handler.getPrimaryGroup(player.getName()));
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
 
	public String getSuffix( Player player)
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
	
	public boolean addPlayerToGroup(Player player, String groupName) {
		return setGroup(player, groupName);
		
	}
}
