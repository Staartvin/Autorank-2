package me.armar.plugins.autorank.playerchecker.requirement;


import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;

public class WorldRequirement extends Requirement {

	String worldName = null;

	@Override
	public String getDescription() {
		return Lang.WORLD_REQUIREMENT.getConfigValue(worldName);
	}

	@Override
	public String getProgress(final Player player) {
		final String world = player.getWorld().getName();
		return world + "/" + worldName;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		final String world = player.getWorld().getName();

		return (worldName != null && world.equals(worldName));
	}

	@Override
	public boolean setOptions(String[] options) {

		if (options.length > 0) {
			worldName = options[0];
		}

		return worldName != null;
	}
}
