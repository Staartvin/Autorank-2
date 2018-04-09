package me.armar.plugins.autorank.storage.mysql;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;

import java.util.List;
import java.util.UUID;

public class MySQLStorageProvider extends StorageProvider {

    // TODO implement MySQL provider
    public MySQLStorageProvider(Autorank instance) {
        super(instance);

        // Initialise provider to make it ready for use.
        if (!this.initialiseProvider()) {
            plugin.debugMessage("There was an error loading storage provider '" + getName() + "'.");
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.DATABASE;
    }

    @Override
    public void setPlayerTime(TimeType timeType, UUID uuid, int time) {

    }

    @Override
    public int getPlayerTime(TimeType timeType, UUID uuid) {
        return 0;
    }

    @Override
    public void resetDataFile(TimeType timeType) {

    }

    @Override
    public void addPlayerTime(TimeType timeType, UUID uuid, int timeToAdd) {

    }

    @Override
    public String getName() {
        return "MySQLStorageProvider";
    }

    @Override
    public boolean initialiseProvider() {
        return false;
    }

    @Override
    public int purgeOldEntries(int threshold) {
        return 0;
    }

    @Override
    public int getNumberOfStoredPlayers(TimeType timeType) {
        return 0;
    }

    @Override
    public List<UUID> getStoredPlayers(TimeType timeType) {
        return null;
    }

    @Override
    public void saveData() {

    }

    @Override
    public boolean canImportData() {
        return false;
    }

    @Override
    public void importData() {

    }

    @Override
    public boolean canBackupData() {
        return false;
    }

    @Override
    public boolean backupData() {
        return false;
    }
}
