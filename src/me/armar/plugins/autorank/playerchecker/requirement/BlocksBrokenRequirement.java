package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlocksBrokenRequirement extends Requirement {

	private int blocksBroken = 0;
	private int blockID = -1;
	private int damageValue = -1;
	
	@Override
	public boolean setOptions(final String[] options) {
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
		} catch (final Exception e) {
			blocksBroken = 0;
			return false;
		}

		return true;

	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final boolean enabled = getStatsPlugin().isEnabled();

		boolean sufficient = false;
		if (blockID > 0) {
			sufficient = getStatsPlugin().getNormalStat("blocks_broken",
					player.getName(), null, blockID + "", damageValue + "") >= blocksBroken;
		} else {
			sufficient = getStatsPlugin().getNormalStat("total_blocks_broken",
					player.getName(), null) >= blocksBroken;
		}

		return enabled && sufficient;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getDescription() {
		String argument = blocksBroken + " ";

		if (blockID > 0 && damageValue >= 0) {
			final ItemStack item = new ItemStack(blockID, 1,
					(short) damageValue);

			argument = argument.concat(item.getType().name().replace("_", "")
					.toLowerCase()
					+ " ");
		} else if (blockID > 0) {
			final ItemStack item = new ItemStack(blockID, 1);

			argument = argument.concat(item.getType().name().replace("_", "")
					.toLowerCase()
					+ " ");
		}

		argument = argument.concat("blocks");
		return Lang.BROKEN_BLOCKS_REQUIREMENT
				.getConfigValue(new String[] { argument });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getStatsPlugin().getNormalStat(
				"blocks_broken", player.getName(), null, blockID + "",
				damageValue + "")
				+ "/" + blocksBroken);
		return progress;
	}
}
