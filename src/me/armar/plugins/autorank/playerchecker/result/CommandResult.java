package me.armar.plugins.autorank.playerchecker.result;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class CommandResult extends Result {

	private List<String> commands = null;
	private Server server = null;

	@Override
	public boolean applyResult(final Player player) {
		if (server != null) {
			for (final String command : commands) {
				final String cmd = command.replace("&p", player.getName());
				server.dispatchCommand(server.getConsoleSender(), cmd);
			}

		}
		return server != null;
	}

	@Override
	public boolean setOptions(final String[] commands) {
		this.server = this.getAutorank().getServer();
		final List<String> replace = new ArrayList<String>();
		for (final String command : commands) {
			replace.add(command.trim());
		}
		this.commands = replace;
		return true;
	}
}
