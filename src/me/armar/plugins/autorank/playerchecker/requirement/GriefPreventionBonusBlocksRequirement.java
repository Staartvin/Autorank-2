package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.GriefPreventionHandler;

public class GriefPreventionBonusBlocksRequirement extends Requirement {

	int bonusBlocks = -1;
	private GriefPreventionHandler handler = null;

	@Override
	public String getDescription() {
		return Lang.GRIEF_PREVENTION_BONUS_BLOCKS_REQUIREMENT.getConfigValue(bonusBlocks);
	}

	@Override
	public String getProgress(final Player player) {
		final int level = handler.getNumberOfBonusBlocks(player.getUniqueId());

		return level + "/" + bonusBlocks;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		if (!handler.isAvailable())
			return false;

		final int level = handler.getNumberOfBonusBlocks(player.getUniqueId());

		return level >= bonusBlocks;
	}

	@Override
	public boolean setOptions(final String[] options) {

		handler = (GriefPreventionHandler) this.getDependencyManager()
				.getDependencyHandler(Dependency.GRIEF_PREVENTION);

		if (options.length > 0) {
			bonusBlocks = Integer.parseInt(options[0]);
		}

		return bonusBlocks != -1 && handler != null;
	}
}
