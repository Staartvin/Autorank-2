package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DamageTakenRequirement extends Requirement {

	private int damageTaken = 0;
	private Autorank plugin;
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	public DamageTakenRequirement() {
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}

	@Override
	public boolean setOptions(String[] options, boolean optional,
			List<Result> results, boolean autoComplete, int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;

		try {
			damageTaken = Integer.parseInt(options[0]);
			return true;
		} catch (Exception e) {
			damageTaken = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(Player player) {
		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}

		// TODO Auto-generated method stub
		return plugin.getStatsHandler().isEnabled()
				&& plugin.getStatsHandler()
						.getNormalStat(player.getName(), "Damage taken", null) >= damageTaken;
	}

	@Override
	public String getDescription() {
		return Lang.DAMAGE_TAKEN_REQUIREMENT.getConfigValue(new String[] {damageTaken + ""});
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
				.getNormalStat(player.getName(), "Damage taken", null) + "/" + damageTaken);
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
