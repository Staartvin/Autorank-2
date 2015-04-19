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
		String lang = Lang.TIMES_SHEARED_REQUIREMENT
				.getConfigValue(AutorankTools.seperateList(timesShorn, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		final int progressBar = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.TIMES_SHEARED.toString(),
				player.getUniqueId(), this.getWorld());

		//progress = progress.concat(progressBar + "/" + timesSheared);
		progress = AutorankTools.makeProgressString(timesShorn, "", ""
				+ progressBar);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		for (final int times : timesShorn) {
			if (this.getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.TIMES_SHEARED.toString(),
					player.getUniqueId(), this.getWorld()) > times) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			try {
				timesShorn.add(Integer.parseInt(options[0]));
			} catch (final Exception e) {
				return false;
			}
		}

		return !timesShorn.isEmpty();
	}
}
