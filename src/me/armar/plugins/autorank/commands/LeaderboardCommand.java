package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LeaderboardCommand extends AutorankCommand implements CommandExecutor {

	private final Autorank plugin;

	public LeaderboardCommand(final Autorank instance) {
		this.setUsage("/ar leaderboard");
		this.setDesc("Show the leaderboard.");
		this.setPermission("autorank.leaderboard");
		
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
