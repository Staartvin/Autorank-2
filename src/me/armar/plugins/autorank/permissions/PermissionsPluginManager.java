package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.handlers.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * PermissionsPluginManager manages what permission handler should be given. It
 * just does basic checks of availability and calculates what permissions plugin
 * suits best.
 * <p>
 * It can choose from GroupManager, PermissionsBukkit, PowerfulPerms, LuckPerms and Vault.
 *
 * @author Staartvin
 */
public class PermissionsPluginManager {

    private PermissionsHandler permissionPlugin;
    private final Autorank plugin;

    public PermissionsPluginManager(final Autorank plugin) {
        this.plugin = plugin;
    }

    private boolean isPluginAvailable(String pluginName) {
        final Plugin x = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        return x != null;
    }

    public PermissionsHandler getPermissionPlugin() {
        return permissionPlugin;
    }

    public void searchPermPlugin() {
        if (isPluginAvailable("GroupManager")) {
            // use Groupmanager
            permissionPlugin = new GroupManagerHandler(plugin);
            plugin.debugMessage("Using GroupManager as permissions plugin");
        } else if (isPluginAvailable("PermissionsBukkit")) {
            // Use PermissionsBukkit
            permissionPlugin = new PermissionsBukkitHandler(plugin);
            plugin.debugMessage("Using PermissionsBukkit as permissions plugin");
        } else if (isPluginAvailable("PowerfulPerms")) {
            // Use PermissionsBukkit
            permissionPlugin = new PowerfulPermsHandler(plugin);
            plugin.debugMessage("Using PowerfulPerms as permissions plugin");
        } else if (isPluginAvailable("LuckPerms")) {
            // Use LuckPerms
            permissionPlugin = new LuckPermsHandler(plugin);
            plugin.debugMessage("Using LuckPerms as permissions plugin");
        } else {
            // use Vault
            permissionPlugin = new VaultPermissionsHandler(plugin);
            plugin.debugMessage("Using Vault as permissions plugin");
        }
    }
}
