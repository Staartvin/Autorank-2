package me.armar.plugins.autorank.permissions;

import org.bukkit.plugin.Plugin;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.handlers.GroupManagerHandler;
import me.armar.plugins.autorank.permissions.handlers.PermissionsBukkitHandler;
import me.armar.plugins.autorank.permissions.handlers.PowerfulPermsHandler;
import me.armar.plugins.autorank.permissions.handlers.VaultPermissionsHandler;

/*
 * PermissionsPluginManager sort the tasks of removing/adding a player to a group depending
 * on the permissions plugin.
 * For now it supports Vault and explicit GroupManager.
 * 
 */

/**
 * PermissionsPluginManager manages what permission handler should be given. It
 * just does basic checks of availability and calculates what permissions plugin
 * suits best.
 * 
 * It can choose from GroupManager, PermissionsBukkit, PowerfulPerms and Vault.
 * 
 * @author Staartvin
 * 
 */
public class PermissionsPluginManager {

    private PermissionsHandler permissionPlugin;
    private final Autorank plugin;

    public PermissionsPluginManager(final Autorank plugin) {
        this.plugin = plugin;
    }

    protected boolean findGroupManager(final Autorank plugin) {
        final Plugin x = plugin.getServer().getPluginManager().getPlugin("GroupManager");
        if (x != null) {
            return true;
        }
        return false;
    }

    protected boolean findPermissionsBukkit(final Autorank plugin) {
        final Plugin x = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
        if (x != null) {
            return true;
        }
        return false;
    }

    protected boolean findPowerfulPerms(final Autorank plugin) {
        final Plugin x = plugin.getServer().getPluginManager().getPlugin("PowerfulPerms");
        if (x != null) {
            return true;
        }
        return false;
    }

    public PermissionsHandler getPermissionPlugin() {
        return permissionPlugin;
    }

    public void searchPermPlugin() {
        if (findGroupManager(plugin)) {
            // use Groupmanager
            permissionPlugin = new GroupManagerHandler(plugin);
        } else if (findPermissionsBukkit(plugin)) {
            // Use PermissionsBukkit
            permissionPlugin = new PermissionsBukkitHandler(plugin);
        } else if (findPowerfulPerms(plugin)) {
            // Use PermissionsBukkit
            permissionPlugin = new PowerfulPermsHandler(plugin);
        } else {
            // use Vault
            permissionPlugin = new VaultPermissionsHandler(plugin);
        }
    }
}
