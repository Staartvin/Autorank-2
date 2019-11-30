package me.armar.plugins.autorank.storage;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This abstract class represents a provider that stores playtime of players. The internals ought to be implemented
 * by the class that extends this abstract class.
 * <p>
 * A storage provider can be identified by its name.
 */
public abstract class StorageProvider {

    public Autorank plugin;

    /**
     * Get the storage type that this storage provider uses.
     *
     * @return type of storage used
     */
    public abstract StorageType getStorageType();

    public StorageProvider(Autorank instance) {
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
    public abstract int getPlayerTime(TimeType timeType, UUID uuid);

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
    public abstract int getNumberOfStoredPlayers(TimeType timeType);

    /**
     * Get a list of players that have been stored for a given type of time
     *
     * @param timeType Type of time
     * @return a list of uuids that represent players that have been stored
     */
    public abstract List<UUID> getStoredPlayers(TimeType timeType);

    /**
     * Check whether all the storage files are still up-to-date or if they should be
     * reset. Autorank stores what values were previously found for the day,
     * week and month and compares these to the current values. If a new day has
     * arrived, the daily time file has to be reset.
     * <br>
     * <br>
     * Also see {@link #isDataFileOutdated(TimeType)} for more info.
     */
    public void doCalendarCheck() {
        // Check if all storage files are still up to date.
        // Check if daily, weekly or monthly files should be reset.

        final Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        for (final TimeType type : TimeType.values()) {
            // If data file is not outdated, leave it be.
            if (!this.isDataFileOutdated(type)) {
                continue;
            }

            // We should reset it now, it has expired.
            this.resetData(type);

            int value = 0;

            if (type == TimeType.DAILY_TIME) {
                value = cal.get(Calendar.DAY_OF_WEEK);

                if (plugin.getSettingsConfig().shouldBroadcastDataReset()) {
                    // Should we broadcast the reset?
                    plugin.getServer().broadcastMessage(Lang.RESET_DAILY_TIME.getConfigValue());
                }

            } else if (type == TimeType.WEEKLY_TIME) {
                value = cal.get(Calendar.WEEK_OF_YEAR);

                if (plugin.getSettingsConfig().shouldBroadcastDataReset()) {
                    // Should we broadcast the reset?

                    plugin.getServer().broadcastMessage(Lang.RESET_WEEKLY_TIME.getConfigValue());
                }
            } else if (type == TimeType.MONTHLY_TIME) {
                value = cal.get(Calendar.MONTH);

                if (plugin.getSettingsConfig().shouldBroadcastDataReset()) {
                    // Should we broadcast the reset?

                    plugin.getServer().broadcastMessage(Lang.RESET_MONTHLY_TIME.getConfigValue());
                }
            }

            // Update tracked storage type
            plugin.getInternalPropertiesConfig().setTrackedTimeType(type, value);
            // We reset leaderboard time so it refreshes again.
            plugin.getInternalPropertiesConfig().setLeaderboardLastUpdateTime(type, 0);

            // Update leaderboard of reset time
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                public void run() {
                    plugin.getLeaderboardManager().updateLeaderboard(type);
                }
            });

        }

    }

    /**
     * Check whether a storage file for a given time type is outdated. Autorank stores players' time in different
     * categories: daily, weekly, monthly and total. The daily time should be reset every day, as it only records
     * data of one day. The same logic applies to weekly and monthly data. A data file is outdated when its
     * expiration date has been reached (a day, week or month respectively).
     *
     * @param timeType Type of time
     * @return true if the file is outdated, false otherwise.
     */
    public boolean isDataFileOutdated(TimeType timeType) {
        // Should we reset a specific storage file?
        // Compare date to last date in internal properties
        final Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        if (timeType == TimeType.DAILY_TIME) {
            return cal.get(Calendar.DAY_OF_WEEK) != plugin.getInternalPropertiesConfig().getTrackedTimeType(timeType);
        } else if (timeType == TimeType.WEEKLY_TIME) {
            return cal.get(Calendar.WEEK_OF_YEAR) != plugin.getInternalPropertiesConfig().getTrackedTimeType(timeType);
        } else if (timeType == TimeType.MONTHLY_TIME) {
            return cal.get(Calendar.MONTH) != plugin.getInternalPropertiesConfig().getTrackedTimeType(timeType);
        }

        return false;
    }

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
