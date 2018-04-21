package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.BattleLevelsHook;
import org.bukkit.entity.Player;

public class BattleLevelsLevelRequirement extends AbstractRequirement {

    private BattleLevelsHook handler = null;
    private int neededLevel = -1;

    @Override
    public String getDescription() {

        return Lang.BATTLELEVELS_LEVEL_REQUIREMENT.getConfigValue(neededLevel);
    }

    @Override
    public String getProgress(final Player player) {

        int level = 0;

        level = handler.getLevel(player.getUniqueId());

        return level + "/" + neededLevel;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isAvailable())
            return false;

        return handler.getLevel(player.getUniqueId()) >= neededLevel;
    }

    @Override
    public boolean setOptions(final String[] options) {

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
}
