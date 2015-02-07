package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class McMMOPowerLevelRequirement extends Requirement {

	private int powerLevel = 0;
	private McMMOHandler handler = null;

	@Override
	public String getDescription() {

		return Lang.MCMMO_POWER_LEVEL_REQUIREMENT
				.getConfigValue(new String[] { powerLevel + "" });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		final int level = handler.getPowerLevel(player);

		progress = progress.concat(level + "/" + powerLevel);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final int level = handler.getPowerLevel(player);

		return level > 0 && level >= powerLevel;
	}

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0) {
			powerLevel = Integer.parseInt(options[0]);

			handler = (McMMOHandler) this.getDependencyManager().getDependency(
					dependency.MCMMO);
		}

		return true;
	}
}
