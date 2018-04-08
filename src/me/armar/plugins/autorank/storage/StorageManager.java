package me.armar.plugins.autorank.storage;

import me.armar.plugins.autorank.Autorank;

import java.util.ArrayList;
import java.util.List;

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
     * Get the primary storage provider (or null if storage provider is active).
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
}
