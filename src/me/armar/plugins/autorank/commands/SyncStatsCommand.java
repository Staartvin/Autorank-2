package me.armar.plugins.autorank.commands;

import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SyncStatsCommand extends AutorankCommand {

	private final Autorank plugin;

	public SyncStatsCommand(final Autorank instance) {
		this.setUsage("/ar syncstats");
		this.setDesc("Sync Autorank's time to Stats' time.");
		this.setPermission("autorank.syncstats");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.syncstats",
				sender))
			return true;

		if (!plugin.getHookedStatsPlugin().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "Stats is not enabled!");
			return true;
		}

		int count = 0;

		// Sync playtime of every player
		for (final UUID uuid : plugin.getPlaytimes().getUUIDKeys()) {

			final OfflinePlayer p = plugin.getServer().getOfflinePlayer(uuid);

			// Time is stored in seconds
			final int statsPlayTime = plugin.getHookedStatsPlugin()
					.getNormalStat("time_played", p.getName(), null);

			if (statsPlayTime <= 0) {
				continue;
			}

			// Update time
			plugin.getPlaytimes().setLocalTime(uuid,
					Math.round(statsPlayTime / 60));

			// Increment count
			count++;
		}

		if (count == 0) {
			sender.sendMessage(ChatColor.GREEN
					+ "Could not sync stats. Run command again!");
		} else {
			sender.sendMessage(ChatColor.GREEN
					+ "Time has succesfully been updated for all entries.");
		}
		return true;
	}

}
