package me.armar.plugins.autorank.commands;

import java.sql.SQLException;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SyncCommand implements CommandExecutor {

	private Autorank plugin;
	
	public SyncCommand(Autorank instance) {
		plugin = instance;
	}
	
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if (!plugin.getCommandsManager().hasPermission("autorank.sync", sender))
			return true;

		if (!plugin.getConfigHandler().useMySQL()) {
			sender.sendMessage(ChatColor.RED + "MySQL is not being used!");
			return true;
		}

		sender.sendMessage(ChatColor.RED
				+ "You do not have to use this command regularly. Use this only one time per server.");

		// Do this async as we are accessing mysql database.
		plugin.getServer().getScheduler()
				.runTaskAsynchronously(plugin, new Runnable() {

					@Override
					public void run() {
						// Update all mysql records
						for (String player : plugin.getPlaytimes()
								.getKeys()) {
							if (plugin.getPlaytimes().getLocalTime(player) <= 0)
								continue;

							int localTime = plugin.getPlaytimes()
									.getLocalTime(player);
							int globalTime = plugin.getPlaytimes()
									.getGlobalTime(player);

							// Update record
							try {
								plugin.getPlaytimes().setGlobalTime(player,
										localTime + globalTime);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						sender.sendMessage(ChatColor.GREEN
								+ "Successfully updated MySQL records!");
					}
				});
		return true;
	}

}
