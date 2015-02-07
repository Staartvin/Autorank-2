package me.armar.plugins.autorank.playerchecker.result;

import org.bukkit.entity.Player;

public class MessageResult extends Result {

	String msg = null;

	@Override
	public boolean applyResult(final Player player) {
		if (player == null) {
			return false;
		}
		player.sendMessage(msg.replaceAll("(&([a-z0-9]))", "\u00A7$2"));
		return msg != null;
	}

	@Override
	public boolean setOptions(final String[] options) {
		if (options.length > 0)
			msg = options[0];
		return msg != null;
	}

}
