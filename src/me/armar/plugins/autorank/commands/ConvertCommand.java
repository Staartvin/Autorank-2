package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * The command delegator for the '/ar convert' command.
 */
public class ConvertCommand extends AutorankCommand {

    private final Autorank plugin;

    public ConvertCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        sender.sendMessage("This command is not used anymore and will be deprecated.");

        return true;
    }

    @Override
    public String getDescription() {
        return "Convert a file to UUID format.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.CONVERT_TIME_DATA;
    }

    @Override
    public String getUsage() {
        return "/ar convert <file>";
    }
}
