package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends AutorankCommand implements CommandExecutor {

	private final Autorank plugin;

	public ReloadCommand(final Autorank instance) {
		this.setUsage("/ar reload");
		this.setDesc("Reload Autorank.");
		this.setPermission("autorank.reload");
		
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
				Lang.AUTORANK_RELOADED.getConfigValue());
		plugin.reload();

		return true;
	}

}
