package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ImportCommand implements CommandExecutor {

	private Autorank plugin;
	
	public ImportCommand(Autorank instance) {
		plugin = instance;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if (!plugin.getCommandsManager().hasPermission("autorank.import", sender)) {
			return true;
		}

		AutorankTools.sendColoredMessage(sender,
				Lang.DATA_IMPORTED.getConfigValue(null));
		plugin.getPlaytimes().importData();

		return true;
	}

}
