package me.armar.plugins.autorank.playtimes;

import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.StatsPlugin.statTypes;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.OnTimeHandler;
import me.staartvin.statz.hooks.handlers.StatsAPIHandler;

public class PlaytimeManager {

    // Autorank keeps track of total time, time online on one day, time online
    // in a week and time online in a month.
    // There are all tracked in minutes.

    public static int INTERVAL_MINUTES = 5;

    private final Autorank plugin;

    // What plugin should Autorank use to check time?
    private final AutorankDependency timePlugin;

    public PlaytimeManager(final Autorank plugin) {
        this.plugin = plugin;

        INTERVAL_MINUTES = plugin.getConfigHandler().getIntervalTime();

        plugin.getLogger().info("Interval check every " + INTERVAL_MINUTES + " minutes.");

        timePlugin = plugin.getConfigHandler().useTimeOf();
    }

    /**
     * Get the time of a player. <br>
     * This depends on what plugin is used to get the time from. <br>
     * Time is seconds.
     * 
     * @param playerName
     *            Player to get the time for
     * @param cache
     *            whether to only use cache or use real time values.
     * @return play time of given player or 0 if not found.
     */
    public int getTimeOfPlayer(final String playerName, final boolean cache) {

        int playTime = 0;

        UUID uuid = null;

        // If using cache, just get the latest stored uuid
        if (cache) {
            uuid = plugin.getUUIDStorage().getStoredUUID(playerName);
        } else {
            uuid = UUIDManager.getUUIDFromPlayer(playerName);
        }

        // Determine what plugin to use for getting the time.
        if (timePlugin.equals(AutorankDependency.STATS)) {
            final StatsPlugin stats = plugin.getHookedStatsPlugin();

            if (stats instanceof StatsHandler) {
                // In seconds
                playTime = ((StatsAPIHandler) plugin.getDependencyManager().getDependencyHandler(Dependency.STATS))
                        .getTotalPlayTime(uuid, null);
            } else {

                if (uuid == null)
                    return playTime;

                // Stats not found, using Autorank's system.
                playTime = plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid) * 60;
            }
        } else if (timePlugin.equals(AutorankDependency.ONTIME)) {
            playTime = ((OnTimeHandler) plugin.getDependencyManager().getDependencyHandler(Dependency.ON_TIME))
                    .getPlayTime(playerName);
            // Time is in minutes, so convert to seconds
            playTime = playTime * 60;
        } else if (timePlugin.equals(AutorankDependency.STATZ)) {
            playTime = (int) ((StatzAPIHandler) plugin.getDependencyManager().getDependency(AutorankDependency.STATZ))
                    .getTotalOf(uuid, statTypes.TIME_PLAYED, null);
            playTime = playTime * 60;
        } else {

            if (uuid == null)
                return playTime;

            // Use internal system of Autorank.
            playTime = plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid) * 60;
        }

        return playTime;
    }

}
