package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TotalVotesRequirement extends Requirement {

	private int totalVotes = 0;
	private Autorank plugin;

	public TotalVotesRequirement() {
		super();
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}
	
	@Override
	public boolean setOptions(String[] options) {
		try {
			totalVotes = Integer.parseInt(options[0]);
			return true;
		} catch (Exception e) {
			totalVotes = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(Player player) {
		// TODO Auto-generated method stub
		return plugin.getStatsHandler().isEnabled() && plugin.getStatsHandler().getTotalTimesVoted(player.getName()) >= totalVotes;
	}

	@Override
	public String getDescription() {
		return "Need a minimum of " + totalVotes + " votes.";
	}

}
