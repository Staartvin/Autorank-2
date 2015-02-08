package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class DamageTakenRequirement extends Requirement {

	private List<Integer> damageTaken = new ArrayList<Integer>();

	@Override
	public String getDescription() {
		return Lang.DAMAGE_TAKEN_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(damageTaken, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		int damTaken = getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.DAMAGE_TAKEN.toString(),
				player.getUniqueId());

		progress = AutorankTools.makeProgressString(damageTaken, "", damTaken
				+ "");
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		int damTaken = getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.DAMAGE_TAKEN.toString(),
				player.getUniqueId());

		for (int damageTake : damageTaken) {
			if (damTaken >= damageTake)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(List<String[]> optionsList) {

		for (String[] options : optionsList) {
			damageTaken.add(Integer.parseInt(options[0]));
		}
		
		return !damageTaken.isEmpty();
	}
}
