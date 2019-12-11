package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.BattleLevelsHook;

import java.util.UUID;

public class BattleLevelsKillStreakRequirement extends AbstractRequirement {

    private BattleLevelsHook handler = null;
    private double neededKillstreak = -1.0;

    @Override
    public String getDescription() {
        return Lang.BATTLELEVELS_NORMAL_KILLSTREAK_REQUIREMENT.getConfigValue(neededKillstreak);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getKillStreak(uuid) + "/" + neededKillstreak;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isAvailable())
            return false;

        return handler.getKillStreak(uuid) >= neededKillstreak;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.BATTLELEVELS);

        handler = (BattleLevelsHook) this.getDependencyManager().getLibraryHook(Library.BATTLELEVELS);

        if (options.length > 0) {
            try {
                neededKillstreak = Double.parseDouble(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (neededKillstreak < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return false;
    }
}
