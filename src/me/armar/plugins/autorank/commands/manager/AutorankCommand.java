package me.armar.plugins.autorank.commands.manager;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

/**
 * This class represents an Autorank command, such as /ar check or /ar times.
 * 
 * @author Staartvin
 * 
 */
public abstract class AutorankCommand implements TabExecutor {

	private String desc = "", usage = "", permission = "";

	/**
	 * Get the description that is used for this command, can be null or empty.
	 */
	public String getDescription() {
		return desc;
	}

	/**
	 * Get the permission that is used to check if a player has permission
	 * to perform this command. Note that a command does not have a permission
	 * that necessarily fits the command. Sometimes, multiple permissions are
	 * needed to check
	 * if a player has access to this command.
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * Get the way this command is supposed to be used.
	 * For example, /ar times &lt;player&gt; &lt;type&gt;
	 */
	public String getUsage() {
		return usage;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public abstract boolean onCommand(final CommandSender sender, final Command cmd, final String label,
			final String[] args);

	/* (non-Javadoc)
	 * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public abstract List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args);

	/**
	 * Set the description of this command.
	 * @param desc Description of command
	 */
	public void setDesc(final String desc) {
		this.desc = desc;
	}

	/**
	 * Set the permission of this command.
	 * @param perm Permission of command
	 */
	public void setPermission(final String perm) {
		this.permission = perm;
	}

	/**
	 * Set the usage of this command.
	 * @param usage Usage of command
	 */
	public void setUsage(final String usage) {
		this.usage = usage;
	}
}
