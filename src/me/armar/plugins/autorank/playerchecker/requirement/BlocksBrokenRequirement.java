package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

public class BlocksBrokenRequirement extends Requirement {

	BlocksWrapper wrapper = null;

	@Override
	public String getDescription() {
		final ItemStack item = wrapper.getItem();

		final StringBuilder arg = new StringBuilder(item.getAmount() + " ");

		if (wrapper.getDisplayName() != null) {
			// Show displayname instead of material name
			arg.append(wrapper.getDisplayName());
		} else {
			if (item.getType().toString().contains("AIR")) {
				arg.append("blocks");
			} else {
				arg.append(item.getType().toString().replace("_", " ").toLowerCase());
			}
			
			if (wrapper.showShortValue()) {
				arg.append(" (Dam. value: " + item.getDurability() + ")");
			}
		}

		String lang = Lang.BROKEN_BLOCKS_REQUIREMENT.getConfigValue(arg.toString());

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getProgress(final Player player) {

		int progressBar = 0;

		if (wrapper.getItem().getTypeId() < 0) {
			progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.TOTAL_BLOCKS_BROKEN,
					player.getUniqueId(), AutorankTools.makeStatsInfo());
		} else {
			progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_BROKEN, player.getUniqueId(),
					AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID", wrapper.getItem().getTypeId(), "dataValue",
							wrapper.getItem().getDurability()));
		}

		return progressBar + "/" + wrapper.getBlocksBroken();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean meetsRequirement(final Player player) {

		if (!getStatsPlugin().isEnabled())
			return false;

		final int blockID = wrapper.getItem().getTypeId();
		final int blocksBroken = wrapper.getBlocksBroken();

		int progress = 0;

		if (blockID > 0) {
			progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_BROKEN, player.getUniqueId(),
					AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID", wrapper.getItem().getTypeId(), "dataValue",
							wrapper.getItem().getDurability()));
		} else {
			progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.TOTAL_BLOCKS_BROKEN, player.getUniqueId(),
					AutorankTools.makeStatsInfo());
		}

		return progress >= blocksBroken;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean setOptions(final String[] options) {
		int id = -1;
		int amount = 1;
		short data = 0;

		String displayName = null;
		boolean showShortValue = false;
		boolean useDisplayName = false;

		if (options.length > 0) {
			amount = Integer.parseInt(options[0].trim());
		}		
		if (options.length > 1) {
			id = AutorankTools.stringtoInt(options[0]);
			amount = Integer.parseInt(options[1].trim());
		}		
		if (options.length > 2) {
			data = (short) AutorankTools.stringtoInt(options[2]);
			// Short value can make a difference, thus we show it.
			showShortValue = true;
		}
		if (options.length > 3) {
			// Displayname
			displayName = options[3];
		}
		if (options.length > 4) {
			// use display name?
			useDisplayName = (options[4].equalsIgnoreCase("true") ? true : false);
		}

		// item = new ItemStack(id, 1, (short) 0, data);
		final ItemStack item = new ItemStack(id, amount, data);

		wrapper = new BlocksWrapper(item, displayName, showShortValue, useDisplayName);

		wrapper.setBlocksBroken(amount);

		return wrapper != null && amount > 0;
	}
}

class BlocksWrapper extends ItemWrapper {

	private int blocksBroken; // How many items does the player need to break?

	public BlocksWrapper(ItemStack item, String displayName, boolean showShortValue, boolean useDisplayName) {
		super(item, displayName, showShortValue, useDisplayName);
	}

	public int getBlocksBroken() {
		return blocksBroken;
	}

	public void setBlocksBroken(final int blocksBroken) {
		this.blocksBroken = blocksBroken;
	}
}
