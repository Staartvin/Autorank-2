package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.JobsHook;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JobsTotalPointsRequirement extends AbstractRequirement {

    private JobsHook jobsHandler;
    int totalPoints = -1;

    @Override
    public String getDescription() {

        String lang = Lang.JOBS_TOTAL_POINTS_REQUIREMENT.getConfigValue(totalPoints);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {

        double points = -1;

        if (jobsHandler == null || !jobsHandler.isAvailable()) {
            points = -1;
        } else {
            points = jobsHandler.getTotalPoints(player.getUniqueId());
        }

        return points + "/" + totalPoints;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        // Add dependency
        addDependency(Library.JOBS);

        double points = -1;

        if (jobsHandler == null || !jobsHandler.isAvailable()) {
            points = -1;
        } else {
            points = jobsHandler.getTotalPoints(uuid);
        }

        return points >= totalPoints;
    }

    @Override
    public boolean setOptions(final String[] options) {

        jobsHandler = (JobsHook) this.getAutorank().getDependencyManager().getLibraryHook(Library.JOBS);

        try {
            totalPoints = Integer.parseInt(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (totalPoints < 0) {
            this.registerWarningMessage("No level is provided or smaller than 0.");
            return false;
        }

        if (jobsHandler == null || !jobsHandler.isAvailable()) {
            this.registerWarningMessage("Jobs is not available");
            return false;
        }

        return true;
    }


}
