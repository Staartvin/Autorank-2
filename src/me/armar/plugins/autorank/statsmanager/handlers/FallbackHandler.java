package me.armar.plugins.autorank.statsmanager.handlers;

import java.util.HashMap;
import java.util.UUID;

import me.armar.plugins.autorank.statsmanager.StatsPlugin;

/**
 * This class is used when no stats plugin is found. This does not do anything,
 * but it allows Autorank to run without erroring all over the place.
 * 
 * @author Staartvin
 * 
 */
public class FallbackHandler extends StatsPlugin {

    @Override
    public int getNormalStat(StatType statType, final UUID uuid, HashMap<String, Object> arguments) {
        return 0;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
