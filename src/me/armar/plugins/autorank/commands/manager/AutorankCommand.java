package me.armar.plugins.autorank.commands.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * This class represents an Autorank command
 * <p>
 * Date created:  17:29:59
 * 13 jul. 2014
 * @author Staartvin
 *
 */
public abstract class AutorankCommand {

	private String desc = "", usage = "", permission = "";
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public void setUsage(String usage) {
		this.usage = usage;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public String getUsage() {
		return usage;
	}
	
	public void setPermission(String perm) {
		this.permission = perm;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public abstract boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args);
}
