package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

public class TimeRequirement extends Requirement {

	int time = -1;
	private boolean optional = false;
	List<Result> results = new ArrayList<Result>();

	@Override
	public boolean setOptions(String[] options, boolean optional, List<Result> results) {
		this.optional = optional;
		this.results = results;
		
		if (options.length > 0)
			this.time = AutorankTools.stringToMinutes(options[0]);
		return (time != -1);
	}

	@Override
	public boolean meetsRequirement(Player player) {
		if (isCompleted(getReqID(this.getClass(), player), player.getName())) {
			return true;
		}
		
		double playtime = this.getAutorank().getPlaytimes()
				.getLocalTime(player.getName());
		return time != -1 && time <= playtime;
	}

	@Override
	public String getDescription() {
		return LanguageHandler.getLanguage().getTimeRequirement(AutorankTools.minutesToString(time));
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

	@Override
	public String getProgress(Player player) {
		String progress = "";
		progress = progress.concat(getAutorank().getPlaytimes().getLocalTime(player.getName()) + "/" + time);
		return progress;
	}

}
