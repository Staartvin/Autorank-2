package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.JobsHandler;

public class JobsCurrentPointsRequirement extends Requirement {

	int currentPoints = -1;

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
			points = jobsHandler.getCurrentPoints(player.getUniqueId());
		}

		return points + "/" + currentPoints;
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
			points = jobsHandler.getCurrentPoints(player.getUniqueId());
		}

		return points >= currentPoints;
	}

	@Override
	public boolean setOptions(final String[] options) {

		currentPoints = Integer.parseInt(options[0]);

		return currentPoints != -1;
	}
}
