package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.GriefPreventionHook;
import org.bukkit.entity.Player;

public class GriefPreventionBonusBlocksRequirement extends AbstractRequirement {

    int bonusBlocks = -1;
    private GriefPreventionHook handler = null;

    @Override
    public String getDescription() {
        return Lang.GRIEF_PREVENTION_BONUS_BLOCKS_REQUIREMENT.getConfigValue(bonusBlocks);
    }

    @Override
    public String getProgress(final Player player) {
        final int level = handler.getNumberOfBonusBlocks(player.getUniqueId());

        return level + "/" + bonusBlocks;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isAvailable())
            return false;

        final int level = handler.getNumberOfBonusBlocks(player.getUniqueId());

        return level >= bonusBlocks;
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.GRIEFPREVENTION);

        handler = (GriefPreventionHook) this.getDependencyManager()
                .getLibraryHook(Library.GRIEFPREVENTION);

        if (options.length > 0) {
            try {
                bonusBlocks = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (bonusBlocks < 0) {
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
