package me.armar.plugins.autorank.permissions.handlers;

import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.LibraryHook;
import me.staartvin.plugins.pluginlibrary.hooks.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency;
import me.armar.plugins.autorank.hooks.vaultapi.PluginLibraryHandler;
import me.armar.plugins.autorank.permissions.PermissionsHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Staartvin & DeathStampler (see replaceGroup())
 *         <p>
 *         VaultPermissionsHandler tackles all work that has to be done with
 *         Vault. (Most of the permissions plugins are supported with Vault)
 */
public class VaultPermissionsHandler implements PermissionsHandler {

    private final Autorank plugin;

    public VaultPermissionsHandler(final Autorank plugin) {
        if (!plugin.getDependencyManager().getDependency(AutorankDependency.VAULT).isAvailable()) {
            plugin.getLogger().severe(
                    "Autorank did not find Vault when it started its boot sequence. This could cause problems!");
        }

        this.plugin = plugin;
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
        LibraryHook hook = plugin.getDependencyManager().getLibraryHook(Library.VAULT);

        if (hook == null || !hook.isAvailable())
            return false;

        if (VaultHook.getPermissions() == null) {
            return false;
        }

        return VaultHook.getPermissions().playerAddGroup(world, player, group);
        // return permission.playerAddGroup(world, player.getName(), group);
    }

    @Override
    public boolean demotePlayer(final Player player, String world, final String groupFrom, final String groupTo) {

        LibraryHook hook = plugin.getDependencyManager().getLibraryHook(Library.VAULT);

        if (hook == null || !hook.isAvailable())
            return false;

        if (VaultHook.getPermissions() == null) {
            return false;
        }

        // Temporary fix for bPermissions
        if (world == null && VaultHook.getPermissions().getName().toLowerCase().contains("bpermissions")) {
            world = player.getWorld().getName();
        }

        // Let get the player groups before we change them.
        final Collection<String> groupsBeforeAdd = getPlayerGroups(player);

        // Output array for debug
        for (final String group : groupsBeforeAdd) {
            plugin.debugMessage("Group of " + player.getName() + " before removing: " + group);
        }

        Collection<String> groupsAfterAdd = null;

        final boolean worked1 = removeGroup(player, world, groupFrom);

        boolean worked2 = false;

        if (worked1) {
            // There should be a difference between the two.
            groupsAfterAdd = getPlayerGroups(player);

            // Output array for debug
            for (final String group : groupsAfterAdd) {
                plugin.debugMessage("Group of " + player.getName() + " after removing: " + group);
            }

            worked2 = addGroup(player, world, groupTo);
        }

        return worked1 && worked2;
    }

    /**
     * Get all known groups
     *
     * @return an array of strings containing all setup groups of the
     * permissions plugin.
     */
    @Override
    public Collection<String> getGroups() {
        List<String> groups = new ArrayList<>();

        LibraryHook hook = plugin.getDependencyManager().getLibraryHook(Library.VAULT);

        if (hook == null || !hook.isAvailable())
            return Collections.unmodifiableCollection(groups);

        if (VaultHook.getPermissions() == null) {
            return Collections.unmodifiableCollection(groups);
        }

        if (VaultHook.getPermissions() == null) {
            return Collections.unmodifiableCollection(groups);
        }

        for (String groupName : VaultHook.getPermissions().getGroups()) {
            groups.add(groupName);
        }

        return Collections.unmodifiableCollection(groups);
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.armar.plugins.autorank.permissions.PermissionsHandler#getName()
     */
    @Override
    public String getName() {
        return VaultHook.getPermissions().getName();
    }

    @Override
    public Collection<String> getPlayerGroups(final Player player) {
        List<String> groups = new ArrayList<>();

        LibraryHook hook = plugin.getDependencyManager().getLibraryHook(Library.VAULT);

        if (hook == null || !hook.isAvailable())
            return Collections.unmodifiableCollection(groups);

        if (VaultHook.getPermissions() == null) {
            return Collections.unmodifiableCollection(groups);
        }

        if (VaultHook.getPermissions() == null) {
            return Collections.unmodifiableCollection(groups);
        }

        final Autorank plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");

        // Let admin choose.
        if (plugin.getConfigHandler().onlyUsePrimaryGroupVault()) {
            groups.add(VaultHook.getPermissions().getPrimaryGroup(player));
        } else {
            for (String groupName : VaultHook.getPermissions().getPlayerGroups(player)) {
                groups.add(groupName);
            }
        }

        return Collections.unmodifiableCollection(groups);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Collection<String> getWorldGroups(final Player player, final String world) {
        List<String> groups = new ArrayList<>();

        LibraryHook hook = plugin.getDependencyManager().getLibraryHook(Library.VAULT);

        if (hook == null || !hook.isAvailable())
            return Collections.unmodifiableCollection(groups);

        if (VaultHook.getPermissions() == null) {
            return Collections.unmodifiableCollection(groups);
        }

        if (VaultHook.getPermissions() == null) {
            return Collections.unmodifiableCollection(groups);
        }

        if (VaultHook.getPermissions() == null) {
            return Collections.unmodifiableCollection(groups);
        }

        for (String groupName : VaultHook.getPermissions().getPlayerGroups(world, player.getName())) {
            groups.add(groupName);
        }

        return Collections.unmodifiableCollection(groups);
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

        LibraryHook hook = plugin.getDependencyManager().getLibraryHook(Library.VAULT);

        if (hook == null || !hook.isAvailable())
            return false;

        if (VaultHook.getPermissions() == null) {
            return false;
        }

        if (VaultHook.getPermissions() == null) {
            return false;
        }

        return VaultHook.getPermissions().playerRemoveGroup(world, player, group);
        // return permission.playerRemoveGroup(world, player.getName(), group);
    }

    @Override
    public boolean replaceGroup(final Player player, String world, final String oldGroup, final String newGroup) {

        LibraryHook hook = plugin.getDependencyManager().getLibraryHook(Library.VAULT);

        if (hook == null || !hook.isAvailable())
            return false;

        if (VaultHook.getPermissions() == null) {
            return false;
        }

        if (VaultHook.getPermissions() == null) {
            return false;
        }

        // Temporary fix for bPermissions
        if (world == null && VaultHook.getPermissions().getName().toLowerCase().contains("bpermissions")) {
            world = player.getWorld().getName();
        }

        // Let get the player groups before we change them.
        final Collection<String> groupsBeforeAdd = getPlayerGroups(player);

        // Output array for debug
        for (final String group : groupsBeforeAdd) {
            plugin.debugMessage("Group of " + player.getName() + " before adding: " + group);
        }

        Collection<String> groupsAfterAdd = null;

        final boolean worked1 = addGroup(player, world, newGroup);

        boolean worked2 = false;

        if (worked1) {
            // There should be a difference between the two.
            groupsAfterAdd = getPlayerGroups(player);

            // Output array for debug
            for (final String group : groupsAfterAdd) {
                plugin.debugMessage("Group of " + player.getName() + " after adding: " + group);
            }

            // When using PEX, if a player is in a default group this is not
            // really listed as the player being in the group.
            // It's just used as an alias. When we would change the rank, the
            // player would lose all other default groups.
            // We check if the player is in a default group and then re-add the
            // other groups after we added the new group the player was ranked
            // up to.
            // Thanks to @DeathStampler for this code and info.
            if (VaultHook.getPermissions().getName().toLowerCase().contains("permissionsex")) {
                // Normally the player should have one more group at this point.
                if (groupsAfterAdd.size() >= (groupsBeforeAdd.size() + 1)) {
                    // We have one more groups than before. Great. Let's remove
                    // oldGroup.
                    worked2 = removeGroup(player, world, oldGroup);

                    // Otherwise, let's see if we have just one group. This is
                    // an indication that the
                    // PermissionsEX player had more than one default group set.
                    // Those are now gone
                    // and we are left with just the newGroup.
                } else if (groupsAfterAdd.size() == 1) {
                    // We have just one group. Let's add any that are missing.
                    for (final String group : groupsBeforeAdd) {
                        // Let's not re-add the oldGroup
                        if (!group.equalsIgnoreCase(oldGroup)) {
                            // Should we check it if succeeds?
                            addGroup(player, world, group);
                        }
                    }
                    worked2 = true;
                } else {
                    // Not sure what situation would lead us here, so we'll just
                    // assume everything is good.
                    worked2 = true;
                }
            } else {
                worked2 = removeGroup(player, world, oldGroup);
            }
        }

        return worked1 && worked2;
    }
}
