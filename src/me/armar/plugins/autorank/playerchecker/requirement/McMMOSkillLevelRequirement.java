package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class McMMOSkillLevelRequirement extends Requirement {

	private int skillLevel = 0;
	private String skillName = "all";
	
	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0) {
			skillLevel = Integer.parseInt(options[0]);
		}
		if (options.length > 1) {
			skillName = options[1];
		}

		return true;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		McMMOHandler handler = (McMMOHandler) this.getAutorank()
				.getDependencyManager().getDependency(dependency.MCMMO);

		int level = handler.getSkillLevel(player, skillName);

		return level > 0 && level >= skillLevel;
	}

	@Override
	public String getDescription() {
		String skill = skillName;

		if (skill.equals("all")) {
			skill = "all skills";
		}

		return Lang.MCMMO_SKILL_LEVEL_REQUIREMENT.getConfigValue(new String[] {
				skillLevel + "", skill });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		int level = ((McMMOHandler) this.getAutorank().getDependencyManager()
				.getDependency(dependency.MCMMO)).getSkillLevel(player,
				skillName);

		progress = progress.concat(level + "/" + skillLevel);
		return progress;
	}
}
