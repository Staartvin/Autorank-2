package me.armar.plugins.autorank.commands;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.activity.History;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

/**
 * The command delegator for the '/ar times' command.
 */
public class ActivityCommand extends AutorankCommand {

    private final Autorank plugin;

    public ActivityCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // How far do we want to look back (in minutes).
        int historyTime = -1;
        boolean specifiedTime = false;

        if (args.length == 1) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));

            return true;
        } else if (args.length == 2) {

            // Did not specify a history, so default to 24 hours
            historyTime = 24 * 60;
        } else {

            String timeString = args[2];

            historyTime = AutorankTools.stringToTime(timeString, Time.MINUTES);

            if (historyTime < 0) {
                sender.sendMessage(ChatColor.RED + "Your history format is not correct. Use a format like 2d 4h 5m.");
                return true;
            }
            
            specifiedTime = true;
        }

        // Now determine target name

        String targetName = args[1];

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(targetName);

        if (uuid == null) {
            sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(targetName));
            return true;
        }

        // Now show data for target.
        targetName = plugin.getUUIDStorage().getRealName(uuid);

        if (targetName == null) {
            // This player has no real name stored -> use cached name
            targetName = plugin.getUUIDStorage().getCachedPlayerName(uuid);
        }

        if (sender instanceof Player) {
            if (targetName.equals(sender.getName())) {
                if (!plugin.getCommandsManager().hasPermission(AutorankPermission.VIEW_ACTIVITY_SELF, sender)) {
                    return true;
                }
            }
        }

        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.VIEW_ACTIVITY_OTHERS, sender)) {
            return true;
        }

        // Activity in seconds
        long activity = plugin.getActivityTracker().getActivityInHistory(uuid,
                new History(historyTime, TimeUnit.MINUTES));

        String historyTimeString = AutorankTools.timeToString(historyTime, Time.MINUTES);
        
        if (!specifiedTime) {
            sender.sendMessage(ChatColor.DARK_RED + "You did not specify a time, so I defaulted you to " + ChatColor.AQUA + historyTimeString + ChatColor.DARK_RED + " instead.");
        }
        
        sender.sendMessage(ChatColor.GOLD + targetName + " has played for " + ChatColor.GREEN
                + AutorankTools.timeToString((int) activity, Time.SECONDS) + ChatColor.GOLD + " in the last "
                + ChatColor.AQUA + historyTimeString);

        return true;
    }

    @Override
    public String getDescription() {
        return "View the play activity of a player within a recent history.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.VIEW_ACTIVITY_SELF;
    }

    @Override
    public String getUsage() {
        return "/ar activity <player> <history>";
    }
}
