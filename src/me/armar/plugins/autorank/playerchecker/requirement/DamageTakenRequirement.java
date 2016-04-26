package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

public class DamageTakenRequirement extends Requirement {

	int damageTaken = -1;

	@Override
	public String getDescription() {

		String lang = Lang.DAMAGE_TAKEN_REQUIREMENT.getConfigValue(damageTaken + "");

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		final int damTaken = getStatsPlugin().getNormalStat(StatsHandler.statTypes.DAMAGE_TAKEN.toString(),
				player.getUniqueId(), this.getWorld());

		return damTaken + "/" + damageTaken;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final int damTaken = getStatsPlugin().getNormalStat(StatsHandler.statTypes.DAMAGE_TAKEN.toString(),
				player.getUniqueId(), this.getWorld());

		return damTaken >= damageTaken;
	}

	@Override
	public boolean setOptions(final String[] options) {

		damageTaken = Integer.parseInt(options[0]);

		return damageTaken != -1;
	}
}
