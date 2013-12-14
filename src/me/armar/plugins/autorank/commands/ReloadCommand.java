package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

	private final Autorank plugin;

	public ReloadCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.reload",
				sender)) {
			return true;
		}

		AutorankTools.sendColoredMessage(sender,
				Lang.AUTORANK_RELOADED.getConfigValue(null));
		plugin.reload();

		return true;
	}

}
