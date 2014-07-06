package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

import org.bukkit.entity.Player;

/**
 * This requirement checks for the total time on the server
 * 
 * (i.e. the time now - the time he joined for the first time)
 * Date created: 13:49:33
 * 15 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class TotalTimeRequirement extends Requirement {

	int time = -1;

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0)
			this.time = AutorankTools.stringToTime(options[0], Time.MINUTES);
		
		return (time != -1);
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		// the time he first joined the server
		long joinTime = player.getFirstPlayed();

		long currentTime = System.currentTimeMillis();

		// Difference in minutes
		long difference = (currentTime - joinTime) / 60000;

		return time != -1 && difference >= time;
	}

	@Override
	public String getDescription() {
		return Lang.TOTAL_TIME_REQUIREMENT
				.getConfigValue(new String[] { AutorankTools
						.timeToString(time, Time.MINUTES) });
	}

	@Override
	public String getProgress(final Player player) {

		// the time he first joined the server
		long joinTime = player.getFirstPlayed();

		long currentTime = System.currentTimeMillis();

		// Difference in minutes
		long difference = (currentTime - joinTime) / 60000;

		String progress = "";
		progress = progress.concat(difference + " min" + "/" + time + " min");
		return progress;
	}
}
