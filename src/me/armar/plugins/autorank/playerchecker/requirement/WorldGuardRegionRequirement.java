package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.worldguardapi.WorldGuardHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

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

		String lang = Lang.WORLD_GUARD_REGION_REQUIREMENT
				.getConfigValue(AutorankTools.seperateList(regionNames, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		final String progress = "Cannot show progress";
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			// Is player in the same world as specified
			if (!this.getWorld().equals(player.getWorld().getName()))
				return false;
		}

		final WorldGuardHandler wgH = (WorldGuardHandler) this.getAutorank()
				.getDependencyManager().getDependency(dependency.WORLDGUARD);

		for (final String region : regionNames) {
			if (wgH.isInRegion(player, region))
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			if (options.length > 0) {
				regionNames.add(options[0].trim());
			}
		}

		return !regionNames.isEmpty();
	}
}
