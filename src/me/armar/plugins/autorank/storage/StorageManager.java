package me.armar.plugins.autorank.storage;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * The StorageManager class is responsible for connection with the registered storage providers. It allows you to
 * obtain a storage provider and manipulate data using it. There can be multiple storage providers active at the same
 * time, but only one storage provider is designated as the 'primary storage provider'.
 * <br>
 * <br>
 * The primary storage provider is used to simplify the pathway to obtain info about a player. If you do not want
 * data of a specific storage provider, but just want the data of 'most trustworthy' storage provider,
 * you can use the primary storage provider.
 */
public class StorageManager {

    // Keep track of what storage providers we are using
    private List<StorageProvider> activeStorageProviders = new ArrayList<>();

    // Store what storage provider acts as the primary storage provider.
    private StorageProvider primaryStorageProvider = null;

    private Autorank plugin;

    public StorageManager(Autorank instance) {
        this.plugin = instance;
    }

    /**
     * Get the primary storage provider (or null if no storage provider is active).
     *
     * @return primary storage provider
     */
    public StorageProvider getPrimaryStorageProvider() {
        return this.primaryStorageProvider;
    }

    /**
     * Set the primary storage provider
     *
     * @param storageProvider Storage provider to set as primary
     * @throws IllegalArgumentException if provided storage provider is null
     */
    public void setPrimaryStorageProvider(StorageProvider storageProvider) throws IllegalArgumentException {
        if (storageProvider == null) {
            throw new IllegalArgumentException("StorageProvider cannot be null.");
        }

        this.primaryStorageProvider = storageProvider;
    }

    /**
     * Get a list of active storage providers by name (or an empty list if no storage provider is active).
     *
     * @return a list of names that correspond to the active storage providers
     */
    public List<String> getActiveStorageProviders() {
        List<String> storageProviders = new ArrayList<>();

        for (StorageProvider storageProvider : activeStorageProviders) {
            storageProviders.add(storageProvider.getName());
        }

        return storageProviders;
    }

    /**
     * Get an active storage provider by name. If no storage provider is active with the requested name, null is
     * returned.
     *
     * @param providerName Name of the storage provider to retrieve.
     * @return StorageProvider that matches the requested name
     */
    public StorageProvider getActiveStorageProvider(String providerName) {
        return this.activeStorageProviders.stream().filter(provider -> provider.getName().equalsIgnoreCase(providerName)).findFirst().orElseGet(() -> null);
    }

    /**
     * Register a new storage provider.
     *
     * @param storageProvider StorageProvider to register
     * @throws IllegalArgumentException if provided storage provider object is null
     */
    public void registerStorageProvider(StorageProvider storageProvider) throws IllegalArgumentException {
        if (storageProvider == null) {
            throw new IllegalArgumentException("StorageProvider cannot be null.");
        }

        activeStorageProviders.add(storageProvider);

        // If we have no primary storage provider yet, we set it to this one.
        if (getPrimaryStorageProvider() == null) {
            setPrimaryStorageProvider(storageProvider);
        }

        plugin.debugMessage("Registered new storage provider: " + storageProvider.getName() + " (type: " +
                storageProvider.getStorageType() + ")");
    }

    /**
     * Deregister an active storage provider (if it is active).
     *
     * @param storageProvider StorageProvider object to deregister
     * @throws IllegalArgumentException if provided storage provider object is null
     */
    public void deRegisterStorageProvider(StorageProvider storageProvider) throws IllegalArgumentException {
        if (storageProvider == null) {
            throw new IllegalArgumentException("StorageProvider cannot be null.");
        }

        activeStorageProviders.remove(storageProvider);
    }

    /**
     * Force a save on all storage providers so they have no in-memory changes that are not saved.
     */
    public void saveAllStorageProviders() {
        for (StorageProvider storageProvider : activeStorageProviders) {
            storageProvider.saveData();
        }
    }

    /**
     * Do a calendar check for all storage providers to see whether a certain storage file is outdated.
     */
    public void doCalendarCheck() {

        plugin.debugMessage("Performing a calendar check!");

        this.checkDataIsUpToDate();
    }

    /**
     * Set the time of a player (for a given time type) for all storage providers.
     *
     * @param timeType Type of time
     * @param uuid     UUID of the player
     * @param value    value to set the player time to
     */
    public void setPlayerTime(TimeType timeType, UUID uuid, int value) {
        for (StorageProvider storageProvider : activeStorageProviders) {
            storageProvider.setPlayerTime(timeType, uuid, value);
        }
    }

    /**
     * Set the time of a player (for all types of time) for all storage providers.
     *
     * @param uuid  UUID of the player
     * @param value value to set the player time to
     */
    public void setPlayerTime(UUID uuid, int value) {
        for (TimeType timeType : TimeType.values()) {
            this.setPlayerTime(timeType, uuid, value);
        }
    }

    /**
     * Set time of a player's for specific storage providers. This means that the player time will only
     * be updated for a player if the storage type of the storage provider matches the given storage type.
     *
     * @param storageType Type of storage
     * @param timeType    Type of time
     * @param uuid        UUID of the player
     * @param value       Value to set time to.
     */
    public void setPlayerTime(StorageProvider.StorageType storageType, TimeType timeType, UUID uuid, int value) {
        this.getActiveStorageProviders().forEach(storageProviderName -> {
            StorageProvider storageProvider = getActiveStorageProvider(storageProviderName);

            if (storageProvider == null) return;

            // Check if the storage type matches.
            if (storageProvider.getStorageType() != storageType) return;

            // Set player time to this storage type.
            storageProvider.setPlayerTime(timeType, uuid, value);
        });
    }

    /**
     * Add time to a player's current time (for a given time type) for all storage providers.
     *
     * @param timeType Type of time
     * @param uuid     UUID of the player
     * @param value    time to add.
     */
    public void addPlayerTime(TimeType timeType, UUID uuid, int value) {
        for (StorageProvider storageProvider : activeStorageProviders) {
            storageProvider.addPlayerTime(timeType, uuid, value);
        }
    }

    /**
     * Add time to a player's current time (for all time types) for all storage providers.
     *
     * @param uuid  UUID of the player
     * @param value time to add.
     */
    public void addPlayerTime(UUID uuid, int value) {
        for (TimeType timeType : TimeType.values()) {
            this.addPlayerTime(timeType, uuid, value);
        }
    }

    /**
     * Add time to a player's current time for specific storage providers. This means that the player time will only
     * be updated for a player if the storage type of the storage provider matches the given storage type.
     *
     * @param storageType Type of storage
     * @param timeType    Type of time
     * @param uuid        UUID of the player
     * @param value       Value to add.
     */
    public void addPlayerTime(StorageProvider.StorageType storageType, TimeType timeType, UUID uuid, int value) {
        this.getActiveStorageProviders().forEach(storageProviderName -> {
            StorageProvider storageProvider = getActiveStorageProvider(storageProviderName);

            if (storageProvider == null) return;

            // Check if the storage type matches.
            if (storageProvider.getStorageType() != storageType) return;

            // Add player time to this storage type.
            storageProvider.addPlayerTime(timeType, uuid, value);
        });
    }

    /**
     * Check whether there is a storage provider that is using the requested storage type.
     *
     * @param storageType Storage type to search
     * @return true if there is an active storage provider that is using the given storage type.
     */
    public boolean isStorageTypeActive(StorageProvider.StorageType storageType) {
        for (StorageProvider storageProvider : activeStorageProviders) {
            if (storageProvider.getStorageType() == storageType) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get an active storage provider that is using a certain type of storage. Note that the returned storage provider
     * may be non-deterministic if more than one storage provider with the same storage type is active.
     *
     * @param storageType Type of storage that the storage provider must use
     * @return first found active storage provider that uses the requested storage type (or null if none was found).
     */
    public StorageProvider getStorageProvider(StorageProvider.StorageType storageType) {
        for (StorageProvider storageProvider : activeStorageProviders) {
            if (storageProvider.getStorageType() == storageType) {
                return storageProvider;
            }
        }

        return null;
    }

    /**
     * Import data for all active storage providers (if they allow importing).
     */
    public void importDataForStorageProviders() {
        for (StorageProvider storageProvider : activeStorageProviders) {
            if (!storageProvider.canImportData()) {
                continue;
            }

            storageProvider.importData();
        }
    }

    /**
     * Back up all active storage providers that allow backups to be made.
     *
     * @return whether all backup were successfully made.
     */
    public boolean backupStorageProviders() {
        boolean successfulBackup = true;

        for (StorageProvider storageProvider : activeStorageProviders) {
            if (!storageProvider.canBackupData()) {
                continue;
            }

            boolean result = storageProvider.backupData();

            if (!result) {
                successfulBackup = false;
            }
        }

        return successfulBackup;
    }

    /**
     * Check whether all the storage files are still up-to-date or if they should be
     * reset. Autorank stores what values were previously found for the day,
     * week and month and compares these to the current values. If a new day has
     * arrived, the daily time file has to be reset.
     * <br>
     * <br>
     * Also see {@link #isDataFileOutdated(TimeType)} for more info.
     */
    public void checkDataIsUpToDate() {
        // Check if all storage files are still up to date.
        // Check if daily, weekly or monthly files should be reset.

        LocalDate today = LocalDate.now();

        for (final TimeType type : TimeType.values()) {
            // If data file is not outdated, leave it be.
            if (!this.isDataFileOutdated(type)) {
                continue;
            }

            // We should reset it now, it has expired.
            activeStorageProviders.forEach(provider -> provider.resetData(type));

            int value = 0;

            String broadcastMessage = "";

            if (type == TimeType.DAILY_TIME) {
                value = today.getDayOfWeek().getValue();
                broadcastMessage = Lang.RESET_DAILY_TIME.getConfigValue();
            } else if (type == TimeType.WEEKLY_TIME) {
                value = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                broadcastMessage = Lang.RESET_WEEKLY_TIME.getConfigValue();
            } else if (type == TimeType.MONTHLY_TIME) {
                value = today.getMonthValue();
                broadcastMessage = Lang.RESET_MONTHLY_TIME.getConfigValue();
            }

            if (plugin.getSettingsConfig().shouldBroadcastDataReset()) {
                // Should we broadcast the reset?
                plugin.getServer().broadcastMessage(broadcastMessage);
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
        LocalDate today = LocalDate.now();

        int trackedTimeType = plugin.getInternalPropertiesConfig().getTrackedTimeType(timeType);

        if (timeType == TimeType.DAILY_TIME) {
            return trackedTimeType != today.getDayOfWeek().getValue();
        } else if (timeType == TimeType.WEEKLY_TIME) {
            return trackedTimeType != today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        } else if (timeType == TimeType.MONTHLY_TIME) {
            return trackedTimeType != today.getMonthValue();
        }

        return false;
    }

}
