package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

public class TotalVotesRequirement extends Requirement {

	private int totalVotes = 0;
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	public TotalVotesRequirement() {
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
			totalVotes = Integer.parseInt(options[0]);
			return true;
		} catch (final Exception e) {
			totalVotes = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}

		// TODO Auto-generated method stub
		return getStatsPlugin().isEnabled()
				&& getStatsPlugin().getNormalStat("Votes", player.getName(),
						null) >= totalVotes;
	}

	@Override
	public String getDescription() {
		return Lang.VOTE_REQUIREMENT.getConfigValue(new String[] { totalVotes
				+ "" });
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
		progress = progress.concat(getStatsPlugin().getNormalStat("Votes",
				player.getName(), null)
				+ "/" + totalVotes);
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
