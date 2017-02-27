package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.JobsHandler;

public class JobsLevelRequirement extends Requirement {

    String jobName;
    private JobsHandler jobsHandler;
    int level = -1;

    @Override
    public String getDescription() {
        
        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        }

        String lang = Lang.JOBS_LEVEL_REQUIREMENT.getConfigValue(level, jobName);

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
            points = jobsHandler.getCurrentLevel(player, jobName);
        }

        return points + "/" + level;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        double points = -1;

        if (jobsHandler == null || !jobsHandler.isAvailable()) {
            points = -1;
        } else {
            points = jobsHandler.getCurrentLevel(player, jobName);
        }

        return points >= level;
    }

    @Override
    public boolean setOptions(final String[] options) {

        jobsHandler = (JobsHandler) this.getAutorank().getDependencyManager().getDependencyHandler(Dependency.JOBS);

        level = Integer.parseInt(options[0]);

        if (options.length > 1) {
            jobName = options[1];
        }

        return level != -1 && jobName != null && jobsHandler != null;
    }
}
