package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlocksBrokenRequirement extends Requirement {

	private int blocksBroken = 0;
	private int blockID = -1;
	private int damageValue = -1;

	private Autorank plugin;
	private boolean optional = false;
	List<Result> results = new ArrayList<Result>();

	public BlocksBrokenRequirement() {
		super();
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}

	@Override
	public boolean setOptions(String[] options, boolean optional,
			List<Result> results) {
		this.optional = optional;
		this.results = results;

		try {
			if (options.length > 0) {
				blocksBroken = Integer.parseInt(options[0].trim());
			}
			if (options.length > 1) {
				blockID = Integer.parseInt(options[0].trim());
				blocksBroken = Integer.parseInt(options[1].trim());
			}
			if (options.length > 2) {
				damageValue = Integer.parseInt(options[2].trim());
			}
		} catch (Exception e) {
			blocksBroken = 0;
			return false;
		}

		return true;

	}

	@Override
	public boolean meetsRequirement(Player player) {
		// TODO Auto-generated method stub
		//System.out.print("(BROKEN) Check for id: " + blockID + ", dv: " + damageValue
		//		+ ", value: " + blocksBroken);

		boolean enabled = plugin.getStatsHandler().isEnabled();

		boolean sufficient = false;
		if (blockID > 0) {
			sufficient = plugin.getStatsHandler().getBlocksBroken(
					player.getName(), blockID, damageValue) >= blocksBroken;
		} else {
			sufficient = plugin.getStatsHandler().getTotalBlocksBroken(
					player.getName()) >= blocksBroken;
		}

		if (isCompleted(getReqID(this.getClass(), player), player.getName())) {
			return true;
		}
		return enabled && sufficient;
	}

	@Override
	public String getDescription() {
		String message = "Break at least " + blocksBroken + " ";

		if (blockID > 0 && damageValue >= 0) {
			ItemStack item = new ItemStack(blockID, 1, (short) damageValue);

			message = message.concat(item.getType().name().replace("_", "")
					.toLowerCase() + " ");
		} else if (blockID > 0) {
			ItemStack item = new ItemStack(blockID, 1);

			message = message.concat(item.getType().name().replace("_", "")
					.toLowerCase() + " ");
		}

		message = message.concat("blocks.");
		return message;
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
		progress = progress.concat(getAutorank().getStatsHandler().getBlocksBroken(player.getName(), blockID, damageValue) + "/" + blocksBroken);
		return progress;
	}
}
