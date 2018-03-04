package me.armar.plugins.autorank.permissions.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Staartvin This is a special permission handler that handles all work
 * from LuckPerms
 */
public class LuckPermsHandler extends PermissionsHandler {

    private final Autorank plugin;
    private LuckPermsApi luckPermsApi = null;

    public LuckPermsHandler(final Autorank plugin) {
        this.plugin = plugin;
        this.setupPermissionsHandler();
    }

    /**
     * Add a player to group
     *
     * @param player Player to add
     * @param world  On a specific world
     * @param group  Group to add the player to
     * @return true if done, false if failed
     */
    public boolean addGroup(final Player player, final String world, final String group) {
        // No known way to set via API, hence we do it the ugly route (via
        // commands).

        if (world != null) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "lp user " + player.getName() + " parent add " + group + " global " + world);
        } else {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "lp user " + player.getName() + " parent add " + group);
        }

        return true;
        // There is no way to check if the command was successful.
    }

    /**
     * Remove a player from a group
     *
     * @param player Player to remove
     * @param world  On a specific world
     * @param group  Group to remove the player from
     * @return true if done, false if failed
     */
    public boolean removeGroup(final Player player, final String world, final String group) {
        // No known way to set via API, hence we do it the ugly route (via
        // commands).`

        if (world != null) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "lp user " + player.getName() + " parent remove " + group + " global " + world);
        } else {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    "lp user " + player.getName() + " parent remove " + group);
        }

        return true;
        // There is no way to check if the command was successful.
    }

    /**
     * Get all known groups
     *
     * @return an array of strings containing all setup groups of the
     * permissions plugin.
     */
    @Override
    public Collection<String> getGroups() {

        List<String> groupNames = new ArrayList<>();

        for (me.lucko.luckperms.api.Group luckPermGroup : luckPermsApi.getGroups()) {
            groupNames.add(luckPermGroup.getName());
        }

        return Collections.unmodifiableCollection(groupNames);
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.permissions.PermissionsHandler#getName()
     */
    @Override
    public String getName() {
        return "LuckPerms";
    }

    @Override
    public Collection<String> getPlayerGroups(final Player player) {
        User user = luckPermsApi.getUser(player.getUniqueId());

        if (user == null) {
            return new ArrayList<>();
        }

        List<String> groupNames = new ArrayList<>();

        for (Node luckPermsNode : user.getAllNodes()) {
            if (luckPermsNode.isGroupNode()) {
                groupNames.add(luckPermsNode.getGroupName());
            }
        }

        return Collections.unmodifiableCollection(groupNames);
    }

    @Override
    public Collection<String> getWorldGroups(final Player player, final String world) {
        User user = luckPermsApi.getUser(player.getUniqueId());

        if (user == null) {
            return new ArrayList<>();
        }

        List<String> groupNames = new ArrayList<>();

        for (Node luckPermsNode : user.getAllNodes()) {
            if (luckPermsNode.isGroupNode()) {

                // If group is not world specific, it also applies to the given world and hence should be added.
                if (!luckPermsNode.isWorldSpecific()) {
                    groupNames.add(luckPermsNode.getGroupName());
                    continue;
                }

                Optional<String> validOnWorld = luckPermsNode.getWorld();

                // World is unknown and so not world specific.
                if (!validOnWorld.isPresent()) {
                    groupNames.add(luckPermsNode.getGroupName());
                } else {
                    // World is not unknown and we should check if it matches the given world.
                    if (validOnWorld.get().equals(world)) {
                        groupNames.add(luckPermsNode.getGroupName());
                    }
                }

            }
        }

        return Collections.unmodifiableCollection(groupNames);
    }


    @Override
    public boolean replaceGroup(final Player player, final String world, final String oldGroup, final String newGroup) {
        return (addGroup(player, world,
                newGroup) && removeGroup(player, world, oldGroup));
    }

    @Override
    public boolean setupPermissionsHandler() {
        Optional<LuckPermsApi> optional = LuckPerms.getApiSafe();

        optional.ifPresent(luckPermsApi1 -> luckPermsApi = luckPermsApi1);

        return luckPermsApi != null;
    }
}
