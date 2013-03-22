package me.armar.plugins.autorank.playerchecker.result;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class CommandResult extends Result {

	private List<String> commands = null;
	private Server server = null;

	@Override
	public boolean setOptions(String[] commands) {
		this.server = this.getAutorank().getServer();
		List<String> replace = new ArrayList<String>();
		for (String command:commands) {
			replace.add(command);
		}
		this.commands = replace;
		return true;
	}

	@Override
	public boolean applyResult(Player player) {
		if (server != null) {
			for (String command:commands) {
				String cmd = command.replace("&p", player.getName());
				server.dispatchCommand(server.getConsoleSender(), cmd);
			}
			
		}
		return server != null;
	}
}
