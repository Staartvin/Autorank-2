package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.UUID;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

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
	public String getDescription() {
		return Lang.GLOBAL_TIME_REQUIREMENT
				.getConfigValue(new String[] { AutorankTools.timeToString(time,
						Time.MINUTES) });
	}

	@Override
	public String getProgress(final Player player) {
		
		UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		String progress = "";
		progress = progress.concat(getAutorank().getPlaytimes().getGlobalTime(
				uuid)
				+ " min" + "/" + time + " min");
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		
		final double playtime = this.getAutorank().getPlaytimes()
				.getGlobalTime(uuid);
		return time != -1 && time <= playtime;
	}

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0)
			this.time = AutorankTools.stringToTime(options[0], Time.MINUTES);
		return (time != -1);
	}
}
