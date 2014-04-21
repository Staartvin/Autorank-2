package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

public class McMMOSkillLevelRequirement extends Requirement {

	private int skillLevel = 0;
	private String skillName = "all";
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	public McMMOSkillLevelRequirement() {
		super();
	}

	@Override
	public boolean setOptions(final String[] options, final boolean optional,
			final List<Result> results, final boolean autoComplete,
			final int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;

		if (options.length > 0) {
			skillLevel = Integer.parseInt(options[0]);
		} if (options.length > 1) {
			skillName = options[1];
		}
		
		return true;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		McMMOHandler handler = (McMMOHandler) this.getAutorank().getDependencyManager().getDependency(dependency.MCMMO);
		
		int level = handler.getSkillLevel(player, skillName);
		
		return level > 0 && level >= skillLevel;
	}

	@Override
	public String getDescription() {
		String skill = skillName;
		
		if (skill.equals("all")) {
			skill = "all skills";
		}
		
		return Lang.MCMMO_SKILL_LEVEL_REQUIREMENT
				.getConfigValue(new String[] { skillLevel + "", skill });
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		int level = ((McMMOHandler) this.getAutorank().getDependencyManager().getDependency(dependency.MCMMO)).getSkillLevel(player, skillName);

		progress = progress.concat(level + "/" + skillLevel);
		return progress;
	}

	@Override
	public boolean useAutoCompletion() {
		return autoComplete;
	}

	@Override
	public int getReqId() {
		return reqId;
	}
}
