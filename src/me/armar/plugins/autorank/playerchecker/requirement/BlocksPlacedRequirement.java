package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

public class BlocksPlacedRequirement extends Requirement {

	BlocksPlacedWrapper wrapper = null;

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

		String lang = Lang.PLACED_BLOCKS_REQUIREMENT.getConfigValue(arg.toString());

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
			progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.TOTAL_BLOCKS_PLACED,
					player.getUniqueId(), AutorankTools.makeStatsInfo());
		} else {
			if (wrapper.showShortValue()) {
				// Use datavalue
				progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_PLACED, player.getUniqueId(),
						AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID", wrapper.getItem().getTypeId(),
								"dataValue", wrapper.getItem().getDurability()));
			} else {
				if (wrapper.getItem().getType() == Material.AIR) {
					// Id was not given so only check amount
					progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_PLACED,
							player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld()));
				} else {
					// ID was given, but no data value
					progressBar = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_PLACED,
							player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID",
									wrapper.getItem().getTypeId()));
				}
			}
		}

		return progressBar + "/" + wrapper.getBlocksPlaced();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean meetsRequirement(final Player player) {
		if (!getStatsPlugin().isEnabled())
			return false;

		int progress = 0;

		if (wrapper.getItem().getTypeId() < 0) {
			progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_PLACED, player.getUniqueId(),
					AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID", wrapper.getItem().getTypeId(),
							"dataValue", wrapper.getItem().getDurability()));
		} else {
			if (wrapper.showShortValue()) {
				// Use datavalue
				progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_PLACED, player.getUniqueId(),
						AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID", wrapper.getItem().getTypeId(),
								"dataValue", wrapper.getItem().getDurability()));
			} else {
				if (wrapper.getItem().getType() == Material.AIR) {
					// Id was not given so only check amount
					progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_PLACED,
							player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld()));
				} else {
					// ID was given, but no data value
					progress = getStatsPlugin().getNormalStat(StatsHandler.statTypes.BLOCKS_PLACED,
							player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld(), "typeID",
									wrapper.getItem().getTypeId()));
				}
			}
		}

		return progress >= wrapper.getBlocksPlaced();
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

		wrapper = new BlocksPlacedWrapper(item, displayName, showShortValue, useDisplayName);

		wrapper.setBlocksPlaced(amount);

		return wrapper != null && amount > 0;
	}
}

class BlocksPlacedWrapper extends ItemWrapper {

	private int blocksPlaced; // How many items does the player need to place?

	public BlocksPlacedWrapper(ItemStack item, String displayName, boolean showShortValue, boolean useDisplayName) {
		super(item, displayName, showShortValue, useDisplayName);
	}

	public int getBlocksPlaced() {
		return blocksPlaced;
	}

	public void setBlocksPlaced(final int blocksPlaced) {
		this.blocksPlaced = blocksPlaced;
	}
}
