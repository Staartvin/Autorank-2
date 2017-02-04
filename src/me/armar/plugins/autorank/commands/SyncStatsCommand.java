package me.armar.plugins.autorank.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * The command delegator for the '/ar syncstats' command.
 */
public class SyncStatsCommand extends AutorankCommand {

    private final Autorank plugin;

    public SyncStatsCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!plugin.getCommandsManager().hasPermission("autorank.syncstats", sender))
            return true;

        if (!plugin.getHookedStatsPlugin().isEnabled()) {
            sender.sendMessage(ChatColor.RED + "Stats is not enabled!");
            return true;
        }

        int count = 0;

        // Sync playtime of every player
        for (final UUID uuid : plugin.getFlatFileManager().getUUIDKeys(TimeType.TOTAL_TIME)) {

            final OfflinePlayer p = plugin.getServer().getOfflinePlayer(uuid);

            // Time is stored in seconds
            final int statsPlayTime = plugin.getHookedStatsPlugin().getNormalStat(StatsPlugin.statTypes.TIME_PLAYED,
                    p.getUniqueId(), AutorankTools.makeStatsInfo());

            if (statsPlayTime <= 0) {
                continue;
            }

            // Update time
            plugin.getFlatFileManager().setLocalTime(TimeType.TOTAL_TIME, Math.round(statsPlayTime / 60), uuid);

            // Increment count
            count++;
        }

        if (count == 0) {
            sender.sendMessage(ChatColor.GREEN + "Could not sync stats. Run command again!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Time has succesfully been updated for all entries.");
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Sync Autorank's time to Stats' time.";
    }

    @Override
    public String getPermission() {
        return "autorank.syncstats";
    }

    @Override
    public String getUsage() {
        return "/ar syncstats";
    }
}
