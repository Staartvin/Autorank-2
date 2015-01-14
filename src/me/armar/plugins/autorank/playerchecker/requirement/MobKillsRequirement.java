package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class MobKillsRequirement extends Requirement {

	private String mobType = null;
	private int totalMobsKilled = 0;

	@Override
	public String getDescription() {
		if (mobType == null) {
			return Lang.TOTAL_MOBS_KILLED_REQUIREMENT
					.getConfigValue(new String[] { totalMobsKilled + " mobs" });
		} else {
			final EntityType entity = EntityType.valueOf(mobType.toUpperCase());
			return Lang.TOTAL_MOBS_KILLED_REQUIREMENT
					.getConfigValue(new String[] { totalMobsKilled + " "
							+ entity.toString().toLowerCase().replace("_", " ")
							+ "(s)" });
		}

	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.MOBS_KILLED.toString(),
				player.getUniqueId(), null, mobType)
				+ "/" + totalMobsKilled);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		return getStatsPlugin().isEnabled()
				&& getStatsPlugin().getNormalStat(
						StatsHandler.statTypes.MOBS_KILLED.toString(),
						player.getUniqueId(), null, mobType) >= totalMobsKilled;
	}

	@Override
	public boolean setOptions(final String[] options) {
		try {
			totalMobsKilled = Integer.parseInt(options[0]);

			if (options.length > 1) {
				mobType = options[1].trim().replace(" ", "_");
			}
			return true;
		} catch (final Exception e) {
			totalMobsKilled = 0;
			return false;
		}
	}
}
