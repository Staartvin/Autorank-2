package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;

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

        if (!this.hasPermission(AutorankPermission.RESET_DATA, sender)) {
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

        if (action.equalsIgnoreCase("progress")) {
            // Reset progress of active paths.
            plugin.getPathManager().resetProgressOnActivePaths(uuid);
            sender.sendMessage(ChatColor.GREEN + "Reset progress on all active paths of " + ChatColor.YELLOW +
                    realName);
        } else if (action.equalsIgnoreCase("activepaths")) {
            plugin.getPathManager().resetActivePaths(uuid);
            sender.sendMessage(ChatColor.GREEN + "Removed all active paths of " + ChatColor.YELLOW + realName);
        } else if (action.equalsIgnoreCase("completedpaths")) {
            plugin.getPathManager().resetCompletedPaths(uuid);
            sender.sendMessage(ChatColor.GREEN + "Removed all completed paths of " + ChatColor.YELLOW + realName);
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid action. You can only use: progress, activepaths or " +
                    "completedpaths.");
            return true;
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Reset certain storage of a player";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.RESET_DATA;
    }

    @Override
    public String getUsage() {
        return "/ar reset <player> <action>";
    }
}
