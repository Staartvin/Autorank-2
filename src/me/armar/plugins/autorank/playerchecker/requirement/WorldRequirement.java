package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

public class WorldRequirement extends Requirement {

	String world = null;
	private boolean optional = false;
	List<Result> results = new ArrayList<Result>();

	@Override
	public boolean setOptions(String[] options, boolean optional, List<Result> results) {
		this.optional = optional;
		this.results = results;
		
		if (options.length > 0)
			this.world = options[0];
		return (world != null);
	}

	@Override
	public boolean meetsRequirement(Player player) {
		if (isCompleted(getReqID(this.getClass(), player), player.getName())) {
			return true;
		}
		
		return world != null && world.equals(player.getWorld().getName());
	}

	@Override
	public String getDescription() {
		return LanguageHandler.getLanguage().getWorldRequirement(world);
	}

	@Override
	public boolean isOptional() {
		// TODO Auto-generated method stub
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

}
