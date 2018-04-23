package me.armar.plugins.autorank.storage;

import me.armar.plugins.autorank.Autorank;

import java.util.ArrayList;
import java.util.List;
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
     * Get a list of active storage providers by name(or an empty list if no storage provider is active).
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
        for (StorageProvider storageProvider : activeStorageProviders) {
            if (storageProvider.getName().equalsIgnoreCase(providerName)) {
                return storageProvider;
            }
        }

        return null;
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
        for (StorageProvider storageProvider : activeStorageProviders) {
            storageProvider.doCalendarCheck();
        }
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

}
