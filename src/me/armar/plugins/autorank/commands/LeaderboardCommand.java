package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LeaderboardCommand extends AutorankCommand {

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

		// Whether to broadcast
		boolean broadcast = false;

		if (args.length > 1) {

			if (args[1].equalsIgnoreCase("force")) {
				// We should force to update the leaderboard first
				plugin.getLeaderboard().updateLeaderboard();
			} else if (args[1].equalsIgnoreCase("broadcast")) {
				// Broadcast the command across the server.
				broadcast = true;
			}
		}

		if (!broadcast) {
			plugin.getLeaderboard().sendLeaderboard(sender);	
		} else {
			plugin.getLeaderboard().broadcastLeaderboard();
		}
		
		return true;
	}

}
