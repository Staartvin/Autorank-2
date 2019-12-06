package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.BattleLevelsHook;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BattleLevelsTopKillStreakRequirement extends AbstractRequirement {

    private BattleLevelsHook handler = null;
    private double neededTopKillstreak = -1.0;

    @Override
    public String getDescription() {

        return Lang.BATTLELEVELS_TOP_KILLSTREAK_REQUIREMENT.getConfigValue(neededTopKillstreak);
    }

    @Override
    public String getProgress(final Player player) {
        return handler.getTopKillStreak(player.getUniqueId()) + "/" + neededTopKillstreak;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isAvailable())
            return false;

        return handler.getTopKillStreak(uuid) >= neededTopKillstreak;
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.BATTLELEVELS);

        handler = (BattleLevelsHook) this.getDependencyManager().getLibraryHook(Library.BATTLELEVELS);

        if (options.length > 0) {
            try {
                neededTopKillstreak = Double.parseDouble(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (neededTopKillstreak < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }
}
