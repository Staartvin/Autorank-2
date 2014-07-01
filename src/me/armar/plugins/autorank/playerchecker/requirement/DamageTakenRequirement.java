package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

import org.bukkit.entity.Player;

public class DamageTakenRequirement extends Requirement {

	private int damageTaken = 0;

	@Override
	public boolean setOptions(final String[] options) {
		try {
			damageTaken = Integer.parseInt(options[0]);
			return true;
		} catch (final Exception e) {
			damageTaken = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		return getStatsPlugin().isEnabled()
				&& getStatsPlugin().getNormalStat(StatsHandler.statTypes.DAMAGE_TAKEN.toString(),
						player.getName(), null) >= damageTaken;
	}

	@Override
	public String getDescription() {
		return Lang.DAMAGE_TAKEN_REQUIREMENT
				.getConfigValue(new String[] { damageTaken + "" });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.DAMAGE_TAKEN.toString(), player.getName(), null)
				+ "/" + damageTaken);
		return progress;
	}
}
