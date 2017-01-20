package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.JobsHandler;

public class JobsTotalPointsRequirement extends Requirement {

    private JobsHandler jobsHandler;
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
    public boolean meetsRequirement(final Player player) {

        double points = -1;

        if (jobsHandler == null || !jobsHandler.isAvailable()) {
            points = -1;
        } else {
            points = jobsHandler.getTotalPoints(player.getUniqueId());
        }

        return points >= totalPoints;
    }

    @Override
    public boolean setOptions(final String[] options) {

        jobsHandler = (JobsHandler) this.getAutorank().getDependencyManager().getDependencyHandler(Dependency.JOBS);

        totalPoints = Integer.parseInt(options[0]);

        return totalPoints != -1 && jobsHandler != null;
    }
}
