package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

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
					+ Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
			return true;
		}

		if (args.length > 1) {

			if (!plugin.getCommandsManager().hasPermission(
					"autorank.checkothers", sender)) {
				return true;
			}

			@SuppressWarnings("deprecation")
			final Player player = plugin.getServer().getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(Lang.PLAYER_NOT_ONLINE
						.getConfigValue(new String[] { args[1] }));
				return true;
			} else {
				if (player.hasPermission("autorank.exclude")) {
					sender.sendMessage(ChatColor.RED
							+ Lang.PLAYER_IS_EXCLUDED
									.getConfigValue(new String[] { args[1] }));
					return true;
				}

				int minutes = plugin.getPlaytimes().getGlobalTime(
						player.getUniqueId());

				if (minutes < 0) {
					sender.sendMessage(Lang.PLAYER_IS_INVALID
							.getConfigValue(new String[] { args[1] }));
					return true;
				}

				AutorankTools.sendColoredMessage(
						sender,
						args[1] + " has played for "
								+ AutorankTools.minutesToString(minutes)
								+ " across all servers.");
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
									.getPlaytimes().getGlobalTime(
											player.getUniqueId()))
							+ " across all servers.");

		} else {
			AutorankTools.sendColoredMessage(sender,
					Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
		}
		return true;
	}

}
