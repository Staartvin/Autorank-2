package me.armar.plugins.autorank.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;

/**
 * The command delegator for the '/ar reset' command.
 */
public class ResetCommand extends AutorankCommand {

    private final Autorank plugin;

    public ResetCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.RESET_DATA, sender)) {
            return true;
        }
        
        
        if (args.length == 1) {
            
            sender.sendMessage(ChatColor.RED + "No user was specified.");
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            
            return true;
            
        } else if (args.length == 2) {
            
            sender.sendMessage(ChatColor.RED + "No type was specified.");
            sender.sendMessage(ChatColor.RED + "You can use: progress, chosenpath or completedpaths");
            
            return true;
        }

        final String target = args[1];
        final String action = args[2];

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(target);

        if (uuid == null) {
            sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(target));
            return true;
        }

        final String realName = plugin.getUUIDStorage().getRealName(uuid);

        if (!action.equalsIgnoreCase("progress") && !action.equalsIgnoreCase("chosenpath") && !action.equalsIgnoreCase("completedpaths")) {
            sender.sendMessage(ChatColor.RED + "Invalid type. You can only use: progress, chosenpath or completedpaths");
            return true;
        }

        if (action.equalsIgnoreCase("progress")) {
            plugin.getPlayerDataConfig().setCompletedRequirements(uuid, null);
            sender.sendMessage(ChatColor.GREEN + "Progress of " + ChatColor.YELLOW + realName + ChatColor.GREEN + " has been reset.");
        } else if (action.equalsIgnoreCase("chosenpath")) {
            plugin.getPlayerDataConfig().setChosenPath(uuid, null);
            sender.sendMessage(ChatColor.GREEN + "Chosen path of " + ChatColor.YELLOW + realName + ChatColor.GREEN + " has been reset.");
        } else if (action.equalsIgnoreCase("completedpaths")) {
            plugin.getPlayerDataConfig().setCompletedPaths(uuid, null);
            sender.sendMessage(ChatColor.GREEN + "Completed paths of " + ChatColor.YELLOW + realName + ChatColor.GREEN + " have been reset.");
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Reset certain data of a player";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.RESET_DATA;
    }

    @Override
    public String getUsage() {
        return "/ar reset <player> <type>";
    }
}
