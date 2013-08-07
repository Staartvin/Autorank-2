package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.AutorankTools;

import org.bukkit.entity.Player;

public class ExpRequirement extends Requirement {

	private int minExp = 999999999;
	private boolean optional = false;

	@Override
	public boolean setOptions(String[] options, boolean optional) {
		this.optional = optional;
		
		try {
			minExp = AutorankTools.stringtoInt(options[0]);
		} catch (Exception e) {
		}

		return minExp == 999999999;
	}

	@Override
	public boolean meetsRequirement(Player player) {
		return player.getLevel() >= minExp;
	}

	@Override
	public String getDescription() {
		return "Have at least level " + minExp + " in exp.";
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

}
