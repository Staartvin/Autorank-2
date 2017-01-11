package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.RPGmeHandler;

public class RPGMeCombatLevelRequirement extends Requirement {

	private RPGmeHandler handler = null;
	private int skillLevel = -1;

	@Override
	public String getDescription() {

		return Lang.RPGME_COMBAT_LEVEL_REQUIREMENT.getConfigValue(skillLevel);
	}

	@Override
	public String getProgress(final Player player) {

		int level = 0;

		level = handler.getCombatLevel(player);

		return level + "/" + skillLevel;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		if (!handler.isAvailable())
			return false;

		return handler.getCombatLevel(player) >= skillLevel;
	}

	@Override
	public boolean setOptions(final String[] options) {

		handler = (RPGmeHandler) this.getDependencyManager().getDependencyHandler(Dependency.RPGME);

		if (options.length > 0) {
			skillLevel = Integer.parseInt(options[0]);
		}

		return skillLevel != -1 && handler != null && handler.isAvailable();
	}
}
