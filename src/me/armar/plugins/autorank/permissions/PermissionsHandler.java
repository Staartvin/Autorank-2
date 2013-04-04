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
	
	public boolean replaceGroup(Player player, String world, String groupFrom, String groupTo);
	
	public String[] getGroups();

}
