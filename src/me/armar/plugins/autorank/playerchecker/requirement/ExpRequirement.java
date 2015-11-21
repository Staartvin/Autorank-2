package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class ExpRequirement extends Requirement {

	private final List<Integer> minExps = new ArrayList<Integer>();

	@Override
	public String getDescription() {

		String lang = Lang.EXP_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(minExps, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		final int expLevel = player.getLevel();

		progress = AutorankTools.makeProgressString(minExps, "", expLevel);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			// Is player in the same world as specified
			if (!this.getWorld().equals(player.getWorld().getName()))
				return false;
		}

		final int expLevel = player.getLevel();

		for (final int expMin : minExps) {
			if (expLevel >= expMin)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			minExps.add(AutorankTools.stringtoInt(options[0]));
		}

		return !minExps.isEmpty();
	}
}
