package me.armar.plugins.autorank.validations;

import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

import org.bukkit.configuration.ConfigurationSection;

public class PermissionGroupValidation {

	private final Autorank autorank;

	public PermissionGroupValidation(final Autorank instance) {
		this.autorank = instance;
	}

	/**
	 * This will check if the groups defined in the advanced config are correct.
	 * 
	 * @param config Advanced Config
	 * @return true if correct; false otherwise
	 */
	public boolean validateAdvancedGroups(final SimpleYamlConfiguration config) {

		if (config == null)
			return false;

		boolean isMissing = false;
		final String[] groups = autorank.getPermPlugHandler()
				.getPermissionPlugin().getGroups();
		Set<String> ranks;
		
		final ConfigurationSection section = config
				.getConfigurationSection("ranks");
		ranks = section.getKeys(false);

		for (final String rank : ranks) {
			for (int i = 0; i < groups.length; i++) {
				final String group = groups[i];
				if (rank.equals(group)) {
					break;
				}


				if (rank.equalsIgnoreCase(group)) {
					// Do not log but register warning
					autorank.getWarningManager().registerWarning(
							"Permissions group '" + rank + "' should be '"
									+ group + "'", 10);
					isMissing = true;
					break;
				}
				
				if ((i == (groups.length - 1))) {
					// If this is the last group and is not equal to the rank defined in the config:
					autorank.getWarningManager().registerWarning(
							"Permissions group is not defined in permissions file: " + rank, 10);
					isMissing = true;
				}
			}
			
			

			if (!isValidChange(rank)) {
				//autorank.getWarningManager().registerWarning("Rank change of rank '" + rank + "' is invalid. (Do the groups used exist?)", 10);
				isMissing = true;
			}
		}

		// If all is okay, then do nothing. Else, disable AR.
		return (!isMissing);
	}

	/**
	 * Checks whether the @group variable is the same as rankFrom group.
	 * It also checks whether the rankTo group is defined as a group in the
	 * permission plugin
	 * 
	 * @param group
	 * @return true if (rankFrom.equals(group) && rankTo is defined in the
	 *         config); false otherwise
	 */
	public boolean isValidChange(final String group) {
		final String rankChange = autorank.getConfigHandler().getRankChange(group);
		final String[] groups = autorank.getPermPlugHandler()
				.getPermissionPlugin().getGroups();

		if (rankChange == null)
			return true;

		if (rankChange.trim().equals(""))
			return false;

		if (!rankChange.contains(";")) {
			boolean isMissing = true;

			for (final String group1 : groups) {
				if (group1.equals(rankChange.trim())) {
					isMissing = false;
				}
			}

			return !isMissing;
		}

		final String[] array = rankChange.split(";");

		String rankFrom = null, rankTo = null;

		if (array.length >= 2) {
			rankFrom = array[0].trim();
			rankTo = array[1].trim();
		}

		if (rankTo == null) {
			autorank.getWarningManager()
					.registerWarning(
							"Rank change of rank '"
									+ group
									+ "' is invalid. There is no rank given to promote to!",
							10);
			return false;
		}

		if (rankFrom == null) {
			autorank.getWarningManager()
					.registerWarning(
							"Rank change of rank '"
									+ group
									+ "' is invalid. There is no rank given to promote from!",
							10);
			return false;
		}

		boolean isMissingRankTo = true, isMissingRankFrom = true;

		// Check whether the rankTo exists
		for (final String group1 : groups) {
			if (group1 == null) continue;
			
			if (group1.equals(rankTo.trim())) {
				isMissingRankTo = false;
			}
		}

		// Check whether the rankFrom exists
		for (final String group1 : groups) {
			if (group1 == null) continue;
			
			if (group1.equals(rankFrom.trim())) {
				isMissingRankFrom = false;
			}
		}

		if (isMissingRankTo) {
			autorank.getWarningManager()
					.registerWarning(
							"Rank change of rank '"
									+ group
									+ "' is invalid. The rank to promote to doesn't exist in the perm file.",
							10);
			return false;
		}

		if (isMissingRankFrom) {
			autorank.getWarningManager()
					.registerWarning(
							"Rank change of rank '"
									+ group
									+ "' is invalid. The rank to promote from doesn't exist in the perm file.",
							10);
			return false;
		}

		return (rankFrom.equals(group) && !isMissingRankTo && !isMissingRankFrom);
	}

	/**
	 * Validates whether the groups in the Simple Config are correct.
	 * 
	 * @param config SimpleConfig
	 * @return true if they are valid; false otherwise.
	 */
	public boolean validateSimpleGroups(final SimpleYamlConfiguration config) {
		if (config == null)
			return false;

		// is any group missing
		boolean isMissing = false;

		final String[] groups = autorank.getPermPlugHandler()
				.getPermissionPlugin().getGroups();
		final Set<String> ranks = config.getKeys(false);

		for (final String rank : ranks) {
			for (int i = 0; i < groups.length; i++) {
				final String group = groups[i];

				// found matching group
				if (rank.equals(group))
					break;

				// found almost matching group
				if (rank.equalsIgnoreCase(group)) {
					// Do not log but register warning
					autorank.getWarningManager().registerWarning(
							"Permissions group '" + rank + "' should be '"
									+ group + "'", 10);
					isMissing = true;
					break;
				}

				// If this is the last group and is not equal to the rank defined in the config:
				if ((i == (groups.length - 1)) && !rank.equals(group)) {
					autorank.getWarningManager().registerWarning(
							"Permissions group is not defined: '" + rank + "'",
							10);
					isMissing = true;
				}
			}

			// Here we check whether the value of rank is good.

			// Value of the group in the config
			final String value = config.getString(rank);

			final String[] temp = value.split(" ");

			if (temp.length < 3) {
				autorank.getWarningManager()
						.registerWarning(
								"Rank line of rank '"
										+ rank
										+ "' is invalid. Take a look at the examples in the config!",
								10);
				isMissing = true;
			}

			final String rankTo = temp[0];

			boolean isMissingRankTo = true;

			for (final String group : groups) {
				if (group.equals(rankTo)) {
					// not missing group
					isMissingRankTo = false;
				}
			}

			if (isMissingRankTo) {
				autorank.getWarningManager().registerWarning(
						"Rank line of rank '" + rank + "' is invalid. Group '"
								+ rankTo + "' doesn't exist!", 10);
				return false;
			}

		}

		return !isMissing;

	}
}
