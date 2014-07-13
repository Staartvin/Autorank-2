package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ImportCommand extends AutorankCommand implements CommandExecutor {

	private final Autorank plugin;

	public ImportCommand(final Autorank instance) {
		this.setUsage("/ar import");
		this.setDesc("Import old data.");
		this.setPermission("autorank.import");
		
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.import",
				sender)) {
			return true;
		}

		AutorankTools.sendColoredMessage(sender,
				Lang.DATA_IMPORTED.getConfigValue());
		plugin.getPlaytimes().importData();

		return true;
	}

}
