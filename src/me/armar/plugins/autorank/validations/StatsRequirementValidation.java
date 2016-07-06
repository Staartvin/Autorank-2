package me.armar.plugins.autorank.validations;

import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.util.AutorankTools;

public class StatsRequirementValidation {

	private final Autorank plugin;

	public StatsRequirementValidation(final Autorank instance) {
		plugin = instance;
	}

	private boolean requiresStats(final String req) {
		final String correctName = AutorankTools.getCorrectName(req);

		if (correctName == null) {
			return false;
		}

		if (correctName.equalsIgnoreCase("blocks broken")) {
			return true;
		} else if (correctName.equalsIgnoreCase("blocks placed")) {
			return true;
		} else if (correctName.equalsIgnoreCase("votes")) {
			return true;
		} else if (correctName.equalsIgnoreCase("damage taken")) {
			return true;
		} else if (correctName.equalsIgnoreCase("mobs killed")) {
			return true;
		} else if (correctName.equalsIgnoreCase("players killed")) {
			return true;
		} else if (correctName.equalsIgnoreCase("blocks moved")) {
			return true;
		} else if (correctName.equalsIgnoreCase("fish caught")) {
			return true;
		} else if (correctName.equalsIgnoreCase("items crafted")) {
			return true;
		} else if (correctName.equalsIgnoreCase("times sheared")) {
			return true;
		} else if (correctName.equalsIgnoreCase("food eaten")) {
			return true;
		} else
			return false;
	}

	/**
	 * Checks if there are Stats required requirements and if Stats is required.
	 * When Stats required requirements are found but Stats is not, it will warn
	 * and return false.
	 * 
	 * @return true if everything is okay, false otherwise.
	 */
	public boolean validateRequirements(final SimpleYamlConfiguration config) {
		if (config == null)
			return false;

		// Simple config does not have stats required requirements.
		if (!config.getBoolean("use advanced config")) {
			return true;
		}
		for (final String group : plugin.getConfigHandler().getRanks()) {
			final Set<String> reqs = plugin.getConfigHandler().getRequirements(group);

			for (final String req : reqs) {
				if (requiresStats(req)) {
					if (plugin.getHookedStatsPlugin() == null || !plugin.getHookedStatsPlugin().isEnabled()) {
						plugin.getWarningManager().registerWarning(
								"You need to install Stats because you have Stats-required requirements listed in your config.",
								5);
						return false;
					}
				}
			}

		}

		return true;
	}
}
