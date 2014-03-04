package me.armar.plugins.autorank.playerchecker.requirement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.factionsapi.FactionsHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

public class FactionPowerRequirement extends Requirement {

	private double factionPower = 0;
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	public FactionPowerRequirement() {
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
			factionPower = Double.parseDouble(options[0]);
			return true;
		} catch (final Exception e) {
			factionPower = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		// TODO Auto-generated method stub
		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}

		FactionsHandler fHandler = (FactionsHandler) this.getAutorank().getDependencyManager().getDependency(dependency.FACTIONS); 
		
		return fHandler.getFactionPower(player) > factionPower;
	}

	@Override
	public String getDescription() {
		return Lang.FACTIONS_POWER_REQUIREMENT
				.getConfigValue(new String[] { factionPower + "" });
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
		final DecimalFormat df = new DecimalFormat("#.##");
		final String doubleRounded = df.format(((FactionsHandler) getAutorank()
				.getDependencyManager().getDependency(dependency.FACTIONS)).getFactionPower(player));

		progress = progress.concat(doubleRounded + "/" + factionPower);
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
