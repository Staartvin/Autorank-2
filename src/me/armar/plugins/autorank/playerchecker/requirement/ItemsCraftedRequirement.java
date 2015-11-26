package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class ItemsCraftedRequirement extends Requirement {

	private final List<Integer> itemsCrafted = new ArrayList<Integer>();

	@Override
	public String getDescription() {

		String lang = Lang.ITEMS_CRAFTED_REQUIREMENT
				.getConfigValue(AutorankTools.seperateList(itemsCrafted, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		final int progressBar = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.ITEMS_CRAFTED.toString(),
				player.getUniqueId(), this.getWorld());

		progress = AutorankTools.makeProgressString(itemsCrafted, "",
				progressBar + "");
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final int realItemsCrafted = this.getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.ITEMS_CRAFTED.toString(),
				player.getUniqueId(), this.getWorld());

		for (final int items : itemsCrafted) {
			if (realItemsCrafted >= items)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			itemsCrafted.add(Integer.parseInt(options[0]));
		}

		return !itemsCrafted.isEmpty();
	}
}
