package me.armar.plugins.autorank.commands.manager;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

/**
 * This class represents an Autorank command
 * <p>
 * Date created: 17:29:59 13 jul. 2014
 * 
 * @author Staartvin
 * 
 */
public abstract class AutorankCommand implements TabExecutor {

	private String desc = "", usage = "", permission = "";

	public String getDescription() {
		return desc;
	}

	public String getPermission() {
		return permission;
	}

	public String getUsage() {
		return usage;
	}

	@Override
	public abstract boolean onCommand(final CommandSender sender,
			final Command cmd, final String label, final String[] args);

	public void setDesc(final String desc) {
		this.desc = desc;
	}

	public void setPermission(final String perm) {
		this.permission = perm;
	}

	public void setUsage(final String usage) {
		this.usage = usage;
	}

	@Override
	public abstract List<String> onTabComplete(CommandSender sender,
			Command cmd, String commandLabel, String[] args);
}
