package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.askyblockapi.ASkyBlockHandler;
import me.armar.plugins.autorank.language.Lang;

public class ASkyBlockLevelRequirement extends Requirement {

	private int islandLevel = -1;

	@Override
	public String getDescription() {

		String lang = Lang.ASKYBLOCK_LEVEL_REQUIREMENT.getConfigValue(islandLevel + "");

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		final ASkyBlockHandler handler = (ASkyBlockHandler) this.getAutorank().getDependencyManager()
				.getDependency(dependency.ASKYBLOCK);

		final UUID uuid = this.getAutorank().getUUIDStorage().getStoredUUID(player.getName());

		final int islandLevel = handler.getIslandLevel(uuid);

		return islandLevel + "/" + this.islandLevel;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final ASkyBlockHandler handler = (ASkyBlockHandler) this.getAutorank().getDependencyManager()
				.getDependency(dependency.ASKYBLOCK);

		final UUID uuid = this.getAutorank().getUUIDStorage().getStoredUUID(player.getName());

		final int islandLevel = handler.getIslandLevel(uuid);

		return islandLevel >= this.islandLevel;
	}

	@Override
	public boolean setOptions(final String[] options) {

		islandLevel = Integer.parseInt(options[0]);

		return islandLevel != -1;
	}
}
