package me.armar.plugins.autorank.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;

public class DebugCommand extends AutorankCommand {

	private final Autorank plugin;

	public DebugCommand(final Autorank instance) {
		this.setUsage("/ar debug");
		this.setDesc("Shows debug information.");
		this.setPermission("autorank.debug");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		// This will create a 'debug.txt' file containing a lot of information about the plugin
		if (!plugin.getCommandsManager()
				.hasPermission("autorank.debug", sender)) {
			return true;
		}

		plugin.getServer().getScheduler()
				.runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						final String fileName = plugin.getDebugger()
								.createDebugFile();

						sender.sendMessage(ChatColor.GREEN + "Debug file '"
								+ fileName + "' created!");
					}
				});

		return true;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender,
			final Command cmd, final String commandLabel, final String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
