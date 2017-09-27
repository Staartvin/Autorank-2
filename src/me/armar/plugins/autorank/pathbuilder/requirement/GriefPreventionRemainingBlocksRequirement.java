package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.GriefPreventionHook;
import org.bukkit.entity.Player;

public class GriefPreventionRemainingBlocksRequirement extends Requirement {

    private GriefPreventionHook handler = null;
    int remainingBlocks = -1;

    @Override
    public String getDescription() {
        return Lang.GRIEF_PREVENTION_REMAINING_BLOCKS_REQUIREMENT.getConfigValue(remainingBlocks);
    }

    @Override
    public String getProgress(final Player player) {
        final int level = handler.getNumberOfRemainingBlocks(player.getUniqueId());

        return level + "/" + remainingBlocks;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isAvailable())
            return false;

        final int level = handler.getNumberOfRemainingBlocks(player.getUniqueId());

        return level >= remainingBlocks;
    }

    @Override
    public boolean setOptions(final String[] options) {

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
}
