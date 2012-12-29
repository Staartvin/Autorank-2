package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultPermissionsHandler implements PermissionsPluginHandler{
    
    private static Permission permission = null;
    
    public VaultPermissionsHandler(JavaPlugin plugin) {
	if(!setupPermissions(plugin)){
	    Autorank.logMessage("Vault not found, autorank will not work!");
	}
    }

    private Boolean setupPermissions(JavaPlugin plugin) {
	RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager()
		.getRegistration(net.milkbowl.vault.permission.Permission.class);
	if (permissionProvider != null) {
	    permission = permissionProvider.getProvider();
	}
	return (permission != null);
    }
    
    @Override
    public String[] getPlayerGroups(Player player) {
	return permission.getPlayerGroups(player);
    }

    @Override
    public boolean replaceGroup(Player player, String world, String oldGroup, String newGroup) {
	return (addGroup(player, world, newGroup) && removeGroup(player, world, oldGroup));
    }

    @Override
    public boolean removeGroup(Player player, String world, String group) {
	return permission.playerRemoveGroup(world, player.getName(), group);
    }

    @Override
    public boolean addGroup(Player player, String world, String group) {
	return permission.playerAddGroup(world, player.getName(), group);
    }

}
