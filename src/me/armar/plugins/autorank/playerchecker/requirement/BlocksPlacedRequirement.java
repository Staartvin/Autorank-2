package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BlocksPlacedRequirement extends Requirement {

	private int blocksPlaced = 0;
	private Autorank plugin;
	private boolean optional = false;

	public BlocksPlacedRequirement() {
		super();
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}
	
	@Override
	public boolean setOptions(String[] options, boolean optional) {
		this.optional = optional;
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

}
