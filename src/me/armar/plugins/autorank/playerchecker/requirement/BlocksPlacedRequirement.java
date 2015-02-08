package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlocksPlacedRequirement extends Requirement {

	private final List<BlocksPlacedWrapper> wrappers = new ArrayList<BlocksPlacedWrapper>();

	//private int blockID = -1;
	//private int blocksPlaced = 0;
	//private int damageValue = -1;

	@SuppressWarnings("deprecation")
	@Override
	public String getDescription() {
		final List<String> names = new ArrayList<String>();

		for (int i = 0; i < wrappers.size(); i++) {
			final BlocksPlacedWrapper wrapper = wrappers.get(i);

			final int blockID = wrapper.getBlockId();
			final int damageValue = wrapper.getDamageValue();

			String message = wrapper.getBlocksPlaced() + " ";

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

			names.add(message);
		}

		return Lang.PLACED_BLOCKS_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(names, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		for (int i = 0; i < wrappers.size(); i++) {
			final BlocksPlacedWrapper wrapper = wrappers.get(i);

			final int progressBar = getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.BLOCKS_PLACED.toString(),
					player.getUniqueId(), null, wrapper.getBlockId() + "",
					wrapper.getDamageValue() + "");

			if (i == 0) {
				progress = progress.concat(progressBar + "/"
						+ wrapper.getBlocksPlaced());
			} else {
				progress = progress.concat(" or " + progressBar + "/"
						+ wrapper.getBlocksPlaced());
			}
		}

		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final boolean enabled = getStatsPlugin().isEnabled();

		if (!enabled)
			return false;

		for (final BlocksPlacedWrapper wrapper : wrappers) {

			final int blockID = wrapper.getBlockId();
			final int damageValue = wrapper.getDamageValue();
			final int blocksPlaced = wrapper.getBlocksPlaced();

			int progress = 0;

			if (blockID > 0) {
				progress = getStatsPlugin().getNormalStat(
						StatsHandler.statTypes.BLOCKS_PLACED.toString(),
						player.getUniqueId(), null, blockID + "",
						damageValue + "");
			} else {
				progress = getStatsPlugin().getNormalStat(
						StatsHandler.statTypes.TOTAL_BLOCKS_PLACED.toString(),
						player.getUniqueId());
			}

			if (progress >= blocksPlaced)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			int blocksPlaced = 0;
			int blockId = -1;
			int damageValue = -1;

			if (options.length > 0) {
				blocksPlaced = Integer.parseInt(options[0].trim());
			}
			if (options.length > 1) {
				blockId = Integer.parseInt(options[0].trim());
				blocksPlaced = Integer.parseInt(options[1].trim());
			}
			if (options.length > 2) {
				damageValue = Integer.parseInt(options[2].trim());
			}

			wrappers.add(new BlocksPlacedWrapper(blockId, blocksPlaced,
					damageValue));
		}

		return !wrappers.isEmpty();
	}
}

class BlocksPlacedWrapper {

	private int blockId, blocksPlaced, damageValue;

	public BlocksPlacedWrapper(final int blockId, final int blocksPlaced,
			final int damageValue) {
		this.setBlockId(blockId);
		this.setBlocksPlaced(blocksPlaced);
		this.setDamageValue(damageValue);
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(final int blockId) {
		this.blockId = blockId;
	}

	public int getBlocksPlaced() {
		return blocksPlaced;
	}

	public void setBlocksPlaced(final int blocksPlaced) {
		this.blocksPlaced = blocksPlaced;
	}

	public int getDamageValue() {
		return damageValue;
	}

	public void setDamageValue(final int damageValue) {
		this.damageValue = damageValue;
	}

}
