package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * The command delegator for the '/ar sync' command.
 */
public class SyncCommand extends AutorankCommand {

    private final Autorank plugin;

    public SyncCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!this.hasPermission(AutorankPermission.SYNC_MYSQL_TABLE, sender))
            return true;

        if (args.length > 1 && args[1].equalsIgnoreCase("stats")) {
            sender.sendMessage(ChatColor.RED + "You probably meant /ar syncstats or /ar sync!");
            return true;
        }

        // If reverse is true, we don't put info TO the database, but we get
        // info FROM the database.
        boolean reverse = false;

        if (args.length > 1 && args[1].equalsIgnoreCase("reverse")) {
            reverse = true;
        }

        // Check if MySQL is active
        if (!plugin.getSettingsConfig().useMySQL()) {
            sender.sendMessage(Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        }

        // Check if Flatfile is active
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.FLAT_FILE)) {
            sender.sendMessage(ChatColor.RED + "There is no active storage provider that supports flatfile data.");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "You do not have to use this command regularly.");

        StorageProvider flatfileStorageProvider = plugin.getStorageManager().getStorageProvider(StorageProvider
                .StorageType.FLAT_FILE);

        StorageProvider databaseStorageProvider = plugin.getStorageManager().getStorageProvider(StorageProvider
                .StorageType.DATABASE);

        if (reverse) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                int count = 0;

                // Loop over all time types
                for (TimeType timeType : TimeType.values()) {
                    // Get all UUIDs.
                    List<UUID> storedUUIDsFlatfile = flatfileStorageProvider.getStoredPlayers(timeType);

                    // For each uuid, set its time to that of the database.
                    for (UUID uuid : storedUUIDsFlatfile) {
                        int databaseValue = 0;
                        try {
                            databaseValue = databaseStorageProvider.getPlayerTime(timeType, uuid).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                        // Skip entries that are empty.
                        if (databaseValue <= 0) {
                            continue;
                        }

                        flatfileStorageProvider.setPlayerTime(timeType, uuid, databaseValue);
                        count++;
                    }

                }

                sender.sendMessage(ChatColor.GREEN + "Successfully updated " + count + " items in data.yml from " +
                        "MySQL database records!");
            });
        } else {
            // Do this async as we are accessing mysql database.
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

                // Get all stored uuids and update MySQL database

                // Loop over all time types
                for (TimeType timeType : TimeType.values()) {
                    // Get all UUIDs.
                    List<UUID> storedUUIDsFlatfile = flatfileStorageProvider.getStoredPlayers(timeType);

                    // For each uuid, get the flatfile value and update the MYSQL database.
                    for (UUID uuid : storedUUIDsFlatfile) {
                        int flatfileValue = 0;
                        try {
                            flatfileValue = flatfileStorageProvider.getPlayerTime(timeType, uuid).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                        // Skip entries that are empty.
                        if (flatfileValue <= 0) {
                            continue;
                        }

                        // Add time to database instead of overwriting it
                        databaseStorageProvider.addPlayerTime(timeType, uuid, flatfileValue);
                    }

                }
                sender.sendMessage(ChatColor.GREEN + "Successfully updated MySQL records!");
            });
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Sync MySQL database with server (Use only once per server).";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.SYNC_MYSQL_TABLE;
    }

    @Override
    public String getUsage() {
        return "/ar sync";
    }
}
