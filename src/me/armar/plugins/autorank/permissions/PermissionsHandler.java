package me.armar.plugins.autorank.permissions;

import org.bukkit.entity.Player;

/*
 * PermissionsHandler takes care of the communications with the permissions
 * plugin.
 * 
 */
public interface PermissionsHandler {

    /**
     * Sometimes replaceGroup does not work. You can then try to do it the
     * reverse way, by demoting someone.
     * 
     * @param player
     *            Player to demote
     * @param world
     *            On which world should we demote him? (null if every world)
     * @param groupFrom
     *            What is the group he's currently in
     * @param groupTo
     *            What is the group you want the player to demote to.
     * @return true if properly demoted, false otherwise.
     */
    public boolean demotePlayer(Player player, String world, String groupFrom, String groupTo);

    /**
     * Get all defined groups
     * 
     * @return an array of all groups defined in the config(s) of the permission
     *         plugin.
     */
    public String[] getGroups();

    /**
     * Get the name of the permissions plugin.
     * 
     * @return
     */
    public String getName();

    /**
     * Get the permission groups of a player.
     * 
     * @param player
     *            Player to use
     * @return a list of permission groups names
     */
    public String[] getPlayerGroups(Player player);

    /**
     * Get the groups of the player in a world.
     * 
     * @param player
     *            Player to get the groups from
     * @param world
     *            World to get the world from
     * @return an array containing all groups that the player is in.
     */
    public String[] getWorldGroups(Player player, String world);

    /**
     * Remove a group from a player and then add one to the player.
     * 
     * @param player
     *            Player to use
     * @param world
     *            World to use, can be null
     * @param deletedGroup
     *            Group to remove
     * @param addedGroup
     *            Group to add
     * @return true if it worked, false otherwise.
     */
    public boolean replaceGroup(Player player, String world, String deletedGroup, String addedGroup);

}
