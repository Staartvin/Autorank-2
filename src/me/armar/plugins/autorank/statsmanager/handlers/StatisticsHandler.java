package me.armar.plugins.autorank.statsmanager.handlers;

import java.util.HashMap;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.staartvin.statz.hooks.handlers.StatisticsAPIHandler;

public class StatisticsHandler extends StatsPlugin {

    private final Autorank plugin;

    private final StatisticsAPIHandler statsApi;

    public StatisticsHandler(final Autorank instance, final StatisticsAPIHandler statsAPI) {
        this.plugin = instance;

        statsApi = statsAPI;
    }

    @Override
    public int getNormalStat(StatType statType, final UUID uuid, final HashMap<String, Object> arguments) {
        // First argument is always the name, second arg is always the world
        return -1;
    }

    @Override
    public boolean isEnabled() {
        if (statsApi == null) {
            plugin.getLogger().info("Statistics (by bitWolfy) api library was not found!");
            return false;
        }

        if (!statsApi.isAvailable()) {
            plugin.getLogger().info("Statistics (by bitWolfy) is not enabled!");
            return false;
        }

        return true;
    }

}
