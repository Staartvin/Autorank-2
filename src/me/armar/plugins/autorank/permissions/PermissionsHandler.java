package me.armar.plugins.autorank.permissions;

import org.bukkit.entity.Player;

//import ru.tehkode.permissions.bukkit.PermissionsEx;

//import com.platymuus.bukkit.permissions.PermissionsPlugin;

/*
 * PermissionsHandler takes care of the communications with the permissions 
 * plugin.
 * 
 */
public interface PermissionsHandler {


	public String[] getPlayerGroups(Player player);
	
	public boolean removeGroup(Player player, String world, String group);
	
	public boolean addGroup(Player player, String world, String group);
	
	public String[] getGroups();

}
