package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * The command delegator for the '/ar debug' command.
 */
public class DebugCommand extends AutorankCommand {

    private final Autorank plugin;

    public DebugCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // This will create a 'debug.txt' file containing a lot of information
        // about the plugin
        if (!this.hasPermission(AutorankPermission.DEBUG_FILE, sender)) {
            return true;
        }

        // Toggle debugger because we may need it. -- Note that Autorank will now output debug messages.
        Debugger.debuggerEnabled = !Debugger.debuggerEnabled;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final String fileName = plugin.getDebugger().createDebugFile();

            sender.sendMessage(ChatColor.GREEN + "Debug file '" + fileName + "' created!");
        });

        return true;
    }

    @Override
    public String getDescription() {
        return "Shows debug information.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.DEBUG_FILE;
    }

    @Override
    public String getUsage() {
        return "/ar debug";
    }
}
