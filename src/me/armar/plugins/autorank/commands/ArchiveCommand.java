package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ArchiveCommand implements CommandExecutor {

	private final Autorank plugin;

	public ArchiveCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.archive",
				sender)) {
			return true;
		}

		int rate = -1;

		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED + "You need to specify a time!");
			return true;
		}

		rate = AutorankTools.stringToMinutes(args[1]);

		if (rate <= 0) {
			sender.sendMessage(ChatColor.RED
					+ Lang.INVALID_FORMAT
							.getConfigValue(new String[] { "/ar archive 10d/10h/10m" }));
			return true;
		}

		sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.YELLOW
				+ plugin.getPlaytimes().archive(rate) + "" + ChatColor.GREEN
				+ " records below " + ChatColor.YELLOW
				+ AutorankTools.minutesToString(rate) + ChatColor.GREEN + ".");
		return true;
	}

}
