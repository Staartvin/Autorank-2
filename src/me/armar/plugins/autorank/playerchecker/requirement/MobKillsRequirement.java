package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class MobKillsRequirement extends Requirement {

	private int totalMobsKilled = 0;
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	private String mobType = null;
	List<Result> results = new ArrayList<Result>();

	public MobKillsRequirement() {
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
			totalMobsKilled = Integer.parseInt(options[0]);

			if (options.length > 1) {
				mobType = options[1].trim().replace(" ", "_");
			}
			return true;
		} catch (final Exception e) {
			totalMobsKilled = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}

		// TODO Auto-generated method stub
		return this.getAutorank().getStatsHandler().isEnabled()
				&& this.getAutorank().getStatsHandler()
						.getTotalMobsKilled(player.getName(), mobType, null) >= totalMobsKilled;
	}

	@Override
	public String getDescription() {
		if (mobType == null) {
			return Lang.TOTAL_MOBS_KILLED_REQUIREMENT
					.getConfigValue(new String[] { totalMobsKilled + " mobs" });
		} else {
			final EntityType entity = EntityType.valueOf(mobType.toUpperCase());
			return Lang.TOTAL_MOBS_KILLED_REQUIREMENT
					.getConfigValue(new String[] { totalMobsKilled + " "
							+ entity.toString().toLowerCase().replace("_", " ")
							+ "(s)" });
		}

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
		progress = progress.concat(getAutorank().getStatsHandler()
				.getTotalMobsKilled(player.getName(), mobType, null)
				+ "/"
				+ totalMobsKilled);
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
}
