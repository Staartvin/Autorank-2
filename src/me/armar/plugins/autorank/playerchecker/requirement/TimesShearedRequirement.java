package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

import org.bukkit.entity.Player;

public class TimesShearedRequirement extends Requirement {

	private int timesSheared = 0;

	@Override
	public String getDescription() {
		return Lang.TIMES_SHEARED_REQUIREMENT.getConfigValue(timesSheared + "");
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		final int progressBar = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.TIMES_SHEARED.toString(),
				player.getName(), null);

		progress = progress.concat(progressBar + "/" + timesSheared);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		return this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.TIMES_SHEARED.toString(),
				player.getName(), null) > timesSheared;
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			timesSheared = Integer.parseInt(options[0]);
			return true;
		} catch (final Exception e) {
			timesSheared = 999999999;
			return false;
		}
	}
}
