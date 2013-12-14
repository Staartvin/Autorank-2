package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SyncStatsCommand implements CommandExecutor {

	private final Autorank plugin;

	public SyncStatsCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.syncstats",
				sender))
			return true;

		if (!plugin.getStatsHandler().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "Stats is not enabled!");
			return true;
		}

		int count = 0;

		// Sync playtime of every player
		for (final String entry : plugin.getPlaytimes().getKeys()) {

			final OfflinePlayer p = plugin.getServer().getOfflinePlayer(entry);

			// Time is stored in seconds
			final int statsPlayTime = plugin.getStatsHandler()
					.getTotalPlayTime(p.getName(), null);

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
