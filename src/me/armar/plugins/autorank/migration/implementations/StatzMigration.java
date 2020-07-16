package me.armar.plugins.autorank.migration.implementations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.migration.MigrationablePlugin;
import me.armar.plugins.autorank.storage.TimeType;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.StatzHook;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StatzMigration extends MigrationablePlugin {

    public StatzMigration(Autorank instance) {
        super(instance);
    }

    @Override
    public boolean isReady() {
        return getPlugin().getDependencyManager().isAvailable(Library.STATZ);
    }

    @Override
    public CompletableFuture<Integer> migratePlayTime(List<UUID> uuids) {
        if (uuids.isEmpty() || !this.isReady()) {
            return CompletableFuture.completedFuture(0);
        }

        return CompletableFuture.supplyAsync(() -> {

            getPlugin().debugMessage("Migrating player data from Statz!");

            int playersImported = 0;

            StatzHook statzHook =
                    (StatzHook) getPlugin().getDependencyManager().getLibraryHook(Library.STATZ).orElse(null);

            if (statzHook == null) return playersImported;

            for (UUID uuid : uuids) {
                double minutesPlayed = statzHook.getSpecificStatistics(PlayerStat.TIME_PLAYED, uuid);

                if (minutesPlayed <= 0) {
                    continue;
                }

                getPlugin().getPlayTimeStorageManager().addPlayerTime(TimeType.TOTAL_TIME, uuid,
                        (int) Math.round(minutesPlayed));

                playersImported++;
            }

            return playersImported;
        });
    }
}
