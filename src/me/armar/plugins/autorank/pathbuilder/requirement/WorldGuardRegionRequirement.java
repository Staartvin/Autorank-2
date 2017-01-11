package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.WorldGuardHandler;

/**
 * This requirement checks for WorldGuard region
 * Date created: 13:49:33
 * 15 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class WorldGuardRegionRequirement extends Requirement {

	private WorldGuardHandler handler;
	String regionName = null;

	@Override
	public String getDescription() {

		String lang = Lang.WORLD_GUARD_REGION_REQUIREMENT.getConfigValue(regionName);

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

		return handler.isInRegion(player, regionName);
	}

	@Override
	public boolean setOptions(final String[] options) {
		handler = (WorldGuardHandler) this.getAutorank().getDependencyManager()
				.getDependencyHandler(Dependency.WORLDGUARD);

		if (options.length > 0) {
			regionName = options[0].trim();
		}

		return regionName != null && handler != null;
	}
}
