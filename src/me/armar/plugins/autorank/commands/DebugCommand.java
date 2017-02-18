package me.armar.plugins.autorank.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.permissions.AutorankPermission;

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
        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.DEBUG_FILE, sender)) {
            return true;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                final String fileName = plugin.getDebugger().createDebugFile();

                sender.sendMessage(ChatColor.GREEN + "Debug file '" + fileName + "' created!");
            }
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
