package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LeaderboardCommand implements CommandExecutor {

	private final Autorank plugin;

	public LeaderboardCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.leaderboard",
				sender)) {
			return true;
		}

		plugin.getLeaderboard().sendLeaderboard(sender);
		return true;
	}

}
