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

    private final Autorank plugin;
    private PermissionsHandler permissionPlugin;

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

    public boolean searchPermPlugin() {
        boolean loadedPermPlugin = false;

        if (isPluginAvailable("GroupManager")) {
            // use Groupmanager
            try {
                permissionPlugin = new GroupManagerHandler(plugin);
                plugin.debugMessage("Using GroupManager as permissions plugin");
                loadedPermPlugin = true;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else if (isPluginAvailable("PermissionsBukkit")) {
            // Use PermissionsBukkit
            try {
                permissionPlugin = new PermissionsBukkitHandler(plugin);
                plugin.debugMessage("Using PermissionsBukkit as permissions plugin");
                loadedPermPlugin = true;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else if (isPluginAvailable("LuckPerms")) {
            // Use LuckPerms
            try {
                permissionPlugin = new LuckPermsHandler(plugin);
                plugin.debugMessage("Using LuckPerms as permissions plugin");
                loadedPermPlugin = true;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            // use Vault
            try {
                permissionPlugin = new VaultPermissionsHandler(plugin);
                plugin.debugMessage("Using Vault as permissions plugin");
                loadedPermPlugin = true;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (!loadedPermPlugin) {
            // We could not load a permissions plugin so we provide fallback support to a dummy handler.
            permissionPlugin = new DummyPermissionsHandler(plugin);
            plugin.debugMessage("Using DummyPermissions handler.");

            // Let admins know something is up.
            plugin.getLogger().severe("Could not find a permissions handler. Are you sure you have a compatible " +
                    "permissions plugin installed?");
        }

        return loadedPermPlugin;
    }
}
