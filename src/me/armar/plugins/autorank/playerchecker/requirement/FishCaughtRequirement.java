package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

public class FishCaughtRequirement extends Requirement {

	int fishCaught = -1;

	@Override
	public String getDescription() {

		String lang = Lang.FISH_CAUGHT_REQUIREMENT.getConfigValue(fishCaught + "");

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		final int progressBar = this.getStatsPlugin().getNormalStat(StatsHandler.statTypes.FISH_CAUGHT.toString(),
				player.getUniqueId(), this.getWorld());

		return progressBar + "/" + fishCaught;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final int fish = this.getStatsPlugin().getNormalStat(StatsHandler.statTypes.FISH_CAUGHT.toString(),
				player.getUniqueId(), this.getWorld());

		return fish >= fishCaught;
	}

	@Override
	public boolean setOptions(final String[] options) {

		fishCaught = Integer.parseInt(options[0]);

		return fishCaught != -1;
	}
}
