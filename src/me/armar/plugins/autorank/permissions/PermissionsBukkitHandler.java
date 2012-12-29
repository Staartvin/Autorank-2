package me.armar.plugins.autorank.permissions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class PermissionsBukkitHandler implements PermissionsPluginHandler{
    
    private PermissionsPlugin plugin;
    private Server server;

    public PermissionsBukkitHandler(PermissionsPlugin x) {
	this.plugin = x;
	this.server = Bukkit.getServer();
    }

    @Override
    public String[] getPlayerGroups(Player player) {
	List<String> res = new ArrayList<String>();
	for(Group g : plugin.getGroups(player.getName())){
	    res.add(g.getName());
	}
	return (String[]) res.toArray();
    }

    @Override
    public boolean replaceGroup(Player player, String world, String oldGroup, String newGroup) {
	removeGroup(player, world, oldGroup);
	addGroup(player, world, newGroup);
	return true;
    }

    @Override
    public boolean removeGroup(Player player, String world, String group) {
	server.dispatchCommand(server.getConsoleSender(), "permissions player removegroup " + group);
	return true;
    }

    @Override
    public boolean addGroup(Player player, String world, String group) {
	server.dispatchCommand(server.getConsoleSender(), "permissions player addgroup " + group);
	return true;
    }

}
