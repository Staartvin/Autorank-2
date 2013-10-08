package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;
import org.bukkit.entity.Player;

public class PlayerKillsRequirement extends Requirement {

	private int totalPlayersKilled = 0;
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	public PlayerKillsRequirement() {
		super();
	}

	@Override
	public boolean setOptions(String[] options, boolean optional,
			List<Result> results, boolean autoComplete, int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;

		try {
			totalPlayersKilled = Integer.parseInt(options[0]);
			return true;
		} catch (Exception e) {
			totalPlayersKilled = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(Player player) {

		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}

		// TODO Auto-generated method stub
		return this.getAutorank().getStatsHandler().isEnabled()
				&& this.getAutorank().getStatsHandler().getTotalMobsKilled(
						player.getName(), "player") >= totalPlayersKilled;
	}

	@Override
	public String getDescription() {
		return Lang.PLAYER_KILLS_REQUIREMENT
				.getConfigValue(new String[] { totalPlayersKilled + ""});
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
	public String getProgress(Player player) {
		String progress = "";
		progress = progress.concat(getAutorank().getStatsHandler()
				.getTotalMobsKilled(player.getName(), "player")
				+ "/"
				+ totalPlayersKilled);
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
