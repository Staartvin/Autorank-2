package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class ExpRequirement extends Requirement {

	private int minExp = 999999999;

	@Override
	public boolean setOptions(final String[] options) {
		try {
			minExp = AutorankTools.stringtoInt(options[0]);
		} catch (final Exception e) {
		}

		return minExp == 999999999;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		return player.getLevel() >= minExp;
	}

	@Override
	public String getDescription() {
		return Lang.EXP_REQUIREMENT
				.getConfigValue(new String[] { minExp + "" });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(player.getLevel() + "/" + minExp);
		return progress;
	}
}
