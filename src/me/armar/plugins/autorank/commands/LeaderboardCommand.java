package me.armar.plugins.autorank.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import net.md_5.bungee.api.ChatColor;

/**
 * The command delegator for the '/ar leaderboard' command.
 */
public class LeaderboardCommand extends AutorankCommand {

    private final Autorank plugin;

    public LeaderboardCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.VIEW_LEADERBOARD.getPermissionString(), sender)) {
            return true;
        }

        // Whether to broadcast
        boolean broadcast = false;
        boolean force = false;

        for (final String arg : args) {
            if (arg.equalsIgnoreCase("force")) {

                // Check for permission
                if (!sender.hasPermission(AutorankPermission.FORCE_UPDATE_LEADERBOARD.getPermissionString())) {
                    sender.sendMessage(Lang.NO_PERMISSION.getConfigValue("autorank.leaderboard.force"));
                    return true;
                }

                force = true;
            } else if (arg.equalsIgnoreCase("broadcast")) {

                // Check for permission
                if (!sender.hasPermission(AutorankPermission.BROADCAST_LEADERBOARD.getPermissionString())) {
                    sender.sendMessage(Lang.NO_PERMISSION.getConfigValue("autorank.leaderboard.broadcast"));
                    return true;
                }

                broadcast = true;
            }
        }

        String leaderboardType = "total";
        TimeType type = null;

        if (args.length > 1 && !args[1].equalsIgnoreCase("force") && !args[1].equalsIgnoreCase("broadcast")) {
            leaderboardType = args[1].toLowerCase();
        }

        if (leaderboardType.equalsIgnoreCase("total")) {
            type = TimeType.TOTAL_TIME;
        } else if (leaderboardType.equalsIgnoreCase("daily") || leaderboardType.contains("day")) {
            type = TimeType.DAILY_TIME;
        } else if (leaderboardType.contains("week")) {
            type = TimeType.WEEKLY_TIME;
        } else if (leaderboardType.contains("month")) {
            type = TimeType.MONTHLY_TIME;
        }

        if (type == null) {
            sender.sendMessage(Lang.INVALID_LEADERBOARD_TYPE.getConfigValue());
            return true;
        }
        
        final TimeType type2 = type;

        if (force) {
            // Forcely update leaderboard first.
            sender.sendMessage(ChatColor.GREEN + "Updating the leaderboard. This could take a while!");
            sender.sendMessage(ChatColor.GOLD + "I'll let you know when the leaderboard is updated.");
            
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                public void run() {
                    // Update leaderboard.
                    plugin.getLeaderboardManager().updateLeaderboard(type2);
                    
                    sender.sendMessage(ChatColor.YELLOW + "Leaderboard updated!");
                }
            });
            
            return true;
        }

        if (!broadcast) {
            plugin.getLeaderboardManager().sendLeaderboard(sender, type);
        } else {
            plugin.getLeaderboardManager().broadcastLeaderboard(type);
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Show the leaderboard.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.VIEW_LEADERBOARD.getPermissionString();
    }

    @Override
    public String getUsage() {
        return "/ar leaderboard <type>";
    }
}
