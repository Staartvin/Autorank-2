package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

/**
 * This requirement checks for global playtime
 * Date created: 13:49:53
 * 15 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class GlobalTimeRequirement extends Requirement {

	int time = -1;

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0)
			this.time = AutorankTools.stringToMinutes(options[0]);
		return (time != -1);
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final double playtime = this.getAutorank().getPlaytimes()
				.getGlobalTime(player.getUniqueId());
		return time != -1 && time <= playtime;
	}

	@Override
	public String getDescription() {
		return Lang.GLOBAL_TIME_REQUIREMENT
				.getConfigValue(new String[] { AutorankTools
						.minutesToString(time) });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getAutorank().getPlaytimes().getGlobalTime(
				player.getUniqueId())
				+ " min" + "/" + time + " min");
		return progress;
	}
}
