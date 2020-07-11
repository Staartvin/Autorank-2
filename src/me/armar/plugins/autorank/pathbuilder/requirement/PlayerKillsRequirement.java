package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.query.StatisticQuery;
import me.armar.plugins.autorank.statsmanager.query.parameter.ParameterType;

import java.util.UUID;

public class PlayerKillsRequirement extends AbstractRequirement {

    int totalPlayersKilled = -1;

    @Override
    public String getDescription() {
        String lang = Lang.PLAYER_KILLS_REQUIREMENT.getConfigValue(totalPlayersKilled + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        final int killed = getStatsPlugin().getNormalStat(StatsPlugin.StatType.PLAYERS_KILLED, uuid,
                StatisticQuery.makeStatisticQuery(ParameterType.WORLD.getKey(), this.getWorld()));

        return killed + "/" + totalPlayersKilled + " player(s)";
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        if (!getStatsPlugin().isEnabled())
            return false;

        final int killed = getStatsPlugin().getNormalStat(StatsPlugin.StatType.PLAYERS_KILLED, uuid,
                StatisticQuery.makeStatisticQuery(ParameterType.WORLD.getKey(), this.getWorld()));

        return killed >= totalPlayersKilled;
    }

    @Override
    public boolean initRequirement(final String[] options) {
        try {
            totalPlayersKilled = Integer.parseInt(options[0]);
        } catch (final Exception e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (totalPlayersKilled < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        final int killed = getStatsPlugin().getNormalStat(StatsPlugin.StatType.PLAYERS_KILLED, uuid,
                StatisticQuery.makeStatisticQuery(ParameterType.WORLD.getKey(), this.getWorld()));

        return killed * 1.0d / this.totalPlayersKilled;
    }
}
