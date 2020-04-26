package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.JobsHook;

import java.util.UUID;

public class JobsCurrentPointsRequirement extends AbstractRequirement {

    int currentPoints = -1;
    private JobsHook jobsHandler;

    @Override
    public String getDescription() {

        String lang = Lang.JOBS_CURRENT_POINTS_REQUIREMENT.getConfigValue(currentPoints);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {

        double points = -1;

        if (jobsHandler == null || !jobsHandler.isAvailable()) {
            points = -1;
        } else {
            points = jobsHandler.getCurrentPoints(uuid);
        }

        return points + "/" + currentPoints;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        double points = -1;

        if (jobsHandler == null || !jobsHandler.isAvailable()) {
            points = -1;
        } else {
            points = jobsHandler.getCurrentPoints(uuid);
        }

        return points >= currentPoints;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.JOBS);

        jobsHandler = (JobsHook) this.getAutorank().getDependencyManager().getLibraryHook(Library.JOBS);

        try {
            currentPoints = Integer.parseInt(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (currentPoints < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (jobsHandler == null || !jobsHandler.isAvailable()) {
            this.registerWarningMessage("Jobs is not available");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        double points = 0;

        if (jobsHandler != null && jobsHandler.isAvailable()) {
            points = jobsHandler.getCurrentPoints(uuid);
        }

        return points / this.currentPoints;
    }
}
