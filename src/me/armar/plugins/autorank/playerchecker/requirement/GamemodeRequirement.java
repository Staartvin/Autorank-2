package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.AutorankTools;

import org.bukkit.entity.Player;

public class GamemodeRequirement extends Requirement {

	int gamemode = -1;
	private boolean optional = false;

	@Override
	public boolean setOptions(String[] options, boolean optional) {
		this.optional = optional;
		
		if (options.length > 0)
			this.gamemode = AutorankTools.stringtoInt(options[0]);
		return (gamemode != -1);
	}

	@Override
	public boolean meetsRequirement(Player player) {
		return gamemode != -1 && gamemode == player.getGameMode().getValue();
	}

	@Override
	public String getDescription() {
		return "Need to be in gamemode " + gamemode + ".";
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

}
