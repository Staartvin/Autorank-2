package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlocksPlacedRequirement extends Requirement {

	private int blockID = -1;
	private int blocksPlaced = 0;
	private int damageValue = -1;

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

		message = message.concat("blocks");
		return Lang.PLACED_BLOCKS_REQUIREMENT
				.getConfigValue(new String[] { message });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.BLOCKS_PLACED.toString(),
				player.getName(), null, blockID + "", damageValue + "")
				+ "/" + blocksPlaced);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final boolean enabled = getStatsPlugin().isEnabled();

		boolean sufficient = false;
		if (blockID > 0) {
			sufficient = getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.BLOCKS_PLACED.toString(),
					player.getName(), null, blockID + "", damageValue + "") >= blocksPlaced;
		} else {
			sufficient = getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.TOTAL_BLOCKS_PLACED.toString(),
					player.getName(), null) >= blocksPlaced;
		}

		return enabled && sufficient;
	}

	@Override
	public boolean setOptions(final String[] options) {
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
}
