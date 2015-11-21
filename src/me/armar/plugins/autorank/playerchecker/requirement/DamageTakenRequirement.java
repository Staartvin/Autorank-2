package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

public class DamageTakenRequirement extends Requirement {

	private final List<Integer> damageTaken = new ArrayList<Integer>();

	@Override
	public String getDescription() {

		String lang = Lang.DAMAGE_TAKEN_REQUIREMENT
				.getConfigValue(AutorankTools.seperateList(damageTaken, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		final int damTaken = getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.DAMAGE_TAKEN.toString(),
				player.getUniqueId(), this.getWorld());

		progress = AutorankTools.makeProgressString(damageTaken, "", damTaken
				+ "");
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final int damTaken = getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.DAMAGE_TAKEN.toString(),
				player.getUniqueId(), this.getWorld());

		for (final int damageTake : damageTaken) {
			if (damTaken >= damageTake)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			damageTaken.add(Integer.parseInt(options[0]));
		}

		return !damageTaken.isEmpty();
	}
}
