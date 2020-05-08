package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.BattleLevelsHook;

import java.util.UUID;

public class BattleLevelsScoreRequirement extends AbstractRequirement {

    private BattleLevelsHook handler = null;
    private double neededScore = -1.0;

    @Override
    public String getDescription() {

        return Lang.BATTLELEVELS_SCORE_REQUIREMENT.getConfigValue(neededScore);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getScore(uuid) + "/" + neededScore;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isHooked())
            return false;

        return handler.getScore(uuid) >= neededScore;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.BATTLELEVELS);

        handler = (BattleLevelsHook) this.getDependencyManager().getLibraryHook(Library.BATTLELEVELS);

        if (options.length > 0) {
            try {
                neededScore = Double.parseDouble(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (neededScore < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getScore(uuid) * 1.0d / neededScore;
    }
}
