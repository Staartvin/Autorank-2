package me.armar.plugins.autorank.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * The command delegator for the '/ar import' command.
 */
public class ImportCommand extends AutorankCommand {

    private final Autorank plugin;

    public ImportCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.IMPORT_DATA, sender)) {
            return true;
        }

        AutorankTools.sendColoredMessage(sender, Lang.DATA_IMPORTED.getConfigValue());
        plugin.getFlatFileManager().importData();

        return true;
    }

    @Override
    public String getDescription() {
        return "Import old data.";
    }

    @Override
    public AutorankPermission getPermission() {
        return AutorankPermission.IMPORT_DATA;
    }

    @Override
    public String getUsage() {
        return "/ar import";
    }
}
