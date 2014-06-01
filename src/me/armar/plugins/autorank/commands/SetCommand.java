package me.armar.plugins.autorank.commands;

import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetCommand implements CommandExecutor {

	private final Autorank plugin;

	public SetCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		int value = -1;
		
		if (args.length < 3) {
			sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar set <player> <value>"));
			return true;
		}
		
		if (args.length > 2) {
			
			StringBuilder builder = new StringBuilder();

			for (int i = 2; i < args.length; i++) {
				builder.append(args[i]);
			}
			
			if (!builder.toString().contains("m") && !builder.toString().contains("h") && !builder.toString().contains("d")) {
				value = AutorankTools.stringtoInt(builder.toString().trim());
			} else {
				value = AutorankTools.stringToMinutes(builder.toString());	
			}
		}

		if (value >= 0) {

			if (args[1].equalsIgnoreCase(sender.getName())) {
				if (!plugin.getCommandsManager().hasPermission(
						"autorank.set.self", sender)) {
					return true;
				}
			} else {
				if (!plugin.getCommandsManager().hasPermission(
						"autorank.set.other", sender)) {
					return true;
				}
			}

			UUID uuid = UUIDManager.getUUIDFromPlayer(args[1]);
			
			if (uuid == null) {
				sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
				return true;
			}
			
			plugin.getPlaytimes().setLocalTime(uuid, value);
			AutorankTools.sendColoredMessage(
					sender,
					Lang.PLAYTIME_CHANGED.getConfigValue(
							args[1], value + " minutes"));
		} else {
			AutorankTools
					.sendColoredMessage(
							sender,
							Lang.INVALID_FORMAT
									.getConfigValue("/ar set <player> <value>" ));
		}

		return true;
	}

}
