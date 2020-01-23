package me.armar.plugins.autorank.playtimes;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayTimeManager {

    // How often do we check whether a player is still online? (in minutes)
    public static int INTERVAL_MINUTES = 5;

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
     *
     * @param timeType Type of playtime to get.
     * @param uuid     UUID of the player
     * @return the play time (in minutes).
     */
    public CompletableFuture<Integer> getPlayTime(TimeType timeType, UUID uuid) {
        return plugin.getPlayTimeStorageManager().getPrimaryStorageProvider().getPlayerTime
                (timeType, uuid);
    }

}
