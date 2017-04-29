package me.armar.plugins.autorank.permissions;

import org.bukkit.Bukkit;
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

    private boolean isGroupManagerAvailable() {
        final Plugin x = Bukkit.getServer().getPluginManager().getPlugin("GroupManager");
        if (x != null) {
            return true;
        }
        return false;
    }

    private boolean isPermissionsBukkitAvailable() {
        final Plugin x = Bukkit.getServer().getPluginManager().getPlugin("PermissionsBukkit");
        if (x != null) {
            return true;
        }
        return false;
    }

    private boolean isPowerfulPermsAvailable() {
        final Plugin x = Bukkit.getServer().getPluginManager().getPlugin("PowerfulPerms");
        if (x != null) {
            return true;
        }
        return false;
    }

    public PermissionsHandler getPermissionPlugin() {
        return permissionPlugin;
    }

    public void searchPermPlugin() {
        if (isGroupManagerAvailable()) {
            // use Groupmanager
            permissionPlugin = new GroupManagerHandler(plugin);
        } else if (isPermissionsBukkitAvailable()) {
            // Use PermissionsBukkit
            permissionPlugin = new PermissionsBukkitHandler(plugin);
        } else if (isPowerfulPermsAvailable()) {
            // Use PermissionsBukkit
            permissionPlugin = new PowerfulPermsHandler(plugin);
        } else {
            // use Vault
            permissionPlugin = new VaultPermissionsHandler(plugin);
        }
    }
}
