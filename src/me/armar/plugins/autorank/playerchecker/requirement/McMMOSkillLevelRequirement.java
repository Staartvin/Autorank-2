package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.mcmmoapi.McMMOHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class McMMOSkillLevelRequirement extends Requirement {

	// [0] skillName, [1] skillLevel
	private final List<String> skillsCombined = new ArrayList<String>();
	//private int skillLevel = 0;
	//private String skillName = "all";
	private McMMOHandler handler = null;

	@Override
	public String getDescription() {

		String desc = Lang.MCMMO_SKILL_LEVEL_REQUIREMENT.getConfigValue();

		for (int i = 0; i < skillsCombined.size(); i++) {
			//String skillCombined = skillsCombined.get(i);
			//String[] split = skillCombined.split(";");
			String skillName = AutorankTools.getStringFromSplitString(
					skillsCombined.get(i), ";", 0);
			final String skillLevel = AutorankTools.getStringFromSplitString(
					skillsCombined.get(i), ";", 1);

			if (skillName.equals("all") || skillName.equals("none")) {
				skillName = "all skills";
			}

			if (i == 0) {
				desc = desc.replace("{0}", skillLevel)
						.replace("{1}", skillName);
			} else {
				desc = desc.concat(" or level " + skillLevel + " in "
						+ skillName);
			}
		}

		return desc;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		for (int i = 0; i < skillsCombined.size(); i++) {
			final String skillCombined = skillsCombined.get(i);

			final int level = handler.getSkillLevel(player, AutorankTools
					.getStringFromSplitString(skillCombined, ";", 0));
			final String skillLevel = AutorankTools.getStringFromSplitString(
					skillCombined, ";", 1);

			if (i == 0) {
				progress = progress.concat(level + "/" + skillLevel);
			} else {
				progress = progress.concat(" or " + level + "/" + skillLevel);
			}

		}
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		for (int i = 0; i < skillsCombined.size(); i++) {
			final String skillCombined = skillsCombined.get(i);

			final int level = handler.getSkillLevel(player, AutorankTools
					.getStringFromSplitString(skillCombined, ";", 0));
			final String skillLevel = AutorankTools.getStringFromSplitString(
					skillCombined, ";", 1);

			if (level > 0 && level >= Integer.parseInt(skillLevel))
				return true;

		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		handler = (McMMOHandler) this.getDependencyManager().getDependency(
				dependency.MCMMO);

		for (final String[] options : optionsList) {
			String skillName = "all";
			int skillLevel = 0;

			if (options.length > 0) {
				skillLevel = Integer.parseInt(options[0]);
			}
			if (options.length > 1) {
				skillName = options[1];
			}

			skillsCombined.add(skillName + ";" + skillLevel);
		}

		return !skillsCombined.isEmpty();
	}
}
