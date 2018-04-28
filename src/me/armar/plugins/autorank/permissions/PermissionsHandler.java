package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.entity.Player;

import java.util.Collection;

/*
 * PermissionsHandler takes care of the communications with the permissions
 * plugin.
 *
 */
public abstract class PermissionsHandler {

    private Autorank plugin;

    public PermissionsHandler(Autorank plugin) {
        this.plugin = plugin;
        this.setupPermissionsHandler();
    }

    /**
     * Get all defined groups
     *
     * @return an array of all groups defined in the config(s) of the permission
     * plugin.
     */
    public abstract Collection<String> getGroups();

    /**
     * Get the name of the permissions plugin.
     *
     * @return
     */
    public abstract String getName();

    /**
     * Get the permission groups of a player.
     *
     * @param player Player to use
     * @return a list of permission groups names
     */
    public abstract Collection<String> getPlayerGroups(Player player);

    /**
     * Get the groups of the player in a world.
     *
     * @param player Player to get the groups from
     * @param world  World to get the world from
     * @return an array containing all groups that the player is in.
     */
    public abstract Collection<String> getWorldGroups(Player player, String world);

    /**
     * Remove a group from a player and then add one to the player.
     *
     * @param player       Player to use
     * @param world        World to use, can be null
     * @param deletedGroup Group to remove
     * @param addedGroup   Group to add
     * @return true if it worked, false otherwise.
     */
    public abstract boolean replaceGroup(Player player, String world, String deletedGroup, String addedGroup);

    /**
     * Set up the permissions handler so that it can be used to handle requests.
     *
     * @return true when the permissions handler was properly set up. False otherwise.
     */
    public abstract boolean setupPermissionsHandler();

    public Autorank getPlugin() {
        return plugin;
    }
}
