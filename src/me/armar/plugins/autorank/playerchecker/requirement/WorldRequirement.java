package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;

import org.bukkit.entity.Player;

public class WorldRequirement extends Requirement {

	String world = null;

	@Override
	public String getDescription() {
		return Lang.WORLD_REQUIREMENT.getConfigValue(new String[] { world });
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(player.getWorld().getName() + "/" + world);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		return world != null && world.equals(player.getWorld().getName());
	}

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0)
			this.world = options[0];

		return (world != null);
	}
}
