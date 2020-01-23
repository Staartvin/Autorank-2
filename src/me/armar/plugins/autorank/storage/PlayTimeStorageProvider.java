package me.armar.plugins.autorank.storage;

import me.armar.plugins.autorank.Autorank;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This abstract class represents a provider that stores playtime of players. The internals ought to be implemented
 * by the class that extends this abstract class.
 * <p>
 * A storage provider can be identified by its name.
 */
public abstract class PlayTimeStorageProvider {

    public Autorank plugin;

    /**
     * Get the storage type that this storage provider uses.
     *
     * @return type of storage used
     */
    public abstract StorageType getStorageType();

    public PlayTimeStorageProvider(Autorank instance) {
        this.plugin = instance;
    }

    /**
     * Set a type of time of a player to the given value.
     *
     * @param timeType Type of time to adjust
     * @param uuid     UUID of the player
     * @param time     Value to set the player's time to.
     */
    public abstract void setPlayerTime(TimeType timeType, UUID uuid, int time);

    /**
     * Get time of a player (for a given type of time). Note that the time may be cached (depending on the storage
     * provider).
     *
     * @param timeType Type of time to request.
     * @param uuid     UUID of the player.
     * @return (Possibly cached) value of requested type of time for a player.
     */
    public abstract CompletableFuture<Integer> getPlayerTime(TimeType timeType, UUID uuid);

    /**
     * Reset data for a given type of time, setting all times (for the given time type) to zero for all players.
     *
     * @param timeType Type of time.
     */
    public abstract void resetData(TimeType timeType);

    /**
     * Add time to a player's time, for a given time type.
     *
     * @param timeType  Type of time
     * @param uuid      UUID of player
     * @param timeToAdd Time to add to the current value of the player's time
     */
    public abstract void addPlayerTime(TimeType timeType, UUID uuid, int timeToAdd);

    /**
     * Get the name of this storage provider.
     *
     * @return name of storage provider
     */
    public abstract String getName();

    /**
     * Initiliase storage provider to make it ready for use.
     *
     * @return whether the storage provider is initialised properly.
     */
    public abstract CompletableFuture<Boolean> initialiseProvider();

    /**
     * Remove data about players that have not been online for at least x time. The threshold is in days.
     *
     * @param threshold After how many days should we count a player's data to be 'old'?
     * @return the number of records deleted.
     */
    public abstract int purgeOldEntries(int threshold);

    /**
     * See {@link #purgeOldEntries(int)} for more details. This method assumes a threshold of 60 days, hence it
     * removes data of players that have not been online for 60 days.
     *
     * @return the number of records deleted.
     */
    public int purgeOldEntries() {
        return this.purgeOldEntries(60);
    }

    /**
     * Get how many players are stored by this storage provider for a given time type.
     *
     * @param timeType Type of time
     * @return number of players stored for this time type.
     */
    public abstract CompletableFuture<Integer> getNumberOfStoredPlayers(TimeType timeType);

    /**
     * Get a list of players that have been stored for a given type of time
     *
     * @param timeType Type of time
     * @return a list of uuids that represent players that have been stored
     */
    public abstract List<UUID> getStoredPlayers(TimeType timeType);

    /**
     * Force save all data that the storage provider is currently using.
     */
    public abstract void saveData();

    /**
     * Check whether this storage provider supports importing data.
     *
     * @return true if this storage provider can import data. False otherwise.
     */
    public abstract boolean canImportData();

    /**
     * Import data using this storage provider. This may overwrite existing data about players (depending on the
     * storage provider)
     */
    public abstract void importData();

    /**
     * Check whether this storage provider allows you to back up its data.
     *
     * @return true if you can backup data of this storage provider
     */
    public abstract boolean canBackupData();

    /**
     * Back up data of this storage provider. Note that this only works for storage providers that support this, see
     * {@link #canBackupData()}.
     *
     * @return whether the back up succeeded.
     */
    public abstract boolean backupData();

    /**
     * Delete backups that were made before a certain date.
     *
     * @param date Date to delete backups.
     * @return how many backups were deleted.
     */
    public abstract int clearBackupsBeforeDate(LocalDate date);

    /**
     * Check whether this storage provider is loaded and ready for use.
     *
     * @return true if it can be used, false if not.
     */
    public abstract boolean isLoaded();

    /**
     * Different types of storage that a StorageProvider may use.
     */
    public enum StorageType {
        FLAT_FILE, DATABASE, HYBRID, OTHER}
}
