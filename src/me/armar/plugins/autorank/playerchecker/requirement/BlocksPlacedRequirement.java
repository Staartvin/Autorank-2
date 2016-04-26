package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

public class BlocksPlacedRequirement extends Requirement {

	BlocksPlacedWrapper wrapper = null;

	// private int blockID = -1;
	// private int blocksPlaced = 0;
	// private int damageValue = -1;

	@SuppressWarnings("deprecation")
	@Override
	public String getDescription() {

		final int blockID = wrapper.getBlockId();
		final int damageValue = wrapper.getDamageValue();
		final String displayName = wrapper.getDisplayName();

		String message = wrapper.getBlocksPlaced() + " ";

		if (blockID > 0 && damageValue >= 0) {
			if (displayName.equals("")) {
				final ItemStack item = new ItemStack(blockID, 1, (short) damageValue);

				message = message.concat(item.getType().name().replace("_", "").toLowerCase() + " ");
			} else {
				message = message.concat(displayName + " ");
			}

		} else if (blockID > 0) {
			if (displayName.equals("")) {
				final ItemStack item = new ItemStack(blockID, 1);

				message = message.concat(item.getType().name().replace("_", "").toLowerCase() + " ");
			} else {
				message = message.concat(displayName + " ");
			}
		}

		message = message.concat("blocks");

		String lang = Lang.PLACED_BLOCKS_REQUIREMENT.getConfigValue(message);

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		int progressBar = 0;

		if (wrapper.getBlockId() < 0) {
			progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.TOTAL_BLOCKS_PLACED.toString(),
					player.getUniqueId());
		} else {
			progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_PLACED.toString(),
					player.getUniqueId(), this.getWorld(), wrapper.getBlockId() + "", wrapper.getDamageValue() + "");
		}

		return progressBar + "/" + wrapper.getBlocksPlaced();
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final boolean enabled = getStatsPlugin().isEnabled();

		if (!enabled)
			return false;

		final int blockID = wrapper.getBlockId();
		final int damageValue = wrapper.getDamageValue();
		final int blocksPlaced = wrapper.getBlocksPlaced();

		int progress = 0;

		if (blockID > 0) {
			progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_PLACED.toString(),
					player.getUniqueId(), this.getWorld(), blockID + "", damageValue + "");
		} else {
			progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.TOTAL_BLOCKS_PLACED.toString(),
					player.getUniqueId());
		}

		return progress >= blocksPlaced;
	}

	@Override
	public boolean setOptions(final String[] options) {

		int blocksPlaced = 0;
		int blockId = -1;
		int damageValue = -1;
		String displayName = "";

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
		if (options.length > 3) {
			displayName = options[3].trim();
		}

		wrapper = new BlocksPlacedWrapper(blockId, blocksPlaced, damageValue, displayName);

		return wrapper != null;
	}
}

class BlocksPlacedWrapper {

	private int blockId, blocksPlaced, damageValue;
	private String displayName;

	public BlocksPlacedWrapper(final int blockId, final int blocksPlaced, final int damageValue,
			final String displayName) {
		this.setBlockId(blockId);
		this.setBlocksPlaced(blocksPlaced);
		this.setDamageValue(damageValue);
		this.setDisplayName(displayName);
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

}
