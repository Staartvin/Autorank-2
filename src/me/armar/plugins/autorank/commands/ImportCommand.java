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
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

        // Get parameters specified by the user.
        List<String> parameters = this.getArgumentOptions(args);

        // Keep track of where we should write the data to.
        // By default, we only write to the local database.
        boolean writeToGlobalDatabase = false, writeToLocalDatabase = true;

        // Keep track of what data we should override.
        boolean overwriteGlobalDatabase = false, overwriteLocalDatabase = false;

        // Check the parameters to be used later.

        if (parameters.contains("db-only")) {
            writeToGlobalDatabase = true;
            writeToLocalDatabase = false;
        } else if (parameters.contains("db")) {
            writeToGlobalDatabase = true;
        }

        if (parameters.contains("overwrite-all")) {
            overwriteGlobalDatabase = true;
            overwriteLocalDatabase = true;
            writeToGlobalDatabase = true;
            writeToLocalDatabase = true;
        } else if (parameters.contains("overwrite-flatfile")) {
            overwriteLocalDatabase = true;
            writeToLocalDatabase = true;
        } else if (parameters.contains("overwrite-db")) {
            overwriteGlobalDatabase = true;
            writeToGlobalDatabase = true;
        }


        // Import data from vanilla minecraft
        // TODO: Remove this random command and replace it with something more logical.
        if (args.length > 1 && args[1] != null && args[1].equalsIgnoreCase("vanilladata")) {

            int importedPlayers = 0;

            for (OfflinePlayer offlinePlayer : plugin.getServer().getOfflinePlayers()) {

                if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getPlayer() == null) continue;

                // Time in minutes
                int vanillaTime = offlinePlayer.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE);

                plugin.getPlayTimeManager().addLocalPlayTime(TimeType.TOTAL_TIME, offlinePlayer.getUniqueId(),
                        vanillaTime);

                importedPlayers++;
            }

            sender.sendMessage(ChatColor.GREEN + "Imported data of " + importedPlayers + " players from Minecraft " +
                    "statistics!");

            return true;
        }

        // Check if we have an active storage provider.
        if (plugin.getStorageManager().getActiveStorageProviders().size() == 0) {
            sender.sendMessage(ChatColor.RED + "There are no active storage providers, so I can't store the imported " +
                    "data!");
            return true;
        }

        // Check if we want to write to the global database and if there is a storage provider active that allows this.
        if (writeToGlobalDatabase && !plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.DATABASE)) {
            sender.sendMessage(ChatColor.RED + "You want to store the imported data to the global database, but no " +
                    "database is active.");
            return true;
        }

        // We need final booleans for using it in a runnable
        boolean finalWriteToLocalDatabase = writeToLocalDatabase;
        boolean finalWriteToGlobalDatabase = writeToGlobalDatabase;
        boolean finalOverwriteGlobalDatabase = overwriteGlobalDatabase;
        boolean finalOverwriteLocalDatabase = overwriteLocalDatabase;

        // Run task async because it might take long.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {

                // Read import folder and find files there.

                String importFolder =
                        plugin.getDataFolder().getAbsolutePath() + File.separator + "imports" + File.separator;

                Map<String, TimeType> filesToImport = new HashMap<String, TimeType>() {
                    {
                        put("Total_time.yml", TimeType.TOTAL_TIME);
                        put("Daily_time.yml", TimeType.DAILY_TIME);
                        put("Weekly_time.yml", TimeType.WEEKLY_TIME);
                        put("Monthly_time.yml", TimeType.MONTHLY_TIME);
                    }
                };

                // Notify user of actions we are going to take.
                if (finalWriteToGlobalDatabase && finalWriteToLocalDatabase) {

                    if (finalOverwriteGlobalDatabase && finalOverwriteLocalDatabase) {
                        sender.sendMessage(ChatColor.GOLD + "Importing data and overriding both the global " +
                                "and local database.");
                    } else {
                        if (finalOverwriteGlobalDatabase) {
                            sender.sendMessage(ChatColor.GOLD + "Importing data and overriding global database.");
                        } else {
                            sender.sendMessage(ChatColor.GOLD + "Importing data and adding to global database.");
                        }

                        if (finalOverwriteLocalDatabase) {
                            sender.sendMessage(ChatColor.GOLD + "Importing data and overriding local database" +
                                    ".");
                        } else {
                            sender.sendMessage(ChatColor.GOLD + "Importing data and adding to local database.");
                        }
                    }
                } else if (finalWriteToGlobalDatabase) {
                    if (finalOverwriteGlobalDatabase) {
                        sender.sendMessage(ChatColor.GOLD + "Importing data and overriding global database.");
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Importing data and adding to global database.");
                    }
                } else {
                    if (finalOverwriteLocalDatabase) {
                        sender.sendMessage(ChatColor.GOLD + "Importing data and overriding local database.");
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Importing data and adding to local database.");
                    }
                }

                for (Map.Entry<String, TimeType> fileToImport : filesToImport.entrySet()) {
                    YamlConfiguration timeConfig = YamlConfiguration.loadConfiguration(new File(importFolder +
                            fileToImport.getKey()));

                    TimeType importedTimeType = fileToImport.getValue();

                    int importedPlayers = 0;

                    for (String uuidString : timeConfig.getKeys(false)) {
                        if (uuidString == null) return;

                        int importedValue = timeConfig.getInt(uuidString);
                        UUID importedPlayer = null;

                        try {
                            importedPlayer = UUID.fromString(uuidString);
                        } catch (IllegalArgumentException exception) {
                            return; // We cannot parse this player.
                        }

                        // Count how many players we imported
                        importedPlayers++;

                        if (finalWriteToLocalDatabase && finalWriteToGlobalDatabase) {
                            // Update both local and global database.

                            if (finalOverwriteGlobalDatabase && finalOverwriteLocalDatabase) {
                                // Overwrite both databases.
                                plugin.getStorageManager().setPlayerTime(importedTimeType, importedPlayer,
                                        importedValue);
                            } else {

                                // Overwrite global database
                                if (finalOverwriteGlobalDatabase) {
                                    plugin.getStorageManager().setPlayerTime(StorageProvider.StorageType.DATABASE,
                                            importedTimeType,
                                            importedPlayer, importedValue);
                                } else {
                                    plugin.getStorageManager().addPlayerTime(StorageProvider.StorageType.DATABASE,
                                            importedTimeType,
                                            importedPlayer, importedValue);
                                }

                                // Overwrite local database
                                if (finalOverwriteLocalDatabase) {
                                    plugin.getStorageManager().setPlayerTime(StorageProvider.StorageType.FLAT_FILE,
                                            importedTimeType,
                                            importedPlayer, importedValue);
                                } else {
                                    plugin.getStorageManager().addPlayerTime(StorageProvider.StorageType.FLAT_FILE,
                                            importedTimeType,
                                            importedPlayer, importedValue);
                                }
                            }
                        } else if (finalWriteToGlobalDatabase) {
                            // Update only global database.

                            if (finalOverwriteGlobalDatabase) {
                                plugin.getStorageManager().setPlayerTime(StorageProvider.StorageType.DATABASE,
                                        importedTimeType,
                                        importedPlayer, importedValue);
                            } else {
                                plugin.getStorageManager().addPlayerTime(StorageProvider.StorageType.DATABASE,
                                        importedTimeType,
                                        importedPlayer, importedValue);
                            }
                        } else {
                            // Update only local database.

                            if (finalOverwriteLocalDatabase) {
                                plugin.getStorageManager().setPlayerTime(StorageProvider.StorageType.FLAT_FILE,
                                        importedTimeType,
                                        importedPlayer, importedValue);
                            } else {
                                plugin.getStorageManager().addPlayerTime(StorageProvider.StorageType.FLAT_FILE,
                                        importedTimeType,
                                        importedPlayer, importedValue);
                            }
                        }
                    }

                    // Give a heads up to the sender if no files were imported.
                    if (importedPlayers == 0) {
                        sender.sendMessage(ChatColor.RED + "Could not import any players for " + importedTimeType +
                                "! Are you sure you put any files in the imports folder?");
                    }
                }

                AutorankTools.sendColoredMessage(sender, Lang.DATA_IMPORTED.getConfigValue());
            }
        });

        return true;
    }

    @Override
    public String getDescription() {
        return "Import time data from your flatfiles into the system.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.IMPORT_DATA;
    }

    @Override
    public String getUsage() {
        return "/ar import <parameters>";
    }
}
