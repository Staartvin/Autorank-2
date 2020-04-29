package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.GriefPreventionHook;

import java.util.UUID;

public class GriefPreventionRemainingBlocksRequirement extends AbstractRequirement {

    private GriefPreventionHook handler = null;
    int remainingBlocks = -1;

    @Override
    public String getDescription() {
        return Lang.GRIEF_PREVENTION_REMAINING_BLOCKS_REQUIREMENT.getConfigValue(remainingBlocks);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getNumberOfRemainingBlocks(uuid) + "/" + remainingBlocks;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isAvailable())
            return false;

        return handler.getNumberOfRemainingBlocks(uuid) >= remainingBlocks;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.GRIEFPREVENTION);

        handler = (GriefPreventionHook) this.getDependencyManager()
                .getLibraryHook(Library.GRIEFPREVENTION);

        if (options.length > 0) {
            try {
                remainingBlocks = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (remainingBlocks < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (handler == null || !handler.isAvailable()) {
            this.registerWarningMessage("GriefPrevention is not available");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getNumberOfRemainingBlocks(uuid) * 1.0d / this.remainingBlocks;
    }
}
