package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

import org.bukkit.entity.Player;

public class ItemsCraftedRequirement extends Requirement {

	private int itemsCrafted = 0;

	@Override
	public String getDescription() {
		return Lang.ITEMS_CRAFTED_REQUIREMENT.getConfigValue(itemsCrafted + "");
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		final int progressBar = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.ITEMS_CRAFTED.toString(),
				player.getUniqueId());

		progress = progress.concat(progressBar + "/" + itemsCrafted);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		return this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.ITEMS_CRAFTED.toString(),
				player.getUniqueId()) > itemsCrafted;
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			itemsCrafted = Integer.parseInt(options[0]);
			return true;
		} catch (final Exception e) {
			itemsCrafted = 999999999;
			return false;
		}
	}
}
