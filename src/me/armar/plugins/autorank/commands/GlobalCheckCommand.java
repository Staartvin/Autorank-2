package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalCheckCommand implements CommandExecutor {

	private final Autorank plugin;

	public GlobalCheckCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		// This is a global check. It will not show you the database numbers
		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			sender.sendMessage(ChatColor.RED
					+ Lang.MYSQL_IS_NOT_ENABLED.getConfigValue(null));
			return true;
		}

		if (args.length > 1) {

			if (!plugin.getCommandsManager().hasPermission(
					"autorank.checkothers", sender)) {
				return true;
			}

			final Player player = plugin.getServer().getPlayer(args[1]);
			if (player == null) {
				AutorankTools.sendColoredMessage(
						sender,
						args[1]
								+ Lang.HAS_PLAYED_FOR.getConfigValue(null)
								+ AutorankTools.minutesToString(plugin
										.getGlobalTime(args[1]))
								+ " across all servers.");
			} else {
				if (player.hasPermission("autorank.exclude")) {
					sender.sendMessage(ChatColor.RED
							+ Lang.PLAYER_IS_EXCLUDED
									.getConfigValue(new String[] { args[1] }));
					return true;
				}

				// Do no check. Players can't be checked on global times (at the moment)
				//check(sender, player);
			}
		} else if (sender instanceof Player) {
			if (!plugin.getCommandsManager().hasPermission("autorank.check",
					sender)) {
				return true;
			}

			if (sender.hasPermission("autorank.exclude")) {
				sender.sendMessage(ChatColor.RED
						+ Lang.PLAYER_IS_EXCLUDED
								.getConfigValue(new String[] { sender.getName() }));
				return true;
			}
			final Player player = (Player) sender;
			AutorankTools.sendColoredMessage(
					sender,
					"You have played for "
							+ AutorankTools.minutesToString(plugin
									.getGlobalTime(player.getName()))
							+ " across all servers.");

		} else {
			AutorankTools.sendColoredMessage(sender,
					Lang.CANNOT_CHECK_CONSOLE.getConfigValue(null));
		}
		return true;
	}

}
