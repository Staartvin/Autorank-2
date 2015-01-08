package me.armar.plugins.autorank.playerchecker.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.ConfigHandler;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.requirement.TimeRequirement;
import me.armar.plugins.autorank.playerchecker.result.MessageResult;
import me.armar.plugins.autorank.playerchecker.result.RankChangeResult;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.util.AutorankTools;

public class RankChangeBuilder {

	private final Autorank autorank;
	private RequirementBuilder requirementBuilder;
	private ResultBuilder resultBuilder;

	public RankChangeBuilder(final Autorank autorank) {
		this.autorank = autorank;
		setResultBuilder(new ResultBuilder());
		setRequirementBuilder(new RequirementBuilder());
	}

	public List<RankChange> createFromAdvancedConfig(
			final SimpleYamlConfiguration config) {
		final List<RankChange> result = new ArrayList<RankChange>();
		final ConfigHandler configHandler = autorank.getConfigHandler();

		//ConfigurationSection section = config.getConfigurationSection("ranks");
		for (final String group : configHandler.getRanks()) {

			final List<Requirement> req = new ArrayList<Requirement>();
			final List<Result> res = new ArrayList<Result>();

			for (final String requirement : configHandler
					.getRequirements(group)) {
				// Implement optional option logic
				final boolean optional = configHandler.isOptional(requirement,
						group);
				// Result for requirement
				final List<String> results = configHandler
						.getResultsOfRequirement(requirement, group);

				// Create a new result List that will get all result when a requirement is met.
				final List<Result> realResults = new ArrayList<Result>();

				for (final String resultString : results) {
					realResults.add(createResult(resultString, configHandler
							.getResultOfRequirement(requirement, group,
									resultString)));
				}
				final int reqId = configHandler.getReqId(requirement, group);

				//System.out.print("REQ ID of " + requirement + " for group " + group + ": " + reqId);

				if (reqId < 0) {
					try {
						throw new Exception(
								"REQ ID COULDN'T BE FOUND! REPORT TO AUTHOR!"
										+ " GROUP: " + group
										+ ", REQUIREMENT: " + requirement);
					} catch (final Exception e) {
						// TODO Auto-generated catch block
						autorank.getLogger().severe(e.getCause().getMessage());
						return result;
					}
				}

				final String correctName = AutorankTools
						.getCorrectName(requirement);

				if (correctName == null) {
					throw new IllegalArgumentException("Requirement '"
							+ requirement + "' of group '" + group
							+ "' is unknown!");
				}

				req.add(createRequirement(
						AutorankTools.getCorrectName(requirement),
						configHandler.getRequirement(requirement, group),
						optional, realResults,
						configHandler.useAutoCompletion(group, requirement),
						reqId));

			}

			for (final String resu : configHandler.getResults(group)) {
				res.add(createResult(resu, configHandler.getResult(resu, group)));
			}

			String rankTo = null;

			if (configHandler.getRankChange(group) != null) {
				final String[] rankChange = configHandler.getRankChange(group)
						.split(";");

				if (rankChange.length <= 0) {
					autorank.getWarningManager().registerWarning(
							"Rank change of " + group + " is invalid!", 10);
					return null;
				}

				if (rankChange.length == 1) {
					rankTo = rankChange[0].trim();
				} else {
					rankTo = rankChange[1].trim();
				}
			}

			result.add(new RankChange(autorank, group, rankTo, req, res));
		}

		return result;
	}

	public List<RankChange> createFromSimpleConfig(
			final SimpleYamlConfiguration config) {
		// TODO logging errors and not making faulty RankChanges
		final List<RankChange> result = new ArrayList<RankChange>();

		final Set<String> ranks = config.getKeys(false);
		final Iterator<String> it = ranks.iterator();

		while (it.hasNext()) {
			final String rank = it.next();
			final String value = (String) config.get(rank);

			final String[] options = value.split(" after ");

			if (options.length <= 0) {
				System.out
						.print("[AutoRank] Simple Config is not configured correctly!");
				autorank.getServer().getPluginManager().disablePlugin(autorank);
				return null;
			}

			// Time requirement
			final List<Requirement> req = new ArrayList<Requirement>();
			final Requirement timeReq = new TimeRequirement();
			timeReq.setOptions(new String[] { options[1] });
			timeReq.setOptional(false);
			timeReq.setResults(new ArrayList<Result>());
			timeReq.setAutoComplete(true);
			timeReq.setReqId(0);
			timeReq.setAutorank(autorank);
			req.add(timeReq);

			// Change the rank
			final List<Result> res = new ArrayList<Result>();
			final Result change = new RankChangeResult();
			change.setOptions(new String[] { rank, options[0] });
			change.setAutorank(autorank);
			res.add(change);

			// Change the message
			final Result message = new MessageResult();
			message.setOptions(new String[] { "&2You got ranked to "
					+ options[0] });
			message.setAutorank(autorank);
			res.add(message);

			result.add(new RankChange(autorank, rank, options[0], req, res));
		}

		return result;
	}

	private Requirement createRequirement(final String type, final String arg,
			final boolean optional, final List<Result> results,
			final boolean autoComplete, final int reqId) {
		final Requirement res = requirementBuilder.create(type);

		if (res != null) {
			res.setAutorank(autorank);
			
			// Check if setOptions is valid
			try {
				if (!res.setOptions(arg.split(";"))) {
					autorank.getLogger().severe("Could not setup requirement '" + type + "'! It's invalid: check the wiki for documentation.");
				}
			} catch (Exception e) {
				autorank.getLogger().severe("Could not setup requirement '" + type + "'! It's invalid: check the wiki for documentation.");
			}

			res.setOptional(optional);
			res.setAutoComplete(autoComplete);
			res.setReqId(reqId);
			res.setResults(results);
		}
		return res;
	}

	private Result createResult(final String type, final String object) {
		final Result res = resultBuilder.create(type);

		if (res != null) {
			res.setAutorank(autorank);
			res.setOptions(object.split(";"));
		}

		return res;
	}

	public RequirementBuilder getRequirementBuilder() {
		return requirementBuilder;
	}

	public ResultBuilder getResultBuilder() {
		return resultBuilder;
	}

	private void setRequirementBuilder(
			final RequirementBuilder requirementBuilder) {
		this.requirementBuilder = requirementBuilder;
	}

	private void setResultBuilder(final ResultBuilder resultBuilder) {
		this.resultBuilder = resultBuilder;
	}

}
