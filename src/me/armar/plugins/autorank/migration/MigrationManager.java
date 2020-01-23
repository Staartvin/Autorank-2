package me.armar.plugins.autorank.migration;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.migration.implementations.OnTimeMigration;
import me.armar.plugins.autorank.migration.implementations.StatzMigration;
import me.armar.plugins.autorank.migration.implementations.VanillaMigration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MigrationManager {

    private Autorank plugin;

    private Map<Migrationable, MigrationablePlugin> migrationablePlugins = new HashMap<>();

    public MigrationManager(Autorank instance) {
        this.plugin = instance;

        migrationablePlugins.put(Migrationable.ONTIME, new OnTimeMigration(instance));
        migrationablePlugins.put(Migrationable.PLAYTIME, new VanillaMigration(instance)); // No, this is not a typo.
        // That plugin uses the statistics of MC.
        migrationablePlugins.put(Migrationable.VANILLA, new VanillaMigration(instance));
        migrationablePlugins.put(Migrationable.STATZ, new StatzMigration(instance));
    }

    public Optional<MigrationablePlugin> getMigrationablePlugin(Migrationable type) {

        if (!this.migrationablePlugins.containsKey(type)) return Optional.empty();

        return Optional.ofNullable(this.migrationablePlugins.get(type));
    }

    public enum Migrationable {ONTIME, PLAYTIME, STATZ, VANILLA}


}
