package me.armar.plugins.autorank.migration;

import me.armar.plugins.autorank.Autorank;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class MigrationablePlugin {

    private Autorank plugin;

    public MigrationablePlugin(Autorank instance) {
        this.plugin = instance;
    }

    public Autorank getPlugin() {
        return this.plugin;
    }

    /**
     * Check whether this plugin is ready for migration.
     *
     * @return true if they are, false otherwise.
     */
    public abstract boolean isReady();

    /**
     * Migrate play time of the given players from this plugin.
     *
     * @param uuids UUIDs to migrate the time to.
     * @return an integer telling you how much players have been migrated.
     */
    public abstract CompletableFuture<Integer> migratePlayTime(List<UUID> uuids);
}
