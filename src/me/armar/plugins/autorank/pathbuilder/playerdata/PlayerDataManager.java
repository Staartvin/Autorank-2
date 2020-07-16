package me.armar.plugins.autorank.pathbuilder.playerdata;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.Autorank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for recording what data storages are available and provides other classes access to them.
 */
public class PlayerDataManager {

    private final Autorank plugin;
    private final List<PlayerDataStorage> activeDataStorage = new ArrayList<>();

    public PlayerDataManager(Autorank instance) {
        this.plugin = instance;
    }

    /**
     * Add a data storage so it can be used.
     *
     * @param storage Storage to add.
     */
    public void addDataStorage(@NonNull PlayerDataStorage storage) {
        if (activeDataStorage.stream().anyMatch(stored -> stored.getDataStorageType() == storage.getDataStorageType()))
            return;

        plugin.debugMessage("Registered player data storage (" + storage.getDataStorageType() + ")");

        activeDataStorage.add(storage);
    }

    /**
     * Get all active data storages.
     *
     * @return a list of active data storages. May be empty.
     */
    public List<PlayerDataStorage> getActiveDataStorages() {
        return activeDataStorage;
    }

    /**
     * Get an active data storage of a specific type.
     *
     * @param type Type of data storage to obtain
     * @return A data storage if present or nothing if none with the given type is active.
     */
    public Optional<PlayerDataStorage> getDataStorage(@NonNull PlayerDataStorageType type) {
        return activeDataStorage.stream().filter(stored -> stored.getDataStorageType() == type).findFirst();
    }

    /**
     * Get the primary data storage provider. By default, this is the local data storage.
     *
     * @return Primary data storage provider.
     */
    public Optional<PlayerDataStorage> getPrimaryDataStorage() {
        // By default, the local player data storage is the primary data storage.
        return this.getDataStorage(PlayerDataStorageType.LOCAL);
    }

    public enum PlayerDataStorageType {
        /**
         * A local data storage stores data that should only be used to determine details of a player on a single
         * instance of Autorank. If a local data storage says a player has completed a path, another instance of
         * Autorank (on another server perhaps) should not use this data.
         */
        LOCAL,
        /**
         * A global data storage stores data that is relevant for all Autorank instances on a network. If a player
         * completes a path, all servers should use the data from the global data storage to check whether a player
         * has completed it.
         */
        GLOBAL
    }


}
