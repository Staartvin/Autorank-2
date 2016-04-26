package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.acidislandapi.AcidIslandHandler;
import me.armar.plugins.autorank.language.Lang;

public class AcidIslandLevelRequirement extends Requirement {

	private int islandLevel = -1;

	@Override
	public String getDescription() {

		String lang = Lang.ACID_ISLAND_LEVEL_REQUIREMENT.getConfigValue(islandLevel + "");

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {
		final AcidIslandHandler handler = (AcidIslandHandler) this.getAutorank().getDependencyManager()
				.getDependency(dependency.ACIDISLAND);

		final UUID uuid = this.getAutorank().getUUIDStorage().getStoredUUID(player.getName());

		final int islandLevel = handler.getIslandLevel(uuid);

		return islandLevel + "/" + this.islandLevel;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final AcidIslandHandler handler = (AcidIslandHandler) this.getAutorank().getDependencyManager()
				.getDependency(dependency.ACIDISLAND);

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
