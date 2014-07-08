package me.armar.plugins.autorank.playerchecker.requirement;

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

	int time = -1;

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0)
			this.time = AutorankTools.stringToTime(options[0], Time.MINUTES);

		return (time != -1);
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		// Use getTimeOf so that when switched to another time, it'll still work.
		// getTimeOfPlayer() is in seconds, so convert.
		final double playtime = this.getAutorank().getPlaytimes()
				.getTimeOfPlayer(player.getName()) / 60;
		return time != -1 && time <= playtime;
	}

	@Override
	public String getDescription() {
		return Lang.TIME_REQUIREMENT
				.getConfigValue(new String[] { AutorankTools.timeToString(time,
						Time.MINUTES) });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getAutorank().getPlaytimes()
				.getTimeOfPlayer(player.getName())
				+ " min"
				+ "/"
				+ time
				+ " min");
		return progress;
	}
}
