package me.armar.plugins.autorank.playerchecker.requirement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
	public boolean setOptions(String[] options, boolean optional, List<Result> results, boolean autoComplete, int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;
		
		try {
			factionPower = Double.parseDouble(options[0]);
			return true;
		} catch (Exception e) {
			factionPower = 0;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(Player player) {
		// TODO Auto-generated method stub
		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}
		
		return this.getAutorank().getFactionsHandler().isEnabled() && this.getAutorank().getFactionsHandler().getFactionPower(player) > factionPower;
	}

	@Override
	public String getDescription() {
		return Lang.FACTIONS_POWER_REQUIREMENT.getConfigValue(new String[] {factionPower + ""});
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
		DecimalFormat df = new DecimalFormat("#.##");
		String doubleRounded = df.format(getAutorank().getFactionsHandler().getFactionPower(player));
		
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
