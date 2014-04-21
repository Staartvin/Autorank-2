package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

public class BlocksMovedRequirement extends Requirement {

	private int blocksMoved = 0;
	private int movementType = 0;

	private int reqId;

	private boolean optional = false;
	private boolean autoComplete = false;
	List<Result> results = new ArrayList<Result>();

	public BlocksMovedRequirement() {
		super();
	}

	@Override
	public boolean setOptions(final String[] options, final boolean optional,
			final List<Result> results, final boolean autoComplete,
			final int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;

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

	@Override
	public boolean meetsRequirement(final Player player) {

		final boolean enabled = getStatsPlugin().isEnabled();

		boolean sufficient = false;
		sufficient = this
				.getStatsPlugin()
				.getNormalStat("blocks_moved", player.getName(),
						player.getWorld().getName(), movementType) > blocksMoved;

		return enabled && sufficient;
	}

	@Override
	public String getDescription() {
		String moveType = getMovementString();

		return Lang.BLOCKS_MOVED_REQUIREMENT.getConfigValue(new String[] {
				blocksMoved + " blocks", moveType });
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getStatsPlugin().getNormalStat(
				"blocks_moved", player.getName(), player.getWorld().getName(),
				movementType)
				+ "/" + blocksMoved + " (" + getMovementString() + ")");
		return progress;
	}

	@Override
	public boolean useAutoCompletion() {
		return autoComplete;
	}

	@Override
	public int getReqId() {
		return reqId;
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
}
