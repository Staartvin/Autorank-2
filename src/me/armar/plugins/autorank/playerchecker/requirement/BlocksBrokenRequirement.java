package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BlocksBrokenRequirement extends Requirement {

	private int blocksBroken = 0;
	private Autorank plugin;
	private boolean optional = false;
	List<Result> results = new ArrayList<Result>();

	public BlocksBrokenRequirement() {
		super();
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}
	
	@Override
	public boolean setOptions(String[] options, boolean optional, List<Result> results) {
		this.optional = optional;
		this.results = results;
		
		try {
			blocksBroken = Integer.parseInt(options[0]);
			return true;
		} catch (Exception e) {
			blocksBroken = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(Player player) {
		// TODO Auto-generated method stub
		boolean enabled = plugin.getStatsHandler().isEnabled();
		boolean blocksbroken = plugin.getStatsHandler().getTotalBlocksBroken(player.getName()) >= blocksBroken; 
		
		if (isCompleted(getReqID(this.getClass(), player), player.getName())) {
			return true;
		}
		return enabled && blocksbroken;
	}

	@Override
	public String getDescription() {
		return "Break at least " + blocksBroken + " blocks.";
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}
}
