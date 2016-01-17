package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

/**
 * This requirement checks for global playtime
 * Date created: 13:49:53
 * 15 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class GlobalTimeRequirement extends Requirement {

	private final List<Integer> times = new ArrayList<Integer>();

	@Override
	public String getDescription() {

		final List<String> sTimes = new ArrayList<String>();

		for (final int time : times) {
			sTimes.add(AutorankTools.timeToString(time, Time.MINUTES));
		}

		return Lang.GLOBAL_TIME_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(sTimes, "or"));
	}

	@Override
	public String getProgress(final Player player) {

		String progress = "";

		final int playtime = getAutorank().getPlaytimes().getGlobalTime(
				player.getUniqueId());

		progress = AutorankTools
				.makeProgressString(times, "min", playtime + "");
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final UUID uuid = player.getUniqueId();

		final double playtime = this.getAutorank().getPlaytimes()
				.getGlobalTime(uuid);

		for (final int time : times) {
			if (time > 0 && playtime >= time) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			if (options.length > 0) {
				times.add(AutorankTools.stringToTime(options[0], Time.MINUTES));
			}
		}
		return !times.isEmpty();
	}
}
