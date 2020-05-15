package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.McRPGHook;

import java.util.UUID;

public class McRPGPowerLevelRequirement extends AbstractRequirement {

    int powerLevel = -1;
    private McRPGHook hook = null;

    @Override
    public String getDescription() {
        return Lang.MCRPG_POWER_LEVEL_REQUIREMENT.getConfigValue(powerLevel);
    }

    @Override
    public String getProgressString(final UUID uuid) {
        return hook.getPowerLevel(uuid) + "/" + powerLevel;
    }

    @Override
    public boolean meetsRequirement(final UUID uuid) {

        if (!hook.isHooked())
            return false;

        return hook.getPowerLevel(uuid) >= powerLevel;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.MCRPG);

        hook = (McRPGHook) this.getDependencyManager().getLibraryHook(Library.MCRPG);

        if (options.length > 0) {
            powerLevel = Integer.parseInt(options[0]);
        }

        if (powerLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (hook == null || !hook.isHooked()) {
            this.registerWarningMessage("McRPG is not available");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return hook.getPowerLevel(uuid) * 1.0d / this.powerLevel;
    }
}
