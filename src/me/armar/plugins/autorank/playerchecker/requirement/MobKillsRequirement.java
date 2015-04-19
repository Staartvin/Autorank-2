package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class MobKillsRequirement extends Requirement {

	//private String mobType = null;
	//private int totalMobsKilled = 0;

	// [0] totalMobsKilled, [1] mobType
	List<String> mobsKilledCombined = new ArrayList<String>();

	@Override
	public String getDescription() {

		String desc = "";

		for (int i = 0; i < mobsKilledCombined.size(); i++) {
			final String mobsKilledCombinedString = mobsKilledCombined.get(i);
			//String[] split = skillCombined.split(";");
			final String total = AutorankTools.getStringFromSplitString(
					mobsKilledCombinedString, ";", 0);
			final String mobType = AutorankTools.getStringFromSplitString(
					mobsKilledCombinedString, ";", 1);

			if (i == 0) {
				if (mobType == null) {
					desc = Lang.TOTAL_MOBS_KILLED_REQUIREMENT
							.getConfigValue(total + " mobs");
				} else {
					final EntityType entity = EntityType.valueOf(mobType
							.toUpperCase());
					desc = Lang.TOTAL_MOBS_KILLED_REQUIREMENT
							.getConfigValue(total
									+ " "
									+ entity.toString().toLowerCase()
											.replace("_", " ") + "(s)");
				}
			} else {
				if (mobType == null) {
					desc = desc.concat(" or " + total + " mobs");
				} else {
					final EntityType entity = EntityType.valueOf(mobType
							.toUpperCase());
					desc = desc.concat(" or " + total + " "
							+ entity.toString().toLowerCase().replace("_", " ")
							+ "(s)");
				}
			}
		}

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			desc = desc.concat(" (in world '" + this.getWorld() + "')");
		}

		return desc;
	}

	@Override
	public String getProgress(final Player player) {

		String progress = "";

		for (int i = 0; i < mobsKilledCombined.size(); i++) {
			final String mobKilledCombined = mobsKilledCombined.get(i);
			String mobType = AutorankTools.getStringFromSplitString(
					mobKilledCombined, ";", 1);
			final String total = AutorankTools.getStringFromSplitString(
					mobKilledCombined, ";", 0);

			final int killed = getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.MOBS_KILLED.toString(),
					player.getUniqueId(), this.getWorld(), mobType);

			if (mobType == null) {
				mobType = "mobs";
			}

			if (i == 0) {
				progress = progress
						.concat(killed + "/" + total + " " + mobType);
			} else {
				progress = progress.concat(" or " + killed + "/" + total + " "
						+ mobType);
			}

		}
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		if (!this.getStatsPlugin().isEnabled())
			return false;

		for (int i = 0; i < mobsKilledCombined.size(); i++) {
			final String mobKilledCombined = mobsKilledCombined.get(i);
			final String mobType = AutorankTools.getStringFromSplitString(
					mobKilledCombined, ";", 1);
			final String total = AutorankTools.getStringFromSplitString(
					mobKilledCombined, ";", 0);

			final int killed = getStatsPlugin().getNormalStat(
					StatsHandler.statTypes.MOBS_KILLED.toString(),
					player.getUniqueId(), this.getWorld(), mobType);

			if (killed >= Integer.parseInt(total))
				return true;

		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			final int total = Integer.parseInt(options[0]);
			String mobType = null;

			if (options.length > 1) {
				mobType = options[1].trim().replace(" ", "_");
			}

			mobsKilledCombined.add(total + ";" + mobType);
		}

		return !mobsKilledCombined.isEmpty();
	}
}
