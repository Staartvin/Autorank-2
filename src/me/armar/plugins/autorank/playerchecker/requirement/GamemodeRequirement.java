package me.armar.plugins.autorank.playerchecker.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class GamemodeRequirement extends Requirement {

	int gameMode = -1;

	@Override
	public String getDescription() {

		String lang = Lang.GAMEMODE_REQUIREMENT.getConfigValue(gameMode + "");

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
	}

	@Override
	public String getProgress(final Player player) {

		@SuppressWarnings("deprecation")
		final int gamemode = player.getGameMode().getValue();

		return gamemode + "/" + gameMode;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			// Is player in the same world as specified
			if (!this.getWorld().equals(player.getWorld().getName()))
				return false;
		}

		@SuppressWarnings("deprecation")
		final int gamemode = player.getGameMode().getValue();

		return gamemode == gameMode;
	}

	@Override
	public boolean setOptions(String[] options) {

		if (options.length > 0)
			gameMode = AutorankTools.stringtoInt(options[0]);

		return gameMode != -1;
	}
}
