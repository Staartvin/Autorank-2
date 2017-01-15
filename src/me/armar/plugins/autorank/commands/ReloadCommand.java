package me.armar.plugins.autorank.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * The command delegator for the '/ar reload' command.
 */
public class ReloadCommand extends AutorankCommand {

	private final Autorank plugin;

	public ReloadCommand(final Autorank instance) {
		this.setUsage("/ar reload");
		this.setDesc("Reload Autorank.");
		this.setPermission("autorank.reload");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.reload", sender)) {
			return true;
		}
		
		// Reload files
		plugin.getPathsConfig().reloadConfig();
		plugin.getPlayerDataConfig().reloadConfig();
		plugin.getSettingsConfig().reloadConfig();
		
		// Rebuild paths
		plugin.getPathManager().initialiseFromConfigs();
		
		AutorankTools.sendColoredMessage(sender, Lang.AUTORANK_RELOADED.getConfigValue());

		return true;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
			final String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
