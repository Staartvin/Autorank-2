package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class TotalVotesRequirement extends Requirement {

	private int totalVotes = 0;
	
	@Override
	public boolean setOptions(final String[] options) {
		try {
			totalVotes = Integer.parseInt(options[0]);
			return true;
		} catch (final Exception e) {
			totalVotes = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		return getStatsPlugin().isEnabled()
				&& getStatsPlugin().getNormalStat("Votes", player.getName(),
						null) >= totalVotes;
	}

	@Override
	public String getDescription() {
		return Lang.VOTE_REQUIREMENT.getConfigValue(new String[] { totalVotes
				+ "" });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getStatsPlugin().getNormalStat("Votes",
				player.getName(), null)
				+ "/" + totalVotes);
		return progress;
	}
}
