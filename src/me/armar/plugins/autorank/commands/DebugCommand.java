package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DebugCommand implements CommandExecutor {

	private Autorank plugin;

	public DebugCommand(Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label,
			String[] args) {

		// This will create a 'debug.txt' file containing a lot of information about the plugin
		if (!plugin.getCommandsManager().hasPermission("autorank.debug", sender)) {
			return true;
		}

		plugin.getServer().getScheduler()
				.runTaskAsynchronously(plugin, new Runnable() {
					public void run() {
						String fileName = plugin.getDebugger()
								.createDebugFile();

						sender.sendMessage(ChatColor.GREEN + "Debug file '"
								+ fileName + "' created!");
					}
				});

		return true;
	}

}
