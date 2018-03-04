package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.McMMOHook;
import org.bukkit.entity.Player;

public class McMMOPowerLevelAbstractRequirement extends AbstractRequirement {

    private McMMOHook handler = null;
    int powerLevel = -1;

    @Override
    public String getDescription() {
        return Lang.MCMMO_POWER_LEVEL_REQUIREMENT.getConfigValue(powerLevel + "");
    }

    @Override
    public String getProgress(final Player player) {
        final int level = handler.getPowerLevel(player);

        return level + "/" + powerLevel;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isAvailable())
            return false;

        final int level = handler.getPowerLevel(player);

        return level >= powerLevel;
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.MCMMO);

        handler = (McMMOHook) this.getDependencyManager().getLibraryHook(Library.MCMMO);

        if (options.length > 0) {
            powerLevel = Integer.parseInt(options[0]);
        }

        if (powerLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (handler == null || !handler.isAvailable()) {
            this.registerWarningMessage("mcMMO is not available");
            return false;
        }

        return true;
    }
}
