package me.armar.plugins.autorank.playtimes;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.StatsPlugin.StatType;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.OnTimeHook;
import me.staartvin.plugins.pluginlibrary.hooks.StatsHook;

import java.util.UUID;

public class PlayTimeManager {

    // How often do we check whether a player is still online? (in minutes)
    public static int INTERVAL_MINUTES = 5;

    private final Autorank plugin;

    // What plugin should Autorank use to check time?
    private final AutorankDependency timePlugin;

    public PlayTimeManager(final Autorank plugin) {
        this.plugin = plugin;

        INTERVAL_MINUTES = plugin.getSettingsConfig().getIntervalTime();

        plugin.getLogger().info("Interval check every " + INTERVAL_MINUTES + " minutes.");

        timePlugin = plugin.getSettingsConfig().useTimeOf();
    }

    /**
     * Get the time of a player. <br>
     * This depends on what plugin is used to get the time from. <br>
     * Time in seconds.
     *
     * @param playerName Player to get the time for
     * @param cache      whether to only use cache or use real time values.
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
                playTime = ((StatsHook) plugin.getDependencyManager().getLibraryHook(Library.STATS)).getNormalStat
                        (uuid, "Playtime", null);
            } else {

                if (uuid == null)
                    return playTime;

                // Stats not found, using Autorank's system.
                playTime = plugin.getStorageManager().getPrimaryStorageProvider().getPlayerTime(TimeType.TOTAL_TIME,
                        uuid) * 60;
            }
        } else if (timePlugin.equals(AutorankDependency.ONTIME)) {
            playTime = (int) (((OnTimeHook) plugin.getDependencyManager().getLibraryHook(Library.ONTIME))
                    .getPlayerData(playerName, "TOTALPLAY") / 1000);
        } else if (timePlugin.equals(AutorankDependency.STATZ)) {
            playTime = (int) ((StatzAPIHandler) plugin.getDependencyManager().getDependency(AutorankDependency.STATZ))
                    .getTotalOf(uuid, StatType.TIME_PLAYED, null);
            playTime = playTime * 60;
        } else {

            if (uuid == null)
                return playTime;

            // Use internal system of Autorank.
            playTime = plugin.getStorageManager().getPrimaryStorageProvider().getPlayerTime(TimeType.TOTAL_TIME,
                    uuid) * 60;
        }

        return playTime;
    }

    /**
     * Get the time plugin that is used to retrieve time of a player
     *
     * @return a {@linkplain AutorankDependency} representing the time plugin
     */
    public AutorankDependency getUsedTimePlugin() {
        return timePlugin;
    }

    /**
     * Get global time (for a given type of time) of a player.
     *
     * @param timeType Type of time
     * @param uuid     UUID of player
     * @return global time of a player or -1 if no active storage provider supports global time.
     */
    public int getGlobalPlayTime(TimeType timeType, UUID uuid) {
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.DATABASE)) {
            return -1;
        }

        return plugin.getStorageManager().getStorageProvider(StorageProvider.StorageType.DATABASE).getPlayerTime
                (timeType, uuid);
    }

    /**
     * Set global time (for a given type of time) of a player.
     *
     * @param timeType Type of time
     * @param uuid     UUID of player
     * @param value    time to set
     */
    public void setGlobalPlayTime(TimeType timeType, UUID uuid, int value) {
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.DATABASE)) {
            return;
        }

        plugin.getStorageManager().getStorageProvider(StorageProvider.StorageType.DATABASE).setPlayerTime(timeType,
                uuid, value);
    }

    /**
     * Add global time (for a given type of time) of a player.
     *
     * @param timeType   Type of time
     * @param uuid       UUID of player
     * @param valueToAdd time to add
     */
    public void addGlobalPlayTime(TimeType timeType, UUID uuid, int valueToAdd) {
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.DATABASE)) {
            return;
        }

        plugin.getStorageManager().getStorageProvider(StorageProvider.StorageType.DATABASE).addPlayerTime(timeType,
                uuid, valueToAdd);
    }

    /**
     * Get local play time of a player (for a specific type of time).
     *
     * @param timeType Type of time
     * @param uuid     UUID of player
     * @return value of time or -1 if no data could be found for the given player
     */
    public int getLocalPlayTime(TimeType timeType, UUID uuid) {
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.FLAT_FILE)) {
            return -1;
        }

        return plugin.getStorageManager().getStorageProvider(StorageProvider.StorageType.FLAT_FILE).getPlayerTime
                (timeType, uuid);
    }

    /**
     * Set the local play time of a player (for a specific type of time).
     *
     * @param timeType Type of time
     * @param uuid     UUID of the player
     * @param value    value to set the local play time.
     */
    public void setLocalPlayTime(TimeType timeType, UUID uuid, int value) {
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.FLAT_FILE)) {
            return;
        }

        plugin.getStorageManager().getStorageProvider(StorageProvider.StorageType.FLAT_FILE).setPlayerTime(timeType,
                uuid, value);
    }

    /**
     * Add local time (for a given type of time) of a player.
     *
     * @param timeType   Type of time
     * @param uuid       UUID of player
     * @param valueToAdd time to add
     */
    public void addLocalPlayTime(TimeType timeType, UUID uuid, int valueToAdd) {
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.FLAT_FILE)) {
            return;
        }

        plugin.getStorageManager().getStorageProvider(StorageProvider.StorageType.FLAT_FILE).addPlayerTime(timeType,
                uuid, valueToAdd);
    }

}
