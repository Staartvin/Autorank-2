package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

/**
 * This requirement checks for the total time on the server
 * 
 * (i.e. the time now - the time he joined for the first time)
 * Date created: 13:49:33
 * 15 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class TotalTimeRequirement extends Requirement {

	int time = -1;
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	@Override
	public boolean setOptions(final String[] options, final boolean optional,
			final List<Result> results, final boolean autoComplete,
			final int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;

		if (options.length > 0)
			this.time = AutorankTools.stringToMinutes(options[0]);
		return (time != -1);
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		// the time he first joined the server
		long joinTime = player.getFirstPlayed();

		long currentTime = System.currentTimeMillis();

		// Difference in minutes
		long difference = (currentTime - joinTime) / 60000;

		return time != -1 && difference >= time;
	}

	@Override
	public String getDescription() {
		return Lang.TOTAL_TIME_REQUIREMENT
				.getConfigValue(new String[] { AutorankTools
						.minutesToString(time) });
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

		// the time he first joined the server
		long joinTime = player.getFirstPlayed();

		long currentTime = System.currentTimeMillis();

		// Difference in minutes
		long difference = (currentTime - joinTime) / 60000;

		String progress = "";
		progress = progress.concat(difference + " min" + "/" + time + " min");
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
