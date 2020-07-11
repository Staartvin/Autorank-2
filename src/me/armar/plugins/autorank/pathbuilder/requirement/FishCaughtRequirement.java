package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.query.StatisticQuery;
import me.armar.plugins.autorank.statsmanager.query.parameter.ParameterType;

import java.util.UUID;

public class FishCaughtRequirement extends AbstractRequirement {

    int fishCaught = -1;

    @Override
    public String getDescription() {

        String lang = Lang.FISH_CAUGHT_REQUIREMENT.getConfigValue(fishCaught + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        final int progressBar = this.getStatsPlugin().getNormalStat(StatsPlugin.StatType.FISH_CAUGHT,
                uuid, StatisticQuery.makeStatisticQuery(ParameterType.WORLD.getKey(), this.getWorld()));

        return progressBar + "/" + fishCaught;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!getStatsPlugin().isEnabled())
            return false;

        final int fish = this.getStatsPlugin().getNormalStat(StatsPlugin.StatType.FISH_CAUGHT, uuid,
                StatisticQuery.makeStatisticQuery(ParameterType.WORLD.getKey(), this.getWorld()));

        return fish >= fishCaught;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        try {
            fishCaught = Integer.parseInt(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (fishCaught < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        final int progressBar = this.getStatsPlugin().getNormalStat(StatsPlugin.StatType.FISH_CAUGHT,
                uuid, StatisticQuery.makeStatisticQuery(ParameterType.WORLD.getKey(), this.getWorld()));

        return progressBar * 1.0d / this.fishCaught;
    }
}
