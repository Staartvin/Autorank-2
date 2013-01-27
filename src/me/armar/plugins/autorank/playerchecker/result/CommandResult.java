package me.armar.plugins.autorank.playerchecker.result;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class CommandResult extends Result{
    
    private String command = null;
    private Server server = null;

    @Override
    public boolean setOptions(String[] options) {
	this.server = this.getAutorank().getServer();
	command = options[0];
	return true;
    }

    @Override
    public boolean applyResult(Player player) {
	if (server != null){
	    String cmd = command.replace("&p", player.getName());
	    server.dispatchCommand(server.getConsoleSender(), cmd);
	}
	return server != null;
    }

}
