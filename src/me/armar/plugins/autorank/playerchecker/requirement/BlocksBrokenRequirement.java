package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlocksBrokenRequirement extends Requirement {

	private int blocksBroken = 0;
	private int blockID = -1;
	private int damageValue = -1;
	
	private int reqId;

	private Autorank plugin;
	private boolean optional = false;
	private boolean autoComplete = false;
	List<Result> results = new ArrayList<Result>();

	public BlocksBrokenRequirement() {
		super();
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}

	@Override
	public boolean setOptions(String[] options, boolean optional,
			List<Result> results, boolean autoComplete, int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;

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

		boolean enabled = plugin.getStatsHandler().isEnabled();

		boolean sufficient = false;
		if (blockID > 0) {
			sufficient = plugin.getStatsHandler().getBlocksBroken(
					player.getName(), blockID, damageValue) >= blocksBroken;
		} else {
			sufficient = plugin.getStatsHandler().getTotalBlocksBroken(
					player.getName()) >= blocksBroken;
		}

		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}
		return enabled && sufficient;
	}

	@Override
	public String getDescription() {
		String argument = blocksBroken + " ";

		if (blockID > 0 && damageValue >= 0) {
			ItemStack item = new ItemStack(blockID, 1, (short) damageValue);

			argument = argument.concat(item.getType().name().replace("_", "")
					.toLowerCase() + " ");
		} else if (blockID > 0) {
			ItemStack item = new ItemStack(blockID, 1);

			argument = argument.concat(item.getType().name().replace("_", "")
					.toLowerCase() + " ");
		}

		argument = argument.concat("blocks.");
		return Lang.BROKEN_BLOCKS_REQUIREMENT.getConfigValue(new String[] {argument});
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
	
	@Override
	public boolean useAutoCompletion() {
		return autoComplete;
	}

	@Override
	public int getReqId() {
		return reqId;
	}
}
