package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class TimesShearedRequirement extends Requirement {

	List<Integer> timesShorn = new ArrayList<Integer>();

	@Override
	public String getDescription() {
		return Lang.TIMES_SHEARED_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(timesShorn, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		final int progressBar = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.TIMES_SHEARED.toString(),
				player.getUniqueId());

		//progress = progress.concat(progressBar + "/" + timesSheared);
		progress = AutorankTools.makeProgressString(timesShorn, "", "" + progressBar);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		for (int times : timesShorn) {
			if (this.getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.TIMES_SHEARED.toString(),
					player.getUniqueId()) > times) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean setOptions(List<String[]> optionsList) {

		for (String[] options : optionsList) {
			try {
				timesShorn.add(Integer.parseInt(options[0]));
			} catch (final Exception e) {
				return false;
			}
		}
		
		return !timesShorn.isEmpty();
	}
}
