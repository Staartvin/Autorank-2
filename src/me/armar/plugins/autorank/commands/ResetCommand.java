package me.armar.plugins.autorank.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;

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

        if (!plugin.getCommandsManager().hasPermission("autorank.reset", sender)) {
            return true;
        }

        if (args.length != 3) {

            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar reset <player> <action>"));
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

        if (!action.equalsIgnoreCase("progress") && !action.equalsIgnoreCase("chosenpath")) {
            sender.sendMessage(ChatColor.RED + "Invalid action. You can only use: progress or chosenpath");
            return true;
        }

        if (action.equalsIgnoreCase("progress")) {
            plugin.getPlayerDataConfig().setCompletedRequirements(uuid, null);
            sender.sendMessage(ChatColor.GREEN + "Reset progress of " + ChatColor.YELLOW + realName);
        } else if (action.equalsIgnoreCase("chosenpath")) {
            plugin.getPlayerDataConfig().setChosenPath(uuid, null);
            sender.sendMessage(ChatColor.GREEN + "Reset chosen path of " + ChatColor.YELLOW + realName);
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Reset certain data of a player";
    }

    @Override
    public String getPermission() {
        return "autorank.reset";
    }

    @Override
    public String getUsage() {
        return "/ar reset <player> <action>";
    }
}
