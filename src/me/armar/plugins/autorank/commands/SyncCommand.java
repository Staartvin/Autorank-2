package me.armar.plugins.autorank.commands;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.language.Lang;

/**
 * The command delegator for the '/ar sync' command.
 */
public class SyncCommand extends AutorankCommand {

    private final Autorank plugin;

    public SyncCommand(final Autorank instance) {
        this.setUsage("/ar sync");
        this.setDesc("Sync MySQL database with server (Use only once per server).");
        this.setPermission("autorank.sync");

        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!plugin.getCommandsManager().hasPermission("autorank.sync", sender))
            return true;

        if (args.length > 1 && args[1].equalsIgnoreCase("stats")) {
            sender.hasPermission(ChatColor.RED + "You probably meant /ar syncstats or /ar sync!");
            return true;
        }

        // If reverse is true, we don't put info TO the database, but we get
        // info FROM the database.
        boolean reverse = false;

        if (args.length > 1 && args[1].equalsIgnoreCase("reverse")) {
            reverse = true;
        }

        if (!plugin.getConfigHandler().useMySQL()) {
            sender.sendMessage(Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        }

        sender.sendMessage(ChatColor.RED + "You do not have to use this command regularly.");

        if (reverse) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    int count = 0;

                    // Update all data.yml records
                    for (Entry<UUID, Integer> entry : plugin.getMySQLManager().getAllPlayersFromDatabase().entrySet()) {
                        plugin.getFlatFileManager().setLocalTime(TimeType.TOTAL_TIME, entry.getValue(), entry.getKey());
                        count++;
                    }

                    sender.sendMessage(ChatColor.GREEN + "Successfully updated Data.yml from " + count
                            + " MySQL database records!");
                }
            });
        } else {
            // Do this async as we are accessing mysql database.
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    // Update all mysql records
                    for (final UUID uuid : plugin.getFlatFileManager().getUUIDKeys(TimeType.TOTAL_TIME)) {
                        final int localTime = plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid);

                        if (localTime <= 0)
                            continue;

                        final int globalTime = plugin.getMySQLManager().getGlobalTime(uuid);

                        plugin.getMySQLManager().setGlobalTime(uuid, localTime + globalTime);
                    }
                    sender.sendMessage(ChatColor.GREEN + "Successfully updated MySQL records!");
                }
            });
        }

        return true;
    }
}
