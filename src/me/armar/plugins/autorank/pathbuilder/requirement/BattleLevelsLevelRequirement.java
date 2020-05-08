package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.BattleLevelsHook;

import java.util.UUID;

public class BattleLevelsLevelRequirement extends AbstractRequirement {

    private BattleLevelsHook handler = null;
    private int neededLevel = -1;

    @Override
    public String getDescription() {

        return Lang.BATTLELEVELS_LEVEL_REQUIREMENT.getConfigValue(neededLevel);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getLevel(uuid) + "/" + neededLevel;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isHooked())
            return false;

        return handler.getLevel(uuid) >= neededLevel;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.BATTLELEVELS);

        handler = (BattleLevelsHook) this.getDependencyManager().getLibraryHook(Library.BATTLELEVELS);

        if (options.length > 0) {
            try {
                neededLevel = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (neededLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getLevel(uuid) * 1.0d / neededLevel;
    }
}
