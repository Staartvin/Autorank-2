package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

import org.bukkit.entity.Player;

public class BlocksMovedRequirement extends Requirement {

	private final List<BlocksMovedWrapper> wrappers = new ArrayList<BlocksMovedWrapper>();

	@Override
	public String getDescription() {

		final List<String> names = new ArrayList<String>();

		for (final BlocksMovedWrapper wrapper : wrappers) {
			names.add(wrapper.getBlocksMoved() + " blocks "
					+ wrapper.getMovementType());
		}

		String desc = "";

		for (int i = 0; i < names.size(); i++) {

			if (i == 0) {
				desc = Lang.BLOCKS_MOVED_REQUIREMENT.getConfigValue()
						.replace("{0}", "").replace("{1}", "").trim()
						+ " " + names.get(i);
			} else {
				desc = desc.concat(" or " + names.get(i));
			}
		}

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			desc = desc.concat(" (in world '" + this.getWorld() + "')");
		}

		return desc;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		for (int i = 0; i < wrappers.size(); i++) {
			final BlocksMovedWrapper wrapper = wrappers.get(i);

			final int progressBar = getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.BLOCKS_MOVED.toString(),
					player.getUniqueId(), this.getWorld(),
					wrapper.getRawMovementType());

			if (i == 0) {
				progress = progress.concat(progressBar + "/"
						+ wrapper.getBlocksMoved() + " ("
						+ wrapper.getMovementType() + ")");
			} else {
				progress = progress.concat(" or " + progressBar + "/"
						+ wrapper.getBlocksMoved() + " ("
						+ wrapper.getMovementType() + ")");
			}

		}

		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final boolean enabled = getStatsPlugin().isEnabled();

		if (!enabled)
			return false;

		for (final BlocksMovedWrapper wrapper : wrappers) {

			final int count = this.getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.BLOCKS_MOVED.toString(),
					player.getUniqueId(), this.getWorld(),
					wrapper.getRawMovementType());

			if (count >= wrapper.getBlocksMoved()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			int blocksMoved = 0;
			int movementType = 0;

			if (options.length > 0) {
				blocksMoved = Integer.parseInt(options[0].trim());
			}
			if (options.length > 1) {
				movementType = Integer.parseInt(options[1].trim());
			}

			wrappers.add(new BlocksMovedWrapper(blocksMoved, movementType));
		}

		return !wrappers.isEmpty();
	}
}

class BlocksMovedWrapper {

	private int blocksMoved = 0;
	private String movementType = "";
	private int rawMovementType = 0;

	public BlocksMovedWrapper(final int blocksMoved, final int moveType) {
		this.blocksMoved = blocksMoved;
		this.movementType = getMovementString(moveType);
		this.rawMovementType = moveType;
	}

	private String getMovementString(final int moveType) {
		switch (moveType) {
		case 0:
			return "by foot";
		case 1:
			return "by boat";
		case 2:
			return "by cart";
		case 3:
			return "by pig";
		case 4:
			return "by piggy-cart";
		case 5:
			return "by horse";
		default:
			return "by foot";
		}
	}

	public int getBlocksMoved() {
		return blocksMoved;
	}

	public String getMovementType() {
		return movementType;
	}

	public int getRawMovementType() {
		return rawMovementType;
	}
}
