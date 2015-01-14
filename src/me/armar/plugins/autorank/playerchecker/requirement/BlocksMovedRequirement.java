package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

import org.bukkit.entity.Player;

public class BlocksMovedRequirement extends Requirement {

	private int blocksMoved = 0;
	private int movementType = 0;

	@Override
	public String getDescription() {
		final String moveType = getMovementString();

		return Lang.BLOCKS_MOVED_REQUIREMENT.getConfigValue(new String[] {
				blocksMoved + " blocks", moveType });
	}

	private String getMovementString() {
		switch (movementType) {
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

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.BLOCKS_MOVED.toString(),
				player.getUniqueId(), player.getWorld().getName(), movementType)
				+ "/" + blocksMoved + " (" + getMovementString() + ")");
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final boolean enabled = getStatsPlugin().isEnabled();

		boolean sufficient = false;
		sufficient = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.BLOCKS_MOVED.toString(),
				player.getUniqueId(), player.getWorld().getName(), movementType) > blocksMoved;

		return enabled && sufficient;
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			if (options.length > 0) {
				blocksMoved = Integer.parseInt(options[0].trim());
			}
			if (options.length > 1) {
				movementType = Integer.parseInt(options[1].trim());
			}
		} catch (final Exception e) {
			blocksMoved = 0;
			return false;
		}

		return true;

	}
}
