package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandResult extends AbstractResult {

    private List<String> commands = null;
    private Server server = null;

    @Override
    public boolean applyResult(final Player player) {
        if (server != null) {
            this.getAutorank().getServer().getScheduler().runTask(this.getAutorank(), new Runnable() {
                public void run() {
                    for (final String command : commands) {
                        final String cmd = command.replace("&p", player.getName());
                        server.dispatchCommand(server.getConsoleSender(), cmd);
                    }
                }
            });
        }
        return server != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.pathbuilder.result.AbstractResult#getDescription()
     */
    @Override
    public String getDescription() {
        return Lang.COMMAND_RESULT.getConfigValue(AutorankTools.createStringFromList(commands));
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
