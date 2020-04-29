package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.query.StatisticQuery;
import me.armar.plugins.autorank.statsmanager.query.parameter.ParameterType;
import me.staartvin.utils.pluginlibrary.Library;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class MobKillsRequirement extends AbstractRequirement {

    private String mobType = null;
    private int totalMobsKilled = -1;

    @Override
    public String getDescription() {

        String desc = "";

        if (mobType == null || mobType.trim().equals("")) {
            desc = Lang.TOTAL_MOBS_KILLED_REQUIREMENT.getConfigValue(totalMobsKilled + " mobs");
        } else {
            desc = Lang.TOTAL_MOBS_KILLED_REQUIREMENT
                    .getConfigValue(totalMobsKilled + " " + mobType.toLowerCase().replace("_", " ") + "(s)");
        }

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            desc = desc.concat(" (in world '" + this.getWorld() + "')");
        }

        return desc;
    }

    @Override
    public String getProgressString(UUID uuid) {

        final int killed = getStatsPlugin().getNormalStat(StatsPlugin.StatType.MOBS_KILLED, uuid,
                StatisticQuery.makeStatisticQuery(
                        ParameterType.WORLD.getKey(), this.getWorld(),
                        ParameterType.MOB_TYPE.getKey(), mobType));

        String entityType = mobType;

        if (mobType == null) {
            entityType = "mobs";
        }

        return killed + "/" + totalMobsKilled + " " + entityType.replace("_", " ") + "(s)";
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!this.getStatsPlugin().isEnabled())
            return false;

        final int killed = getStatsPlugin().getNormalStat(StatsPlugin.StatType.MOBS_KILLED, uuid,
                StatisticQuery.makeStatisticQuery(
                        ParameterType.WORLD.getKey(), this.getWorld(),
                        ParameterType.MOB_TYPE.getKey(), mobType));

        return killed >= totalMobsKilled;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.STATZ);

        totalMobsKilled = Integer.parseInt(options[0]);

        if (options.length > 1) {
            mobType = options[1].trim().replace(" ", "_");

            if (mobType.equalsIgnoreCase("charged_creeper")) {
                mobType = "POWERED CREEPER";
            } else if (mobType.equalsIgnoreCase("spider_jockey")) {
                mobType = "SPIDER JOCKEY";
            } else if (mobType.equalsIgnoreCase("chicken_jockey")) {
                mobType = "CHICKEN JOCKEY";
            } else if (mobType.equalsIgnoreCase("killer_rabbit")) {
                mobType = "KILLER RABBIT";
            } else {
                mobType = EntityType.valueOf(mobType.toUpperCase()).name();
            }
        }

        if (totalMobsKilled < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        final int killed = getStatsPlugin().getNormalStat(StatsPlugin.StatType.MOBS_KILLED, uuid,
                StatisticQuery.makeStatisticQuery(
                        ParameterType.WORLD.getKey(), this.getWorld(),
                        ParameterType.MOB_TYPE.getKey(), mobType));

        return killed * 1.0d / this.totalMobsKilled;
    }
}
