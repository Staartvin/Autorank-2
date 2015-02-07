package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

import org.bukkit.entity.Player;

public class FishCaughtRequirement extends Requirement {

	private int fishCaught = 0;

	@Override
	public String getDescription() {
		return Lang.FISH_CAUGHT_REQUIREMENT.getConfigValue(fishCaught + "");
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		final int progressBar = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.FISH_CAUGHT.toString(),
				player.getUniqueId());

		progress = progress.concat(progressBar + "/" + fishCaught);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		return this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.FISH_CAUGHT.toString(),
				player.getUniqueId()) > fishCaught;
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			fishCaught = Integer.parseInt(options[0]);
			return true;
		} catch (final Exception e) {
			fishCaught = 999999999;
			return false;
		}
	}
}
