package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.worldguardapi.WorldGuardHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

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

	List<String> regionNames = new ArrayList<String>();

	@Override
	public String getDescription() {
		return Lang.WORLD_GUARD_REGION_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(regionNames, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		final String progress = "Cannot show progress";
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final WorldGuardHandler wgH = (WorldGuardHandler) this.getAutorank()
				.getDependencyManager().getDependency(dependency.WORLDGUARD);

		for (String region : regionNames) {
			if (wgH.isInRegion(player, region))
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(List<String[]> optionsList) {
		
		for (String[] options: optionsList) {
			if (options.length > 0) {
				regionNames.add(options[0].trim());
			}
		}
		
		return !regionNames.isEmpty();
	}
}
