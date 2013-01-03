package me.armar.plugins.autorank.permissions;

import me.armar.plugins.autorank.Autorank;
import net.milkbowl.vault.Vault;

import org.bukkit.plugin.Plugin;

//import ru.tehkode.permissions.bukkit.PermissionsEx;

//import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class PermissionsHandler {

    @SuppressWarnings("unused")
	private Autorank plugin;
    private VaultPermissionsHandler vPermHandler;

    public PermissionsHandler(Autorank plugin) {
    	if(findVault(plugin)){
	    Autorank.logMessage("Vault Hooked!");
	}else{
	    Autorank.logMessage("WARNING No permissions plugin was found!");
	    this.plugin = plugin;
	}
    	setPermHandler(new VaultPermissionsHandler(plugin));
    }

    private boolean findVault(Autorank plugin) {
	Plugin x = plugin.getServer().getPluginManager().getPlugin("Vault");
	if (x != null & x instanceof Vault) {
	    return true;
	}
	return false;
    }

	public VaultPermissionsHandler getPermHandler() {
		return vPermHandler;
	}

	public void setPermHandler(VaultPermissionsHandler vPermHandler) {
		this.vPermHandler = vPermHandler;
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
