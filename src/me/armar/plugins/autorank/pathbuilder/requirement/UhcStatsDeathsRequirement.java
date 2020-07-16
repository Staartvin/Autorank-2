package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.UHCStatsHook;

import java.util.UUID;

public class UhcStatsDeathsRequirement extends AbstractRequirement {

    private UHCStatsHook handler = null;
    private int requiredDeaths = -1;

    @Override
    public String getDescription() {
        return Lang.UHC_STATS_DEATHS_REQUIREMENT.getConfigValue(requiredDeaths);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getNumberOfDeaths(uuid) + "/" + requiredDeaths;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isHooked())
            return false;

        return handler.getNumberOfDeaths(uuid) >= requiredDeaths;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.UHCSTATS);

        handler = (UHCStatsHook) this.getDependencyManager().getLibraryHook(Library.UHCSTATS).orElse(null);

        if (options.length > 0) {
            try {
                requiredDeaths = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (requiredDeaths < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return handler != null;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getNumberOfDeaths(uuid) * 1.0d / requiredDeaths;
    }
}
