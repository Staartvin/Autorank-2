package me.armar.plugins.autorank.rankbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.ConfigHandler;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.requirement.TimeRequirement;
import me.armar.plugins.autorank.playerchecker.result.MessageResult;
import me.armar.plugins.autorank.playerchecker.result.RankChangeResult;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.rankbuilder.builders.RequirementBuilder;
import me.armar.plugins.autorank.rankbuilder.builders.ResultBuilder;
import me.armar.plugins.autorank.rankbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * This class builds all the groups changes with their parent group. <br>
 * It keeps track of all the groups that have a change group and keeps track of
 * the change group's requirements and results. <br>
 * This allows multiple one group to have multiple change paths. (multiple
 * changes).
 * <p>
 * Date created: 14:20:20 5 aug. 2015
 * 
 * @author Staartvin
 * 
 */
public class ChangeGroupBuilder {

	private final Autorank plugin;

	private RequirementBuilder requirementBuilder;
	private ResultBuilder resultBuilder;

	public ChangeGroupBuilder(final Autorank plugin) {
		this.plugin = plugin;
		setResultBuilder(new ResultBuilder());
		setRequirementBuilder(new RequirementBuilder());
	}

	public HashMap<String, List<ChangeGroup>> initialiseChangeGroups(final boolean simpleConfigUsed,
			final SimpleYamlConfiguration config, final HashMap<String, List<ChangeGroup>> changeGroups) {
		if (simpleConfigUsed) {
			return initSimpleConfig(config, changeGroups);
		} else {
			return initAdvancedConfig(config, changeGroups);
		}
	}

	private HashMap<String, List<ChangeGroup>> initSimpleConfig(final SimpleYamlConfiguration config,
			final HashMap<String, List<ChangeGroup>> changeGroups) {

		final Set<String> ranks = config.getKeys(false);

		final Iterator<String> it = ranks.iterator();

		while (it.hasNext()) {

			final String rank = it.next();
			final String value = (String) config.get(rank);

			final String[] options = value.split(" after ");

			String rankName;

			if (rank.contains("-copy-")) {
				// Rank belongs to another rank

				final int pointer = rank.indexOf("-copy-");

				final String subString = rank.substring(0, pointer);

				rankName = subString;
			} else {
				rankName = rank;
			}

			// Shut down to prevent any issues from happening.
			if (options.length <= 0) {
				System.out.print("[AutoRank] Simple Config is not configured correctly!");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return null;
			}

			// Time requirement

			final RequirementsHolder holder = new RequirementsHolder(plugin);

			final Requirement timeReq = new TimeRequirement();
			timeReq.setOptions(new String[] { options[1] });
			timeReq.setOptional(false);
			timeReq.setResults(new ArrayList<Result>());
			timeReq.setAutoComplete(true);
			timeReq.setReqId(0);
			timeReq.setAutorank(plugin);
			holder.addRequirement(timeReq);

			// Change the rank
			final List<Result> res = new ArrayList<Result>();
			final Result change = new RankChangeResult();
			change.setOptions(new String[] { rankName, options[0] });
			change.setAutorank(plugin);
			res.add(change);

			// Change the message
			final Result message = new MessageResult();
			message.setOptions(new String[] { "&2You got ranked to " + options[0] });
			message.setAutorank(plugin);
			res.add(message);

			// The ChangeGroups that are already with this group
			List<ChangeGroup> currentChanges = null;

			if (changeGroups.containsKey(rankName)) {
				currentChanges = changeGroups.get(rankName);
			} else {
				currentChanges = new ArrayList<ChangeGroup>();
			}

			// ChangeGroup for this rank
			final ChangeGroup changeGroup = new ChangeGroup(plugin);

			// Save the RequirementsHolder
			changeGroup.addRequirementHolder(holder);

			// Save the results
			changeGroup.setResults(res);

			changeGroup.setParentGroup(rankName);
			changeGroup.setInternalGroup(rank);
			changeGroup.setDisplayName(plugin.getConfigHandler().getDisplayName(rank));

			currentChanges.add(changeGroup);

			// Save the current changegroup to the parent group
			changeGroups.put(rankName, currentChanges);
		}

		return changeGroups;
	}

	private HashMap<String, List<ChangeGroup>> initAdvancedConfig(final SimpleYamlConfiguration config,
			final HashMap<String, List<ChangeGroup>> changeGroups) {

		final ConfigHandler configHandler = plugin.getConfigHandler();

		for (final String group : configHandler.getRanks()) {

			String groupName;

			if (group.contains("-copy-")) {
				// Rank belongs to another rank

				final int pointer = group.indexOf("-copy-");

				final String subString = group.substring(0, pointer);

				groupName = subString;
			} else {
				groupName = group;
			}

			// final List<Requirement> req = new ArrayList<Requirement>();
			final List<Result> res = new ArrayList<Result>();
			final List<RequirementsHolder> holders = new ArrayList<RequirementsHolder>();

			for (final String requirement : configHandler.getRequirements(group)) {

				// Implement optional option logic
				final boolean optional = configHandler.isOptional(requirement, group);
				// Result for requirement
				final List<String> results = configHandler.getResultsOfRequirement(requirement, group);

				// Create a new result List that will get all result when a
				// requirement is met.
				final List<Result> realResults = new ArrayList<Result>();

				for (final String resultString : results) {
					realResults.add(createResult(resultString,
							configHandler.getResultOfRequirement(requirement, group, resultString)));
				}
				final int reqId = configHandler.getReqId(requirement, group);

				if (reqId < 0) {
					try {
						throw new Exception("REQ ID COULDN'T BE FOUND! REPORT TO AUTHOR!" + " GROUP: " + group
								+ ", REQUIREMENT: " + requirement);
					} catch (final Exception e) {
						// TODO Auto-generated catch block
						plugin.getLogger().severe(e.getCause().getMessage());
						return null;
					}
				}

				final String correctName = AutorankTools.getCorrectName(requirement);

				if (correctName == null) {
					throw new IllegalArgumentException(
							"Requirement '" + requirement + "' of group '" + group + "' is unknown!");
				}

				final RequirementsHolder holder = new RequirementsHolder(plugin);

				// Option strings seperated
				final List<String[]> optionsList = configHandler.getOptions(requirement, group);

				for (final String[] options : optionsList) {
					final Requirement newRequirement = createRequirement(correctName, options, optional, realResults,
							configHandler.useAutoCompletion(group, requirement), reqId, requirement, group);

					if (newRequirement == null)
						continue;

					// Make requirement world-specific if a world was specified
					// in
					// the config.
					if (plugin.getConfigHandler().isRequirementWorldSpecific(requirement, group)) {
						newRequirement.setWorld(plugin.getConfigHandler().getWorldOfRequirement(requirement, group));
					}

					// Set whether this requirement will derank a player if it
					// is not met.
					if (plugin.getConfigHandler().isRequirementDerankable(group, requirement)) {
						holder.setDerankable(true);
					}

					holder.addRequirement(newRequirement);
				}

				holders.add(holder);
			}

			for (final String resu : configHandler.getResults(group)) {
				final Result result = createResult(resu, configHandler.getResult(resu, group));

				if (result == null)
					continue;

				res.add(result);
			}

			// ChangeGroup for this rank
			final ChangeGroup changeGroup = new ChangeGroup(plugin);

			// Save the RequirementsHolders
			changeGroup.setRequirementHolders(holders);

			// Save the results
			changeGroup.setResults(res);

			changeGroup.setParentGroup(groupName);
			changeGroup.setInternalGroup(group);
			changeGroup.setDisplayName(plugin.getConfigHandler().getDisplayName(group));

			if (configHandler.getRankChange(group) != null) {
				final String[] rankChange = configHandler.getRankChange(group).split(";");

				if (rankChange.length <= 0) {
					plugin.getWarningManager().registerWarning("Rank change of " + group + " is invalid!", 10);
					return null;
				}
			}

			// The ChangeGroups that are already with this group
			List<ChangeGroup> currentChanges = null;

			if (changeGroups.containsKey(groupName)) {
				currentChanges = changeGroups.get(groupName);
			} else {
				currentChanges = new ArrayList<ChangeGroup>();
			}

			currentChanges.add(changeGroup);

			// Save the current changegroup to the parent group
			changeGroups.put(groupName, currentChanges);
		}

		// Find previous ranks for all change groups to establish hierarchy
		for (final String group : changeGroups.keySet()) {
			if (configHandler.getRankChange(group) != null) {
				final String[] rankChange = configHandler.getRankChange(group).split(";");

				String rankChangeName = null;

				if (rankChange.length == 1) {
					rankChangeName = rankChange[0].trim();
				} else {
					rankChangeName = rankChange[1].trim();
				}

				// Set next rank up group
				for (final ChangeGroup changeGroup : changeGroups.get(group)) {
					changeGroup.setNextGroup(rankChangeName);
				}

				if (changeGroups.get(rankChangeName) != null) {
					for (final ChangeGroup changeGroup : changeGroups.get(rankChangeName)) {
						if (changeGroup == null)
							continue;

						changeGroup.setPreviousGroup(group);
					}
				}
			}
		}

		return changeGroups;
	}

	private Result createResult(final String type, final String object) {
		final Result res = resultBuilder.create(type);

		if (res != null) {
			res.setAutorank(plugin);
			res.setOptions(object.split(";"));
		}

		return res;
	}

	private Requirement createRequirement(final String type, final String[] options, final boolean optional,
			final List<Result> results, final boolean autoComplete, final int reqId, String originalName,
			String originalGroup) {
		final Requirement res = requirementBuilder.create(type);

		if (res != null) {
			res.setAutorank(plugin);

			final String errorMessage = "Could not set up requirement '" + originalName + "' of " + originalGroup
					+ "! It's invalid: check the wiki for documentation.";

			try {
				if (!res.setOptions(options)) {
					plugin.getLogger().severe(errorMessage);
					plugin.getWarningManager().registerWarning(errorMessage, 10);
				}
			} catch (final Exception e) {
				plugin.getLogger().severe(errorMessage);
				plugin.getWarningManager().registerWarning(errorMessage, 10);
			}

			res.setOptional(optional);
			res.setAutoComplete(autoComplete);
			res.setReqId(reqId);
			res.setResults(results);
		}
		return res;
	}

	public RequirementBuilder getRequirementBuilder() {
		return requirementBuilder;
	}

	public void setRequirementBuilder(final RequirementBuilder requirementBuilder) {
		this.requirementBuilder = requirementBuilder;
	}

	public ResultBuilder getResultBuilder() {
		return resultBuilder;
	}

	public void setResultBuilder(final ResultBuilder resultBuilder) {
		this.resultBuilder = resultBuilder;
	}

}
