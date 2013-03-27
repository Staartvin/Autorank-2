package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.AutorankTools;

import org.bukkit.entity.Player;

public class TimeRequirement extends Requirement {

	int time = -1;

	@Override
	public boolean setOptions(String[] options) {
		if (options.length > 0)
			this.time = AutorankTools.stringToMinutes(options[0]);
		return (time != -1);
	}

	@Override
	public boolean meetsRequirement(Player player) {
		double playtime = this.getAutorank().getPlaytimes()
				.getTime(player.getName());
		return time != -1 && time <= playtime;
	}

	@Override
	public String getDescription() {
		return "Playtime needs to be " + AutorankTools.minutesToString(time)
				+ " or higher.";
	}

}
