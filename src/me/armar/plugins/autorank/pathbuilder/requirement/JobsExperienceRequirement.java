package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.JobsHandler;

public class JobsExperienceRequirement extends Requirement {

    int experience = -1;
    String jobName;
    private JobsHandler jobsHandler;

    @Override
    public String getDescription() {

        String lang = Lang.JOBS_EXPERIENCE_REQUIREMENT.getConfigValue(experience, jobName);

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
            points = jobsHandler.getCurrentXP(player, jobName);
        }

        return points + "/" + experience;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        double points = -1;

        if (jobsHandler == null || !jobsHandler.isAvailable()) {
            points = -1;
        } else {
            points = jobsHandler.getCurrentXP(player, jobName);
        }

        return points >= experience;
    }

    @Override
    public boolean setOptions(final String[] options) {

        jobsHandler = (JobsHandler) this.getAutorank().getDependencyManager().getDependencyHandler(Dependency.JOBS);

        experience = Integer.parseInt(options[0]);

        if (options.length > 1) {
            jobName = options[1];
        }

        return experience != -1 && jobName != null && jobsHandler != null;
    }
}
