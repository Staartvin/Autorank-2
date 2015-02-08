package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

import org.bukkit.entity.Player;

/**
 * This requirement checks for local play time
 * Date created: 13:49:33
 * 15 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class TimeRequirement extends Requirement {

	List<Integer> times = new ArrayList<Integer>();

	@Override
	public String getDescription() {
		List<String> sTimes = new ArrayList<String>();

		for (int time : times) {
			sTimes.add(AutorankTools.timeToString(time, Time.MINUTES));
		}

		return Lang.TIME_REQUIREMENT.getConfigValue(AutorankTools.seperateList(
				sTimes, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		int playtime = (getAutorank().getPlaytimes().getTimeOfPlayer(
				player.getName()) / 60);

		/*for (int i=0;i<times.size();i++) {
			int time = times.get(i);
			
			if (i==0) {
				progress += playtime + " min/" + time + " min";
			} else {
				progress += "or " + playtime + " min/" + time + " min";
			}
		}*/
		progress = AutorankTools
				.makeProgressString(times, "min", "" + playtime);

		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		// Use getTimeOf so that when switched to another time, it'll still work.
		// getTimeOfPlayer() is in seconds, so convert.
		final double playtime = this.getAutorank().getPlaytimes()
				.getTimeOfPlayer(player.getName()) / 60;

		for (int time : times) {
			if (time != -1 && time <= playtime) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean setOptions(List<String[]> optionsList) {

		for (String[] options : optionsList) {
			if (options.length > 0) {
				times.add(AutorankTools.stringToTime(options[0], Time.MINUTES));
			} else {
				return false;
			}
		}

		return !times.isEmpty();
	}
}
