package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

public class TotalVotesRequirement extends Requirement {

	int totalVotes = -1;

	@Override
	public String getDescription() {

		String lang = Lang.VOTE_REQUIREMENT.getConfigValue(totalVotes + "");

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		final int votes = getStatsPlugin().getNormalStat(StatsHandler.statTypes.VOTES.toString(), player.getUniqueId(),
				this.getWorld());

		return votes + "/" + totalVotes;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		if (!this.getStatsPlugin().isEnabled()) {
			return false;
		}

		final int votes = getStatsPlugin().getNormalStat(StatsHandler.statTypes.VOTES.toString(), player.getUniqueId(),
				this.getWorld());

		return votes >= totalVotes;
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			totalVotes = Integer.parseInt(options[0]);
		} catch (final Exception e) {
			return false;
		}

		return totalVotes != -1;
	}
}
