package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

public class MobKillsRequirement extends Requirement {

	private String mobType = null;
	private int totalMobsKilled = -1;

	@Override
	public String getDescription() {

		String desc = "";

		if (mobType == null || mobType.trim().equals("")) {
			desc = Lang.TOTAL_MOBS_KILLED_REQUIREMENT.getConfigValue(totalMobsKilled + " mobs");
		} else {
			String entityName;

			if (mobType.equalsIgnoreCase("wither_skeleton")) {
				entityName = "WITHER SKELETON";
			} else if (mobType.equalsIgnoreCase("charged_creeper")) {
				entityName = "CHARGED CREEPER";
			} else if (mobType.equalsIgnoreCase("spider_jockey")) {
				entityName = "SPIDER JOCKEY";
			} else if (mobType.equalsIgnoreCase("chicken_jockey")) {
				entityName = "CHICKEN JOCKEY";
			} else if (mobType.equalsIgnoreCase("killer_rabbit")) {
				entityName = "KILLER RABBIT";
			} else if (mobType.equalsIgnoreCase("elder_guardian")) {
				entityName = "ELDER GUARDIAN";
			} else {
				entityName = EntityType.valueOf(mobType.toUpperCase()).toString();
			}

			entityName = entityName.replace("_", " ");

			desc = Lang.TOTAL_MOBS_KILLED_REQUIREMENT
					.getConfigValue(totalMobsKilled + " " + entityName.toLowerCase() + "(s)");
		}

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			desc = desc.concat(" (in world '" + this.getWorld() + "')");
		}

		return desc;
	}

	@Override
	public String getProgress(final Player player) {

		final int killed = getStatsPlugin().getNormalStat(StatsHandler.statTypes.MOBS_KILLED, player.getUniqueId(),
				AutorankTools.makeStatsInfo("world", this.getWorld(), "mobType", mobType));

		String entityType = mobType;

		if (mobType == null) {
			entityType = "mobs";
		}

		return killed + "/" + totalMobsKilled + " " + entityType.replace("_", " ") + "(s)";
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		if (!this.getStatsPlugin().isEnabled())
			return false;

		final int killed = getStatsPlugin().getNormalStat(StatsHandler.statTypes.MOBS_KILLED, player.getUniqueId(),
				AutorankTools.makeStatsInfo("world", this.getWorld(), "mobType", mobType));

		return killed >= totalMobsKilled;
	}

	@Override
	public boolean setOptions(final String[] options) {

		totalMobsKilled = Integer.parseInt(options[0]);

		if (options.length > 1) {
			mobType = options[1].trim().replace(" ", "_");
		}

		return totalMobsKilled != -1;
	}
}
