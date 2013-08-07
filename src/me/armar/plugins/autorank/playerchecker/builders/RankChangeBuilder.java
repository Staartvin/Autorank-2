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
			timeReq.setOptions(new String[] { options[1] }, false);
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

			result.add(new RankChange(rank, options[0], req, res));
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
				System.out.print("requirement "
						+ requirement
						+ " optional: "
						+ configHandler.isOptional(requirement, group));
				
				req.add(createRequirement(requirement, configHandler.getRequirement(requirement, group), configHandler.isOptional(requirement, group)));

			}

				for (String resu : configHandler.getResults(group)) {
					res.add(createResult(resu, configHandler.getResult(resu, group)));
				}

				
			String[] rankChange = configHandler.getRankChange(group).split(";");

			if (rankChange.length <= 0) {
				System.out
						.print("[AutoRank] Advanced Config is not configured correctly!");
				autorank.getServer().getPluginManager().disablePlugin(autorank);
				return null;
			}

			String rankTo = null;
			if (rankChange.length == 1) {
				rankTo = rankChange[0].trim();
			} else {
				rankTo = rankChange[1].trim();
			}

			result.add(new RankChange(group, rankTo, req, res));
		}

		return result;
	}

	private Result createResult(String type, Object object) {
		Result res = resultBuilder.create(type);
		object = object.toString();

		if (res != null) {
			res.setAutorank(autorank);
			res.setOptions(((String) object).split(";"));
		}

		return res;
	}

	private Requirement createRequirement(String type, Object arg,
			boolean optional) {
		arg = arg.toString();
		Requirement res = requirementBuilder.create(type);

		if (res != null) {
			res.setAutorank(autorank);
			res.setOptions(((String) arg).split(";"), optional);
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
