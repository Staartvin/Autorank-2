package me.armar.plugins.autorank.playerchecker.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.config.ConfigHandler;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.requirement.TimeRequirement;
import me.armar.plugins.autorank.playerchecker.result.MessageResult;
import me.armar.plugins.autorank.playerchecker.result.RankChangeResult;
import me.armar.plugins.autorank.playerchecker.result.Result;

public class RankChangeBuilder {

	private ResultBuilder resultBuilder;
	private RequirementBuilder requirementBuilder;
	private Autorank autorank;

	public RankChangeBuilder(Autorank autorank) {
		this.autorank = autorank;
		setResultBuilder(new ResultBuilder());
		setRequirementBuilder(new RequirementBuilder());
	}

	public List<RankChange> createFromSimpleConfig(
			SimpleYamlConfiguration config) {
		// TODO logging errors and not making faulty RankChanges
		List<RankChange> result = new ArrayList<RankChange>();

		Set<String> ranks = config.getKeys(false);
		Iterator<String> it = ranks.iterator();

		while (it.hasNext()) {
			String rank = it.next();
			String value = (String) config.get(rank);

			String[] options = value.split(" after ");

			if (options.length <= 0) {
				System.out
						.print("[AutoRank] Simple Config is not configured correctly!");
				autorank.getServer().getPluginManager().disablePlugin(autorank);
				return null;
			}

			// Time requirement
			List<Requirement> req = new ArrayList<Requirement>();
			Requirement timeReq = new TimeRequirement();
			timeReq.setOptions(new String[] { options[1] }, false,
					new ArrayList<Result>(), true, 0);
			timeReq.setAutorank(autorank);
			req.add(timeReq);

			// Change the rank
			List<Result> res = new ArrayList<Result>();
			Result change = new RankChangeResult();
			change.setOptions(new String[] { rank, options[0] });
			change.setAutorank(autorank);
			res.add(change);

			// Change the message
			Result message = new MessageResult();
			message.setOptions(new String[] { "&2You got ranked to "
					+ options[0] });
			message.setAutorank(autorank);
			res.add(message);

			result.add(new RankChange(autorank, rank, options[0], req, res));
		}

		return result;
	}

	public List<RankChange> createFromAdvancedConfig(
			SimpleYamlConfiguration config) {
		List<RankChange> result = new ArrayList<RankChange>();
		ConfigHandler configHandler = autorank.getConfigHandler();

		//ConfigurationSection section = config.getConfigurationSection("ranks");
		for (String group : configHandler.getRanks()) {

			List<Requirement> req = new ArrayList<Requirement>();
			List<Result> res = new ArrayList<Result>();

			for (String requirement : configHandler.getRequirements(group)) {
				// Implement optional option logic
				boolean optional = configHandler.isOptional(requirement, group);
				// Result for requirement
				List<String> results = configHandler.getResultsOfRequirement(
						requirement, group);

				// Create a new result List that will get all result when a requirement is met.
				List<Result> realResults = new ArrayList<Result>();

				for (String resultString : results) {
					realResults.add(createResult(resultString, configHandler
							.getResultOfRequirement(requirement, group,
									resultString)));
				}
				int reqId = configHandler.getReqId(requirement, group);

				//System.out.print("REQ ID of " + requirement + " for group " + group + ": " + reqId);

				if (reqId < 0) {
					try {
						throw new Exception(
								"REQ ID COULDN'T BE FOUND! REPORT TO AUTHOR!"
										+ " GROUP: " + group
										+ ", REQUIREMENT: " + requirement);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						autorank.getLogger().severe(e.getCause().getMessage());
						return result;
					}
				}
				req.add(createRequirement(AutorankTools.getCorrectName(requirement),
						configHandler.getRequirement(requirement, group),
						optional, realResults,
						configHandler.useAutoCompletion(group, requirement),
						reqId));

			}

			for (String resu : configHandler.getResults(group)) {
				res.add(createResult(resu, configHandler.getResult(resu, group)));
			}

			String rankTo = null;

			if (configHandler.getRankChange(group) != null) {
				String[] rankChange = configHandler.getRankChange(group).split(
						";");

				if (rankChange.length <= 0) {
					autorank.getWarningManager().registerWarning("Rank change of " + group + " is invalid!", 10);
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

	private Result createResult(String type, String object) {
		Result res = resultBuilder.create(type);

		if (res != null) {
			res.setAutorank(autorank);
			res.setOptions(object.split(";"));
		}

		return res;
	}

	private Requirement createRequirement(String type, String arg,
			boolean optional, List<Result> results, boolean autoComplete,
			int reqId) {
		Requirement res = requirementBuilder.create(type);

		if (res != null) {
			res.setAutorank(autorank);
			res.setOptions(arg.split(";"), optional, results, autoComplete,
					reqId);
		}
		return res;
	}

	public ResultBuilder getResultBuilder() {
		return resultBuilder;
	}

	private void setResultBuilder(ResultBuilder resultBuilder) {
		this.resultBuilder = resultBuilder;
	}

	public RequirementBuilder getRequirementBuilder() {
		return requirementBuilder;
	}

	private void setRequirementBuilder(RequirementBuilder requirementBuilder) {
		this.requirementBuilder = requirementBuilder;
	}

}
