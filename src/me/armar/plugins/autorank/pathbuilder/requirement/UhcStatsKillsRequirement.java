package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.UHCStatsHook;

import java.util.UUID;

public class UhcStatsKillsRequirement extends AbstractRequirement {

    private UHCStatsHook handler = null;
    private int requiredKills = -1;

    @Override
    public String getDescription() {
        return Lang.UHC_STATS_KILLS_REQUIREMENT.getConfigValue(requiredKills);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getNumberOfKills(uuid) + "/" + requiredKills;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isAvailable())
            return false;

        return handler.getNumberOfKills(uuid) >= requiredKills;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.UHCSTATS);

        handler = (UHCStatsHook) this.getDependencyManager().getLibraryHook(Library.UHCSTATS);

        if (options.length > 0) {
            try {
                requiredKills = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (requiredKills < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getNumberOfKills(uuid) * 1.0d / requiredKills;
    }
}
