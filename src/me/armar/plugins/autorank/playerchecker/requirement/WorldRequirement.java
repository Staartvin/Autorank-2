package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.LanguageHandler;

import org.bukkit.entity.Player;

public class WorldRequirement extends Requirement {

	String world = null;

	@Override
	public boolean setOptions(String[] options) {
		if (options.length > 0)
			this.world = options[0];
		return (world != null);
	}

	@Override
	public boolean meetsRequirement(Player player) {
		return world != null && world.equals(player.getWorld().getName());
	}

	@Override
	public String getDescription() {
		return LanguageHandler.getLanguage().getWorldRequirement(world);
	}

}
