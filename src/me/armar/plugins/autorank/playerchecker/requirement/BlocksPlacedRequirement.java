package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlocksPlacedRequirement extends Requirement {

	private int blocksPlaced = 0;
	private int blockID = -1;
	private int damageValue = -1;

	private int reqId;

	private final Autorank plugin;
	private boolean optional = false;
	private boolean autoComplete = false;
	List<Result> results = new ArrayList<Result>();

	public BlocksPlacedRequirement() {
		super();
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}

	@Override
	public boolean setOptions(final String[] options, final boolean optional,
			final List<Result> results, final boolean autoComplete,
			final int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;

		try {
			if (options.length > 0) {
				blocksPlaced = Integer.parseInt(options[0].trim());
			}
			if (options.length > 1) {
				blockID = Integer.parseInt(options[0].trim());
				blocksPlaced = Integer.parseInt(options[1].trim());
			}
			if (options.length > 2) {
				damageValue = Integer.parseInt(options[2].trim());
			}
		} catch (final Exception e) {
			blocksPlaced = 0;
			return false;
		}

		return true;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		// TODO Auto-generated method stub
		//System.out.print("(PLACED) Check for id: " + blockID + ", dv: " + damageValue
		//		+ ", value: " + blocksPlaced);

		final boolean enabled = plugin.getStatsHandler().isEnabled();

		boolean sufficient = false;
		if (blockID > 0) {
			sufficient = plugin.getStatsHandler()
					.getBlocksStat(player.getName(), blockID, damageValue,
							null, "Block place") >= blocksPlaced;
		} else {
			sufficient = plugin.getStatsHandler().getTotalBlocksPlaced(
					player.getName(), null) >= blocksPlaced;
		}

		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}

		return enabled && sufficient;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getDescription() {
		String message = blocksPlaced + " ";

		if (blockID > 0 && damageValue >= 0) {
			final ItemStack item = new ItemStack(blockID, 1,
					(short) damageValue);

			message = message.concat(item.getType().name().replace("_", "")
					.toLowerCase()
					+ " ");
		} else if (blockID > 0) {
			final ItemStack item = new ItemStack(blockID, 1);

			message = message.concat(item.getType().name().replace("_", "")
					.toLowerCase()
					+ " ");
		}

		message = message.concat("blocks.");
		return Lang.PLACED_BLOCKS_REQUIREMENT
				.getConfigValue(new String[] { message });
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
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getAutorank().getStatsHandler()
				.getBlocksStat(player.getName(), blockID, damageValue, null,
						"Block place")
				+ "/" + blocksPlaced);
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
