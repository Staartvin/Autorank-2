package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * This requirement checks for global playtime Date created: 13:49:53 15 jan.
 * 2014
 *
 * @author Staartvin
 */
public class GlobalTimeRequirement extends AbstractRequirement {

    private int globalTime = -1;

    @Override
    public String getDescription() {
        return Lang.GLOBAL_TIME_REQUIREMENT.getConfigValue(AutorankTools.timeToString(globalTime, Time.MINUTES));
    }

    @Override
    public String getProgress(final Player player) {

        int playTime = 0;
        try {
            playTime = getAutorank().getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, player
                    .getUniqueId()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return AutorankTools.timeToString(playTime, Time.MINUTES) + "/" + AutorankTools.timeToString(globalTime,
                Time.MINUTES);
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        int playTime = 0;

        try {
            playTime = this.getAutorank().getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return globalTime != -1 && playTime >= globalTime;
    }

    @Override
    public boolean setOptions(final String[] options) {

        globalTime = AutorankTools.stringToTime(options[0], Time.MINUTES);

        if (globalTime < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (!getAutorank().getStorageManager().isStorageTypeActive(StorageProvider.StorageType.DATABASE)) {
            this.registerWarningMessage("There is no active storage provider that supports global time!");
            return false;
        }

        return true;
    }
}
