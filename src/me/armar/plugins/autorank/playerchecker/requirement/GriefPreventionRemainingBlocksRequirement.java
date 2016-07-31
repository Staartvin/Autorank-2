package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.GriefPreventionHandler;

public class GriefPreventionRemainingBlocksRequirement extends Requirement {

	int remainingBlocks = -1;
	private GriefPreventionHandler handler = null;

	@Override
	public String getDescription() {
		return Lang.GRIEF_PREVENTION_REMAINING_BLOCKS_REQUIREMENT.getConfigValue(remainingBlocks);
	}

	@Override
	public String getProgress(final Player player) {
		final int level = handler.getNumberOfRemainingBlocks(player.getUniqueId());

		return level + "/" + remainingBlocks;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		if (!handler.isAvailable())
			return false;

		final int level = handler.getNumberOfRemainingBlocks(player.getUniqueId());

		return level >= remainingBlocks;
	}

	@Override
	public boolean setOptions(final String[] options) {

		handler = (GriefPreventionHandler) this.getDependencyManager()
				.getDependencyHandler(Dependency.GRIEF_PREVENTION);

		if (options.length > 0) {
			remainingBlocks = Integer.parseInt(options[0]);
		}

		return remainingBlocks != -1 && handler != null;
	}
}
