package me.armar.plugins.autorank.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class ImportCommand extends AutorankCommand {

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

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
