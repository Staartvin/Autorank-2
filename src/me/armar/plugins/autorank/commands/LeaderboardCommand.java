package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LeaderboardCommand implements CommandExecutor {

	private Autorank plugin;
	
	public LeaderboardCommand(Autorank instance) {
		plugin = instance;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if (!plugin.getCommandsManager().hasPermission("autorank.leaderboard", sender)) {
			return true;
		}
		
		plugin.getLeaderboard().sendLeaderboard(sender);
		return true;
	}

}
