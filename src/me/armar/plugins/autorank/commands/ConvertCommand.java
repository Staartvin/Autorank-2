package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import org.bukkit.ChatColor;
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

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "No file was given to convert.");
            return true;
        }

        final String targetFile = args[1];

        if (targetFile.equalsIgnoreCase("playerdata")) {

            if (!this.hasPermission(AutorankPermission.CONVERT_PLAYER_DATA, sender)) {
                return true;
            }

            // Convert playerdata
            plugin.getPlayerDataConfig().convertNamesToUUIDs();

            sender.sendMessage(ChatColor.RED + "Converting playerdata.yml to use new UUID format.");
        } else if (targetFile.equalsIgnoreCase("data") || targetFile.equalsIgnoreCase("times")) {

            if (!this.hasPermission(AutorankPermission.CONVERT_TIME_DATA, sender)) {
                return true;
            }

            sender.sendMessage(ChatColor.RED + "This operation is not supported anymore!");
        } else if (targetFile.equalsIgnoreCase("simpleconfig")) {

            if (!this.hasPermission(AutorankPermission.CONVERT_SIMPLE_CONFIG, sender)) {
                return true;
            }

            // Convert SimpleConfig.
            if (plugin.getDataConverter().convertSimpleConfigToPaths()) {
                sender.sendMessage(ChatColor.GREEN + "SimpleConfig was converted to Paths.yml and stored as 'Paths_from_SimpleConfig.yml'!");
            } else {
                sender.sendMessage(ChatColor.RED + "Something went wrong when converting the SimpleConfig.yml. Are you sure the file is there?");
            }
        } else if (targetFile.equalsIgnoreCase("advancedconfig")) {

            if (!this.hasPermission(AutorankPermission.CONVERT_ADVANCED_CONFIG, sender)) {
                return true;
            }

            // Convert AdvancedConfig.
            if (plugin.getDataConverter().convertAdvancedConfigToPaths()) {
                sender.sendMessage(ChatColor.GREEN + "AdvancedConfig was converted to Paths.yml and stored as 'Paths_from_AdvancedConfig.yml'!");
            } else {
                sender.sendMessage(ChatColor.RED + "Something went wrong when converting the AdvancedConfig.yml. Are you sure the file is there?");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown file. Can convert either 'simpleconfig', 'advancedconfig' or 'playerdata'.");
        }

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
