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
		String world = player.getWorld().getName();

		progress = AutorankTools.makeProgressString(worlds, "", world);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		String world = player.getWorld().getName();

		for (String realWorld : worlds) {
			if (realWorld != null && realWorld.equals(world))
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(List<String[]> optionsList) {

		for (String[] options : optionsList) {
			if (options.length > 0)
				worlds.add(options[0]);
		}
		
		return !worlds.isEmpty();
	}
}
