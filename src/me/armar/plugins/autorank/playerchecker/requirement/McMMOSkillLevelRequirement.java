package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class McMMOSkillLevelRequirement extends Requirement {

	private int skillLevel = 0;
	private String skillName = "all";
	private McMMOHandler handler = null;

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
		final int level = handler.getSkillLevel(player, skillName);

		progress = progress.concat(level + "/" + skillLevel);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final int level = handler.getSkillLevel(player, skillName);

		return level > 0 && level >= skillLevel;
	}

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0) {
			skillLevel = Integer.parseInt(options[0]);
		}
		if (options.length > 1) {
			skillName = options[1];
		}

		handler = (McMMOHandler) this.getDependencyManager().getDependency(
				dependency.MCMMO);

		return true;
	}
}
