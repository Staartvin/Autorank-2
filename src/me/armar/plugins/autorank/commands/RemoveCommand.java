package me.armar.plugins.autorank.commands;

import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveCommand implements CommandExecutor {

	private final Autorank plugin;

	public RemoveCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.remove",
				sender)) {
			return true;
		}

		UUID uuid = UUIDManager.getUUIDFromPlayer(args[1]);
		
		int value = -1;
		
		if (args.length > 2) {

			StringBuilder builder = new StringBuilder();

			for (int i = 2; i < args.length; i++) {
				builder.append(args[i]);
			}

			if (!builder.toString().contains("m")
					&& !builder.toString().contains("h")
					&& !builder.toString().contains("d")) {
				value = -AutorankTools.stringtoInt(builder.toString().trim());
				value += plugin.getPlaytimes().getLocalTime(uuid);
			} else {
				value = -AutorankTools.stringToMinutes(builder.toString());
				value += plugin.getPlaytimes().getLocalTime(uuid);
			}
		}

		if (value >= 0) {
			plugin.getPlaytimes().setLocalTime(uuid, value);
			AutorankTools.sendColoredMessage(
					sender,
					Lang.PLAYTIME_CHANGED.getConfigValue(
							args[1], value + ""));
		} else {
			AutorankTools
					.sendColoredMessage(
							sender,
							Lang.INVALID_FORMAT
									.getConfigValue("/ar remove [player] [value]"));
		}

		return true;
	}

}
