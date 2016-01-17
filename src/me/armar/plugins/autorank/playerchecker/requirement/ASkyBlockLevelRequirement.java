package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.askyblockapi.ASkyBlockHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class ASkyBlockLevelRequirement extends Requirement {

	private final List<Integer> islandLevels = new ArrayList<Integer>();

	@Override
	public String getDescription() {

		String lang = Lang.ASKYBLOCK_LEVEL_REQUIREMENT.getConfigValue(AutorankTools.seperateList(islandLevels, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		ASkyBlockHandler handler = (ASkyBlockHandler) this.getAutorank().getDependencyManager()
				.getDependency(dependency.ASKYBLOCK);

		UUID uuid = this.getAutorank().getUUIDStorage().getStoredUUID(player.getName());

		final int islandLevel = handler.getIslandLevel(uuid);

		progress = AutorankTools.makeProgressString(islandLevels, "", islandLevel + "");
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		ASkyBlockHandler handler = (ASkyBlockHandler) this.getAutorank().getDependencyManager()
				.getDependency(dependency.ASKYBLOCK);

		UUID uuid = this.getAutorank().getUUIDStorage().getStoredUUID(player.getName());

		final int islandLevel = handler.getIslandLevel(uuid);

		for (final int level : islandLevels) {
			if (islandLevel >= level)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			islandLevels.add(Integer.parseInt(options[0]));
		}

		return !islandLevels.isEmpty();
	}
}
