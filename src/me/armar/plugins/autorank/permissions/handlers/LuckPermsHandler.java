package me.armar.plugins.autorank.permissions.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author Staartvin This is a special permission handler that handles all work
 * from LuckPerms
 */
public class LuckPermsHandler extends PermissionsHandler {

    private LuckPerms luckPermsApi;

    public LuckPermsHandler(final Autorank plugin) {
        super(plugin);
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
            getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(),
                    "lp user " + player.getName() + " parent add " + group + " global " + world);
        } else {
            getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(),
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
            getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(),
                    "lp user " + player.getName() + " parent remove " + group + " global " + world);
        } else {
            getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(),
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
        return Collections.unmodifiableCollection(
                luckPermsApi.getGroupManager().getLoadedGroups().stream()
                        .map(Group::getName).collect(Collectors.toList()));
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

        User user = luckPermsApi.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return new ArrayList<>();
        }

        return Collections.unmodifiableCollection(user.getDistinctNodes().parallelStream()
                .filter(node -> node instanceof InheritanceNode)
                .map(node -> ((InheritanceNode) node).getGroupName())
                .collect(Collectors.toList()));
    }

    @Override
    public Collection<String> getWorldGroups(final Player player, final String world) {
        // The API is not very clear on how to check for world groups, so I'm ignoring that for now.
        return this.getPlayerGroups(player);
    }


    @Override
    public boolean replaceGroup(final Player player, final String world, final String oldGroup, final String newGroup) {
        return (addGroup(player, world,
                newGroup) && removeGroup(player, world, oldGroup));
    }

    @Override
    public boolean setupPermissionsHandler() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) luckPermsApi = provider.getProvider();

        return luckPermsApi != null;
    }
}
