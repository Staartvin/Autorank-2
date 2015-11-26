package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlocksBrokenRequirement extends Requirement {

	private final List<BlocksWrapper> wrappers = new ArrayList<BlocksWrapper>();

	//private int blockID = -1;
	//private int blocksBroken = 0;
	//private int damageValue = -1;

	@SuppressWarnings("deprecation")
	@Override
	public String getDescription() {
		final List<String> names = new ArrayList<String>();

		for (int i = 0; i < wrappers.size(); i++) {
			final BlocksWrapper wrapper = wrappers.get(i);

			final int blockID = wrapper.getBlockId();
			final int damageValue = wrapper.getDamageValue();

			final String displayName = wrapper.getDisplayName();

			String message = wrapper.getBlocksBroken() + " ";

			if (blockID > 0 && damageValue >= 0) {

				if (displayName.equals("")) {
					final ItemStack item = new ItemStack(blockID, 1,
							(short) damageValue);

					message = message.concat(item.getType().name()
							.replace("_", "").toLowerCase()
							+ " ");
				} else {
					message = message.concat(displayName + " ");
				}

			} else if (blockID > 0) {
				if (displayName.equals("")) {
					final ItemStack item = new ItemStack(blockID, 1);

					message = message.concat(item.getType().name()
							.replace("_", "").toLowerCase()
							+ " ");
				} else {
					message = message.concat(displayName + " ");
				}

			}

			message = message.concat("blocks");

			names.add(message);
		}

		String lang = Lang.BROKEN_BLOCKS_REQUIREMENT
				.getConfigValue(AutorankTools.seperateList(names, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		for (int i = 0; i < wrappers.size(); i++) {
			final BlocksWrapper wrapper = wrappers.get(i);

			int progressBar = 0;

			if (wrapper.getBlockId() < 0) {
				progressBar = getStatsPlugin().getNormalStat(
						StatsHandler.statTypes.TOTAL_BLOCKS_BROKEN.toString(),
						player.getUniqueId());
			} else {
				progressBar = getStatsPlugin().getNormalStat(
						StatsHandler.statTypes.BLOCKS_BROKEN.toString(),
						player.getUniqueId(), this.getWorld(),
						wrapper.getBlockId() + "",
						wrapper.getDamageValue() + "");
			}

			if (i == 0) {
				progress = progress.concat(progressBar + "/"
						+ wrapper.getBlocksBroken());
			} else {
				progress = progress.concat(" or " + progressBar + "/"
						+ wrapper.getBlocksBroken());
			}
		}

		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final boolean enabled = getStatsPlugin().isEnabled();

		if (!enabled)
			return false;

		for (final BlocksWrapper wrapper : wrappers) {

			final int blockID = wrapper.getBlockId();
			final int damageValue = wrapper.getDamageValue();
			final int blocksBroken = wrapper.getBlocksBroken();

			int progress = 0;

			if (blockID > 0) {
				progress = getStatsPlugin().getNormalStat(
						StatsHandler.statTypes.BLOCKS_BROKEN.toString(),
						player.getUniqueId(), this.getWorld(), blockID + "",
						damageValue + "");
			} else {
				progress = getStatsPlugin().getNormalStat(
						StatsHandler.statTypes.TOTAL_BLOCKS_BROKEN.toString(),
						player.getUniqueId());
			}

			if (progress >= blocksBroken)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {
		for (final String[] options : optionsList) {
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

			wrappers.add(new BlocksWrapper(blockId, blocksBroken, damageValue,
					displayName));
		}

		return !wrappers.isEmpty();
	}
}

class BlocksWrapper {

	private int blockId, blocksBroken, damageValue;
	private String displayName;

	public BlocksWrapper(final int blockId, final int blocksBroken,
			final int damageValue, final String displayName) {
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
