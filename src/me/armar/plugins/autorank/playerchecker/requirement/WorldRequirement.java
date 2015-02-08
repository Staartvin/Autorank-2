package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class WorldRequirement extends Requirement {

	List<String> worlds = new ArrayList<String>();

	@Override
	public String getDescription() {
		return Lang.WORLD_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(worlds, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		final String world = player.getWorld().getName();

		progress = AutorankTools.makeProgressString(worlds, "", world);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final String world = player.getWorld().getName();

		for (final String realWorld : worlds) {
			if (realWorld != null && realWorld.equals(world))
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			if (options.length > 0)
				worlds.add(options[0]);
		}

		return !worlds.isEmpty();
	}
}
