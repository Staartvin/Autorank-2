package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SyncStatsCommand implements CommandExecutor {

	private Autorank plugin;
	
	public SyncStatsCommand(Autorank instance) {
		plugin = instance;
	}
	
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if (!plugin.getCommandsManager().hasPermission("autorank.syncstats", sender))
			return true;

		if (!plugin.getStatsHandler().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "Stats is not enabled!");
			return true;
		}

		int count = 0;

		// Sync playtime of every player
		for (String entry : plugin.getPlaytimes().getKeys()) {

			OfflinePlayer p = plugin.getServer().getOfflinePlayer(entry);
			
			// Time is stored in seconds
			int statsPlayTime = (int) plugin.getStatsHandler().getTotalPlayTime(p.getName(), null);

			if (statsPlayTime <= 0) {
				continue;
			}

			// Check to see if the time actually changed.
			if ((statsPlayTime / 60) != plugin.getPlaytimes().getLocalTime(
					entry)) {

				// Update time
				plugin.getPlaytimes().setLocalTime(entry, (statsPlayTime / 60));

				// Increment count
				count++;
			}
		}

		sender.sendMessage(ChatColor.GREEN
				+ (count + " entries have been updated!"));
		return true;
	}

}
