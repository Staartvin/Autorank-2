package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import net.milkbowl.vault.Vault;

import org.bukkit.plugin.Plugin;

//import ru.tehkode.permissions.bukkit.PermissionsEx;

//import com.platymuus.bukkit.permissions.PermissionsPlugin;

/*
 * PermissionsHandler takes care of the communications with the permissions 
 * plugin.
 * 
 */
public class PermissionsHandler {

	//private Autorank plugin;
	//private VaultPermissionsHandler vPermHandler;

	public PermissionsHandler(Autorank plugin) {
		if (findVault(plugin)) {
			Autorank.logMessage("Vault Hooked!");
		} else {
			Autorank.logMessage("WARNING Vault was not found!");
			//this.plugin = plugin;
		}
		//vPermHandler = new VaultPermissionsHandler(plugin);
	}

	protected boolean findVault(Autorank plugin) {
		Plugin x = plugin.getServer().getPluginManager().getPlugin("Vault");
		if (x != null & x instanceof Vault) {
			return true;
		}
		return false;
	}
	
	protected boolean findGroupManager(Autorank plugin) {
		Plugin x = plugin.getServer().getPluginManager().getPlugin("GroupManager");
		if (x != null) {
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

}
