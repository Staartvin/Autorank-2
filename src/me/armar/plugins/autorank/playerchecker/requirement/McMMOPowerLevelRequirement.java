package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class McMMOPowerLevelRequirement extends Requirement {

	private int powerLevel = 0;

	@Override
	public String getDescription() {

		return Lang.MCMMO_POWER_LEVEL_REQUIREMENT
				.getConfigValue(new String[] { powerLevel + "" });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		final int level = ((McMMOHandler) this.getAutorank()
				.getDependencyManager().getDependency(dependency.MCMMO))
				.getPowerLevel(player);

		progress = progress.concat(level + "/" + powerLevel);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final McMMOHandler handler = (McMMOHandler) this.getAutorank()
				.getDependencyManager().getDependency(dependency.MCMMO);

		final int level = handler.getPowerLevel(player);

		return level > 0 && level >= powerLevel;
	}

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0) {
			powerLevel = Integer.parseInt(options[0]);
		}

		return true;
	}
}
