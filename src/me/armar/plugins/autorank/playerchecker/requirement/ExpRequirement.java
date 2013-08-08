package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

public class ExpRequirement extends Requirement {

	private int minExp = 999999999;
	private boolean optional = false;
	List<Result> results = new ArrayList<Result>();

	@Override
	public boolean setOptions(String[] options, boolean optional, List<Result> results) {
		this.optional = optional;
		this.results = results;
		
		try {
			minExp = AutorankTools.stringtoInt(options[0]);
		} catch (Exception e) {
		}

		return minExp == 999999999;
	}

	@Override
	public boolean meetsRequirement(Player player) {
		if (isCompleted(getReqID(this.getClass(), player), player.getName())) {
			return true;
		}
		
		return player.getLevel() >= minExp;
	}

	@Override
	public String getDescription() {
		return "Have at least level " + minExp + " in exp.";
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
		progress = progress.concat(player.getLevel() + "/" + minExp);
		return progress;
	}

}
