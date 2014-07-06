package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

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
			this.time = AutorankTools.stringToMinutes(options[0]);
		
		return (time != -1);
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		// Use getTimeOf so that when switched to another time, it'll still work.
		final double playtime = this.getAutorank().getPlaytimes()
				.getTimeOfPlayer(player.getName());
		return time != -1 && time <= playtime;
	}

	@Override
	public String getDescription() {
		return Lang.TIME_REQUIREMENT
				.getConfigValue(new String[] { AutorankTools
						.minutesToString(time) });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getAutorank().getPlaytimes().getTimeOfPlayer(
				player.getName())
				+ " min" + "/" + time + " min");
		return progress;
	}
}
