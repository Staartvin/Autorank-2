package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import net.milkbowl.vault.Vault;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

//import ru.tehkode.permissions.bukkit.PermissionsEx;

//import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class PermissionsHandler implements PermissionsPluginHandler {

    private PermissionsPluginHandler permissionPlugin;

    public PermissionsHandler(JavaPlugin plugin) {
/*	if(findPermissionsEX(plugin)){
	    Autorank.logMessage("Using PermissionsEx.");
	}else if(findPermissionsBukkit(plugin)){
	    Autorank.logMessage("Using PermissionsBukkit.");
	} */ if(findVault(plugin)){
	    Autorank.logMessage("Using Vault.");
	}else{
	    Autorank.logMessage("WARNING No permissions plugin was found !");
	}
    }

    private boolean findVault(JavaPlugin plugin) {
	Plugin x = plugin.getServer().getPluginManager().getPlugin("Vault");
	if (x != null & x instanceof Vault) {
	    return true;
	}
	return false;
    }
    
 /*   private boolean findPermissionsBukkit(JavaPlugin plugin) {
	Plugin x = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
	if (x != null & x instanceof PermissionsPlugin) {
	    permissionPlugin = new PermissionsBukkitHandler((PermissionsPlugin) x);
	    return true;
	}
	return false;
    }

    private boolean findPermissionsEX(JavaPlugin plugin) {
	Plugin x = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
	if (x != null & x instanceof PermissionsEx) {
	    permissionPlugin = new PermissionsEXHandler();
	    return true;
	}
	return false;
    } */

    @Override
    public String[] getPlayerGroups(Player player) {
	if (permissionPlugin != null) {
	    return permissionPlugin.getPlayerGroups(player);
	} else {
	    return null;
	}
    }

    @Override
    public boolean replaceGroup(Player player, String world, String oldGroup, String newGroup) {
	if (permissionPlugin != null) {
	    return permissionPlugin.replaceGroup(player, world, oldGroup, newGroup);
	} else {
	    return false;
	}
    }

    @Override
    public boolean removeGroup(Player player, String world, String group) {
	if (permissionPlugin != null) {
	    return permissionPlugin.removeGroup(player, world, group);
	} else {
	    return false;
	}
    }

    @Override
    public boolean addGroup(Player player, String world, String group) {
	if (permissionPlugin != null) {
	    return permissionPlugin.addGroup(player, world, group);
	} else {
	    return false;
	}
    }

}
