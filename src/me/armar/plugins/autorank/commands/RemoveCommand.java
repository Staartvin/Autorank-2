package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveCommand implements CommandExecutor {

	private Autorank plugin;
	
	public RemoveCommand(Autorank instance) {
		plugin = instance;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if (!plugin.getCommandsManager().hasPermission("autorank.remove", sender)) {
			return true;
		}

		int value = -1;
		if (args.length > 2)
			try {
				value = -AutorankTools.stringtoInt(args[2]);
				value += plugin.getLocalTime(args[1]);
			} catch (NumberFormatException e) {
			}

		if (value >= 0) {
			plugin.setLocalTime(args[1], value);
			AutorankTools.sendColoredMessage(
					sender,
					Lang.PLAYTIME_CHANGED.getConfigValue(new String[] {
							args[1], value + "" }));
		} else {
			AutorankTools
					.sendColoredMessage(
							sender,
							Lang.INVALID_FORMAT
									.getConfigValue(new String[] { "/ar remove [player] [value]" }));
		}

		return true;
	}

}
