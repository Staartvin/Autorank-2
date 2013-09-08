package me.armar.plugins.autorank.permissions;

import org.bukkit.entity.Player;
/*
 * PermissionsHandler takes care of the communications with the permissions 
 * plugin.
 * 
 */
public interface PermissionsHandler {


	public String[] getPlayerGroups(Player player);
	
	public boolean replaceGroup(Player player, String world, String groupFrom, String groupTo);
	
	/**
	 * Get all defined groups
	 * @return an array of all groups defined in the config(s) of the permission plugin.
	 */
	public String[] getGroups();
	
	/**
	 * Gets the groups of the player in a world
	 * @param player Player to get the groups from
	 * @param world World to get the world from
	 * @return an array containing all groups that the player is in.
	 */
	public String[] getWorldGroups(Player player, String world);

}
