package me.armar.plugins.autorank.playerchecker.result;

import org.bukkit.entity.Player;

public class RankChangeResult extends Result {

	String from = null;
	String to = null;
	String world = null;

	@Override
	public boolean setOptions(String[] options) {
		if (options.length > 1) {
			from = options[0].trim();
			to = options[1].trim();
		}
		if (options.length > 2) {
			world = options[2].trim();
		}
		return from != null && to != null;
	}

	@Override
	public boolean applyResult(Player player) {
		return this.getAutorank().getPermPlugHandler()
				.replaceGroup(player, world, from, to);
	}

}
