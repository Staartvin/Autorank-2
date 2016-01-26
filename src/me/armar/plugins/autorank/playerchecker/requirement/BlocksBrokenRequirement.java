package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

public class BlocksBrokenRequirement extends Requirement {

	BlocksWrapper wrapper = null;

	// private int blockID = -1;
	// private int blocksBroken = 0;
	// private int damageValue = -1;

	@SuppressWarnings("deprecation")
	@Override
	public String getDescription() {
		final int blockID = wrapper.getBlockId();
		final int damageValue = wrapper.getDamageValue();

		final String displayName = wrapper.getDisplayName();

		String message = wrapper.getBlocksBroken() + " ";

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

		String lang = Lang.BROKEN_BLOCKS_REQUIREMENT.getConfigValue(message);

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
			progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.TOTAL_BLOCKS_BROKEN.toString(),
					player.getUniqueId());
		} else {
			progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_BROKEN.toString(),
					player.getUniqueId(), this.getWorld(), wrapper.getBlockId() + "", wrapper.getDamageValue() + "");
		}

		return progressBar + "/" + wrapper.getBlocksBroken();
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		if (!getStatsPlugin().isEnabled())
			return false;

		final int blockID = wrapper.getBlockId();
		final int damageValue = wrapper.getDamageValue();
		final int blocksBroken = wrapper.getBlocksBroken();

		int progress = 0;

		if (blockID > 0) {
			progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_BROKEN.toString(),
					player.getUniqueId(), this.getWorld(), blockID + "", damageValue + "");
		} else {
			progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.TOTAL_BLOCKS_BROKEN.toString(),
					player.getUniqueId());
		}

		return progress >= blocksBroken;
	}

	@Override
	public boolean setOptions(String[] options) {
		int blocksBroken = 0;
		int blockId = -1;
		int damageValue = -1;
		String displayName = "";

		if (options.length > 0) {
			blocksBroken = Integer.parseInt(options[0].trim());
		}
		if (options.length > 1) {
			blockId = Integer.parseInt(options[0].trim());
			blocksBroken = Integer.parseInt(options[1].trim());
		}
		if (options.length > 2) {
			damageValue = Integer.parseInt(options[2].trim());
		}
		if (options.length > 3) {
			displayName = options[3].trim();
		}

		wrapper = new BlocksWrapper(blockId, blocksBroken, damageValue, displayName);

		return wrapper != null;
	}
}

class BlocksWrapper {

	private int blockId, blocksBroken, damageValue;
	private String displayName;

	public BlocksWrapper(final int blockId, final int blocksBroken, final int damageValue, final String displayName) {
		this.setBlockId(blockId);
		this.setBlocksBroken(blocksBroken);
		this.setDamageValue(damageValue);
		this.setDisplayName(displayName);
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(final int blockId) {
		this.blockId = blockId;
	}

	public int getBlocksBroken() {
		return blocksBroken;
	}

	public void setBlocksBroken(final int blocksBroken) {
		this.blocksBroken = blocksBroken;
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
