package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.worldguardapi.WorldGuardHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

/**
 * This requirement checks for WorldGuard region
 * Date created:  13:49:33
 * 15 jan. 2014
 * @author Staartvin
 *
 */
public class WorldGuardRegionRequirement extends Requirement {

	private String regionName = "";
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
			regionName = options[0].trim();
		return (regionName != null);
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}

		WorldGuardHandler wgH = (WorldGuardHandler) this.getAutorank().getDependencyManager().getDependency(dependency.WORLDGUARD);
		
		return wgH.isInRegion(player, regionName);
	}

	@Override
	public String getDescription() {
		return Lang.WORLD_GUARD_REGION_REQUIREMENT
				.getConfigValue(new String[] { regionName });
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
		String progress = "Cannot show progress";
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
