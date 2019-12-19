package me.armar.plugins.autorank.statsmanager.handlers;

import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.query.StatisticQuery;

import java.util.UUID;

/**
 * This class is used when no stats plugin is found. This does not do anything,
 * but it allows Autorank to run without erroring all over the place.
 *
 * @author Staartvin
 */
public class FallbackHandler extends StatsPlugin {

    @Override
    public int getNormalStat(StatType statType, UUID uuid, StatisticQuery query) {
        return 0;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
