package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class FishCaughtRequirement extends Requirement {

	private List<Integer> fishCaught = new ArrayList<Integer>();

	@Override
	public String getDescription() {
		return Lang.FISH_CAUGHT_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(fishCaught, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		final int progressBar = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.FISH_CAUGHT.toString(),
				player.getUniqueId());

		progress = AutorankTools
				.makeProgressString(fishCaught, "", progressBar);

		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		int fish = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.FISH_CAUGHT.toString(),
				player.getUniqueId());

		for (int fishC : fishCaught) {
			if (fish >= fishC)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(List<String[]> optionsList) {

		for (String[] options: optionsList) {
			fishCaught.add(Integer.parseInt(options[0]));	
		}
		
		return !fishCaught.isEmpty();
	}
}
