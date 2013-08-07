package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.LanguageHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TotalVotesRequirement extends Requirement {

	private int totalVotes = 0;
	private Autorank plugin;
	private boolean optional = false;

	public TotalVotesRequirement() {
		super();
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}
	
	@Override
	public boolean setOptions(String[] options, boolean optional) {
		this.optional = optional;
		
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
		return LanguageHandler.getLanguage().getVoteRequirement(totalVotes);
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

}
