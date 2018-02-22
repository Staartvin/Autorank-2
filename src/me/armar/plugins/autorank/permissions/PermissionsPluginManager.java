package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.handlers.GroupManagerHandler;
import me.armar.plugins.autorank.permissions.handlers.PermissionsBukkitHandler;
import me.armar.plugins.autorank.permissions.handlers.PowerfulPermsHandler;
import me.armar.plugins.autorank.permissions.handlers.VaultPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * PermissionsPluginManager manages what permission handler should be given. It
 * just does basic checks of availability and calculates what permissions plugin
 * suits best.
 * <p>
 * It can choose from GroupManager, PermissionsBukkit, PowerfulPerms and Vault.
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
        } else if (isPluginAvailable("PermissionsBukkit")) {
            // Use PermissionsBukkit
            permissionPlugin = new PermissionsBukkitHandler(plugin);
        } else if (isPluginAvailable("PowerfulPerms")) {
            // Use PermissionsBukkit
            permissionPlugin = new PowerfulPermsHandler(plugin);
        } else {
            // use Vault
            permissionPlugin = new VaultPermissionsHandler(plugin);
        }
    }
}
