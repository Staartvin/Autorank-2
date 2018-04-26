package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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

        if (!this.hasPermission(AutorankPermission.IMPORT_DATA, sender)) {
            return true;
        }

        // Import data from vanilla minecraft
        if (args.length > 1 && args[1] != null && args[1].equalsIgnoreCase("vanilladata")) {

            int importedPlayers = 0;

            for (OfflinePlayer offlinePlayer : plugin.getServer().getOfflinePlayers()) {

                if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getPlayer() == null) continue;

                // Time in seconds
                int vanillaTime = offlinePlayer.getPlayer().getStatistic(Statistic.PLAY_ONE_TICK) / 20;

                plugin.getPlayTimeManager().addLocalPlayTime(TimeType.TOTAL_TIME, offlinePlayer.getUniqueId(),
                        vanillaTime / 60);

                importedPlayers++;
            }

            sender.sendMessage(ChatColor.GREEN + "Imported data of " + importedPlayers + " players from Minecraft " +
                    "statistics!");

            return true;
        }

        int supportingProviders = 0;

        // Check whether there is a storage provide that supports importing
        for (String namedStorageProvider : plugin.getStorageManager().getActiveStorageProviders()) {
            StorageProvider storageProvider = plugin.getStorageManager().getActiveStorageProvider(namedStorageProvider);

            if (storageProvider.canImportData()) {
                supportingProviders++;
            }
        }

        if (supportingProviders <= 0) {
            sender.sendMessage(ChatColor.RED + "There is no active storage provider that allows importing of " +
                    "storage, hence Autorank cannot import data.");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Started importing data for " + ChatColor.GOLD +
                supportingProviders + ChatColor.GREEN + " active storage providers.");

        plugin.getStorageManager().importDataForStorageProviders();

        AutorankTools.sendColoredMessage(sender, Lang.DATA_IMPORTED.getConfigValue());

        return true;
    }

    @Override
    public String getDescription() {
        return "Import old storage.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.IMPORT_DATA;
    }

    @Override
    public String getUsage() {
        return "/ar import";
    }
}
