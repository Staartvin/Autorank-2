package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.worldguardapi.WorldGuardHandler;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

/**
 * This requirement checks for WorldGuard region
 * Date created: 13:49:33
 * 15 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class WorldGuardRegionRequirement extends Requirement {

	private String regionName = "";

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0)
			regionName = options[0].trim();
		
		return (regionName != null);
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		WorldGuardHandler wgH = (WorldGuardHandler) this.getAutorank()
				.getDependencyManager().getDependency(dependency.WORLDGUARD);

		return wgH.isInRegion(player, regionName);
	}

	@Override
	public String getDescription() {
		return Lang.WORLD_GUARD_REGION_REQUIREMENT
				.getConfigValue(new String[] { regionName });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "Cannot show progress";
		return progress;
	}
}
