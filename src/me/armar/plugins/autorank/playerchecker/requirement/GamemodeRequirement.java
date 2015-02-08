package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class GamemodeRequirement extends Requirement {

	private final List<Integer> gamemodes = new ArrayList<Integer>();

	@Override
	public String getDescription() {
		return Lang.GAMEMODE_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(gamemodes, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";

		@SuppressWarnings("deprecation")
		final int gamemode = player.getGameMode().getValue();

		progress = AutorankTools.makeProgressString(gamemodes, "", gamemode);
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		@SuppressWarnings("deprecation")
		final int gamemode = player.getGameMode().getValue();

		for (final int mode : gamemodes) {
			if (gamemode == mode)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			if (options.length > 0)
				gamemodes.add(AutorankTools.stringtoInt(options[0]));
		}

		return !gamemodes.isEmpty();
	}
}
