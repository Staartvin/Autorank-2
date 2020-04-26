package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.GriefPreventionHook;

import java.util.UUID;

public class GriefPreventionClaimsCountRequirement extends AbstractRequirement {

    int claimsCount = -1;
    private GriefPreventionHook handler = null;

    @Override
    public String getDescription() {
        return Lang.GRIEF_PREVENTION_CLAIMS_COUNT_REQUIREMENT.getConfigValue(claimsCount);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getNumberOfClaims(uuid) + "/" + claimsCount;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isAvailable())
            return false;

        return handler.getNumberOfClaims(uuid) >= claimsCount;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.GRIEFPREVENTION);

        handler = (GriefPreventionHook) this.getDependencyManager()
                .getLibraryHook(Library.GRIEFPREVENTION);

        if (options.length > 0) {
            try {
                claimsCount = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (claimsCount < 0) {
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
        return handler.getNumberOfClaims(uuid) * 1.0d / claimsCount;
    }
}
