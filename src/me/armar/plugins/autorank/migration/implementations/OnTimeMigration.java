package me.armar.plugins.autorank.migration.implementations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.migration.MigrationablePlugin;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.OnTimeHook;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OnTimeMigration extends MigrationablePlugin {

    public OnTimeMigration(Autorank instance) {
        super(instance);
    }

    @Override
    public boolean isReady() {
        return getPlugin().getDependencyManager().isAvailable(Library.ONTIME);
    }

    @Override
    public CompletableFuture<Integer> migratePlayTime(List<UUID> uuids) {

        if (uuids.isEmpty() || !this.isReady()) {
            return CompletableFuture.completedFuture(0);
        }

        return CompletableFuture.supplyAsync(() -> {

            OnTimeHook onTimeHook = (OnTimeHook) getPlugin().getDependencyManager().getLibraryHook(Library.ONTIME);

            getPlugin().debugMessage("Migrating player data from OnTime!");

            int playersImported = 0;

            for (UUID uuid : uuids) {

                String playerName = null;

                try {
                    playerName = UUIDManager.getPlayerName(uuid).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    continue;
                }

                // Skip the player if the playername couldn't be found.
                if (playerName == null) continue;

                if (!onTimeHook.isPlayerStored(playerName)) {
                    getPlugin().debugMessage("Player '" + playerName + "' doesn't have any information stored in " +
                            "OnTime");
                    continue;
                }

                // Time played (in ms)
                long totalPlayed = onTimeHook.getPlayerData(playerName, "TOTALPLAY");
                long todayPlayed = onTimeHook.getPlayerData(playerName, "TODAYPLAY");
                long weekPlayed = onTimeHook.getPlayerData(playerName, "WEEKPLAY");
                long monthPlayed = onTimeHook.getPlayerData(playerName, "MONTHPLAY");

                getPlugin().getPlayTimeStorageManager().addPlayerTime(TimeType.TOTAL_TIME, uuid,
                        (int) (totalPlayed / 60000));
                getPlugin().getPlayTimeStorageManager().addPlayerTime(TimeType.DAILY_TIME, uuid,
                        (int) (todayPlayed / 60000));
                getPlugin().getPlayTimeStorageManager().addPlayerTime(TimeType.WEEKLY_TIME, uuid,
                        (int) (weekPlayed / 60000));
                getPlugin().getPlayTimeStorageManager().addPlayerTime(TimeType.MONTHLY_TIME, uuid,
                        (int) (monthPlayed / 60000));

                playersImported++;
            }

            return playersImported;
        });
    }
}
