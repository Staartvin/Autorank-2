package me.armar.plugins.autorank.playtimes;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This manager is the access point for getting play times of players. Note that there are three ways to get the time
 * of a player and that they (may) give different values. Autorank keeps track of the local time (time played on this
 * server) and global time (time played across all servers that are sharing the same database).
 * <p></p>
 * These times are kept by the {@link PlayTimeStorageProvider}s. Each storage provider is either a local or global
 * time keeper. However, only one of them can be the primary storage provider. The admin indicates the primary
 * storage provider. The primary time (time of the primary storage provider) is hence either local or global and
 * will be used for showing leaderboards, checking time requirements and in the commands.
 *
 * <p></p>
 * To get the local time, use {@link #getLocalPlayTime(TimeType, UUID)}. Evidently, to get the global time of a
 * player (if it is being tracked), see {@link #getGlobalPlayTime(TimeType, UUID)}. To get the primary time of the
 * player (either local or global time, use {@link #getPlayTime(TimeType, UUID, TimeUnit)}.
 */
public class PlayTimeManager {

    // How often do we check whether a player is still online? (in minutes)
    public static int INTERVAL_MINUTES = 5;
    //Mutiplier, only works if "use real time" is true
    public static int MULTIPLIER = 1;

    private final Autorank plugin;

    public PlayTimeManager(final Autorank plugin) {
        this.plugin = plugin;

        INTERVAL_MINUTES = plugin.getSettingsConfig().getIntervalTime();

        plugin.getLogger().info("Interval check every " + INTERVAL_MINUTES + " minutes.");
    }

    /**
     * Get global time (for a given type of time) of a player.
     *
     * @param timeType Type of time
     * @param uuid     UUID of player
     * @return global time of a player or -1 if no active storage provider supports global time.
     */
    public CompletableFuture<Integer> getGlobalPlayTime(TimeType timeType, UUID uuid) {

        if (!plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            return CompletableFuture.completedFuture(-1);
        }

        return plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.DATABASE).getPlayerTime
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
        if (!plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            return;
        }

        plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.DATABASE).setPlayerTime(timeType,
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
        if (!plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            return;
        }

        plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.DATABASE).addPlayerTime(timeType,
                uuid, valueToAdd);
    }

    /**
     * Get local play time of a player (for a specific type of time).
     *
     * @param timeType Type of time
     * @param uuid     UUID of player
     * @return value of time or -1 if no data could be found for the given player
     */
    public CompletableFuture<Integer> getLocalPlayTime(TimeType timeType, UUID uuid) {
        if (!plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.FLAT_FILE)) {
            return CompletableFuture.completedFuture(-1);
        }

        return plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.FLAT_FILE).getPlayerTime
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
        if (!plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.FLAT_FILE)) {
            return;
        }

        plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.FLAT_FILE).setPlayerTime(timeType,
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
        if (!plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.FLAT_FILE)) {
            return;
        }

        plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.FLAT_FILE).addPlayerTime(timeType,
                uuid, valueToAdd);
    }

    /**
     * Get the play time of a player based on the primary storage provider. Note that this may take some time and
     * therefore it is recommended to create a callback based on this method.
     * <p>
     * Note that this method is deprecated. Use {@link #getPlayTime(TimeType, UUID, TimeUnit)} instead.
     *
     * @param timeType Type of playtime to get.
     * @param uuid     UUID of the player
     * @return the play time (in minutes).
     */
    @Deprecated
    public CompletableFuture<Integer> getPlayTime(TimeType timeType, UUID uuid) {
        return plugin.getPlayTimeStorageManager().getPrimaryStorageProvider().getPlayerTime
                (timeType, uuid);
    }

    /**
     * Get the play time of a player based on the primary storage provider. Note that this may take some time and
     * therefore it is recommended to create a callback based on this method.
     *
     * @param timeType Type of time you want to get
     * @param uuid     UUID of the player to get the time from
     * @param timeUnit Unit you want the player time in.
     * @return time played in the specified time unit.
     */
    public CompletableFuture<Long> getPlayTime(TimeType timeType, UUID uuid, TimeUnit timeUnit) {
        // This is in units

        return CompletableFuture.supplyAsync(() -> {

            int minutes = 0;

            try {
                minutes = plugin.getPlayTimeStorageManager().getPrimaryStorageProvider().getPlayerTime(timeType,
                        uuid).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return timeUnit.convert(minutes, TimeUnit.MINUTES);
        });
    }

}
