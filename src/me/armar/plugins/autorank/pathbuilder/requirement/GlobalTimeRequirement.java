package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
        return Lang.GLOBAL_TIME_REQUIREMENT.getConfigValue(AutorankTools.timeToString(globalTime, TimeUnit.MINUTES));
    }

    @Override
    public String getProgressString(UUID uuid) {

        int playTime = 0;
        try {
            playTime = getAutorank().getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return AutorankTools.timeToString(playTime, TimeUnit.MINUTES) + "/" + AutorankTools.timeToString(globalTime,
                TimeUnit.MINUTES);
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
    public boolean initRequirement(final String[] options) {

        globalTime = AutorankTools.stringToTime(options[0], TimeUnit.MINUTES);

        if (globalTime < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (!getAutorank().getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            this.registerWarningMessage("There is no active storage provider that supports global time!");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        int playTime = 0;
        try {
            playTime = getAutorank().getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return playTime * 1.0d / globalTime;
    }
}
