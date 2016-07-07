package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.JobsHandler;

public class JobsLevelRequirement extends Requirement {

	int level = -1;
	String jobName;

	@Override
	public String getDescription() {

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

		StatzAPIHandler statz = (StatzAPIHandler) this.getAutorank().getDependencyManager()
				.getDependency(dependency.STATZ);

		if (statz == null || !statz.isAvailable()) {
			points = -1;
		}

		JobsHandler jobsHandler = (JobsHandler) statz.getDependencyHandler(Dependency.JOBS);

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

		StatzAPIHandler statz = (StatzAPIHandler) this.getAutorank().getDependencyManager()
				.getDependency(dependency.STATZ);

		if (statz == null || !statz.isAvailable()) {
			points = -1;
		}

		JobsHandler jobsHandler = (JobsHandler) statz.getDependencyHandler(Dependency.JOBS);

		if (jobsHandler == null || !jobsHandler.isAvailable()) {
			points = -1;
		} else {
			points = jobsHandler.getCurrentLevel(player, jobName);
		}

		return points >= level;
	}

	@Override
	public boolean setOptions(final String[] options) {

		level = Integer.parseInt(options[0]);

		if (options.length > 1) {
			jobName = options[1];
		}

		return level != -1 && jobName != null;
	}
}
