package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BlocksPlacedRequirement extends Requirement {

	private int blocksPlaced = 0;
	private Autorank plugin;
	private boolean optional = false;
	List<Result> results = new ArrayList<Result>();

	public BlocksPlacedRequirement() {
		super();
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}
	
	@Override
	public boolean setOptions(String[] options, boolean optional, List<Result> results) {
		this.optional = optional;
		this.results = results;
		
		try {
			blocksPlaced = Integer.parseInt(options[0]);
			return true;
		} catch (Exception e) {
			blocksPlaced = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(Player player) {
		// TODO Auto-generated method stub
		if (isCompleted(getReqID(this.getClass(), player), player.getName())) {
			return true;
		}
		
		return plugin.getStatsHandler().isEnabled() && plugin.getStatsHandler().getTotalBlocksPlaced(player.getName()) >= blocksPlaced;
	}

	@Override
	public String getDescription() {
		return "Place at least " + blocksPlaced + " blocks.";
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
