package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class GamemodeRequirement extends Requirement {

	int gamemode = -1;

	@Override
	public String getDescription() {
		return Lang.GAMEMODE_REQUIREMENT.getConfigValue(new String[] { gamemode
				+ "" });
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getProgress(final Player player) {
		String progress = "";
		progress = progress.concat(player.getGameMode().getValue() + "/"
				+ gamemode);
		return progress;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean meetsRequirement(final Player player) {
		return gamemode != -1 && gamemode == player.getGameMode().getValue();
	}

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0)
			this.gamemode = AutorankTools.stringtoInt(options[0]);
		return (gamemode != -1);
	}
}
