package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlocksBrokenRequirement extends Requirement {

	private List<BlocksWrapper> wrappers = new ArrayList<BlocksWrapper>();
	
	//private int blockID = -1;
	//private int blocksBroken = 0;
	//private int damageValue = -1;

	@SuppressWarnings("deprecation")
	@Override
	public String getDescription() {
		List<String> names = new ArrayList<String>();

		for (int i = 0; i < wrappers.size(); i++) {
			BlocksWrapper wrapper = wrappers.get(i);

			int blockID = wrapper.getBlockId();
			int damageValue = wrapper.getDamageValue();

			String message = wrapper.getBlocksBroken() + " ";

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

		return Lang.BROKEN_BLOCKS_REQUIREMENT
				.getConfigValue(AutorankTools.seperateList(names, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		for (int i = 0; i < wrappers.size(); i++) {
			BlocksWrapper wrapper = wrappers.get(i);

			int progressBar = getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.BLOCKS_BROKEN.toString(),
					player.getUniqueId(), null, wrapper.getBlockId() + "",
					wrapper.getDamageValue() + "");

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

		for (BlocksWrapper wrapper : wrappers) {

			int blockID = wrapper.getBlockId();
			int damageValue = wrapper.getDamageValue();
			int blocksBroken = wrapper.getBlocksBroken();

			int progress = 0;

			if (blockID > 0) {
				progress = getStatsPlugin().getNormalStat(
						StatsHandler.statTypes.BLOCKS_BROKEN.toString(),
						player.getUniqueId(), null, blockID + "",
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
	public boolean setOptions(List<String[]> optionsList) {
		for (String[] options : optionsList) {
			int blocksBroken = 0;
			int blockId = -1;
			int damageValue = -1;

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

			wrappers.add(new BlocksWrapper(blockId, blocksBroken, damageValue));
		}

		return !wrappers.isEmpty();
	}
}

class BlocksWrapper {

	private int blockId, blocksBroken, damageValue;

	public BlocksWrapper(int blockId, int blocksBroken, int damageValue) {
		this.setBlockId(blockId);
		this.setBlocksBroken(blocksBroken);
		this.setDamageValue(damageValue);
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

	public int getBlocksBroken() {
		return blocksBroken;
	}

	public void setBlocksBroken(int blocksBroken) {
		this.blocksBroken = blocksBroken;
	}

	public int getDamageValue() {
		return damageValue;
	}

	public void setDamageValue(int damageValue) {
		this.damageValue = damageValue;
	}

}
