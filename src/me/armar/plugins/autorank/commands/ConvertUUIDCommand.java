package me.armar.plugins.autorank.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;

/**
 * The command delegator for the '/ar convert' command.
 */
public class ConvertUUIDCommand extends AutorankCommand {

    private final Autorank plugin;

    public ConvertUUIDCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "No file was given to convert.");
            return true;
        }

        final String targetFile = args[1];

        if (targetFile.equalsIgnoreCase("playerdata")) {

            if (!plugin.getCommandsManager().hasPermission("autorank.convert.playerdata", sender)) {
                return true;
            }

            // Convert playerdata
            plugin.getPlayerDataConfig().convertNamesToUUIDs();

            sender.sendMessage(ChatColor.RED + "Converting playerdata.yml to use new UUID format.");
        } else if (targetFile.equalsIgnoreCase("data") || targetFile.equalsIgnoreCase("times")) {

            if (!plugin.getCommandsManager().hasPermission("autorank.convert.data", sender)) {
                return true;
            }

            // Convert data.yml
            // plugin.getPlaytimes().convertToUUIDStorage();

            sender.sendMessage(ChatColor.RED + "This operation is not supported anymore!");
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown file. Can convert either 'data' or 'playerdata'.");
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Convert a file to UUID format.";
    }

    @Override
    public String getPermission() {
        return "autorank.convert.data";
    }

    @Override
    public String getUsage() {
        return "/ar convert <file>";
    }
}
