package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.McMMOHandler;

public class McMMOPowerLevelRequirement extends Requirement {

	private McMMOHandler handler = null;
	int powerLevel = -1;

	@Override
	public String getDescription() {
		return Lang.MCMMO_POWER_LEVEL_REQUIREMENT.getConfigValue(powerLevel + "");
	}

	@Override
	public String getProgress(final Player player) {
		final int level = handler.getPowerLevel(player);

		return level + "/" + powerLevel;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		if (!handler.isAvailable())
			return false;

		final int level = handler.getPowerLevel(player);

		return level >= powerLevel;
	}

	@Override
	public boolean setOptions(final String[] options) {

		handler = (McMMOHandler) this.getDependencyManager().getDependencyHandler(Dependency.MCMMO);

		if (options.length > 0) {
			powerLevel = Integer.parseInt(options[0]);

		}

		return powerLevel != -1 && handler != null;
	}
}
