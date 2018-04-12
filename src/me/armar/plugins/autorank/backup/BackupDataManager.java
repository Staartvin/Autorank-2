package me.armar.plugins.autorank.backup;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * This class is used to create a backup-storage.yml that will store what files
 * were backed up and at what time.
 *
 * @author Staartvin
 */
public class BackupDataManager {

    private FileConfiguration backupConfig;
    private File backupConfigFile;
    private final Autorank plugin;

    public BackupDataManager(final Autorank autorank) {
        plugin = autorank;
    }

    /**
     * Create a new backup storage file if it did not exist already. <br>
     * If it already exists, it will be loaded into memory.
     */
    public void createNewFile() {
        reloadConfig();
        saveConfig();

        loadConfig();

        plugin.debugMessage("Backup storage file loaded (backup-storage.yml)");
    }

    /**
     * Get the backup-storage.yml file.
     *
     * @return
     */
    public FileConfiguration getConfig() {
        if (backupConfig == null) {
            this.reloadConfig();
        }
        return backupConfig;
    }

    /**
     * Get the last time a backup was made for a specific file.
     *
     * @param file File to check
     * @return the last time a file was backed up (UNIX timestamp), or -1 if it
     * was never backed up.
     */
    public long getLatestBackup(final String file) {
        return backupConfig.getLong(file, -1);
    }

    /**
     * Load the backup storage file of Autorank (backup-storage.yml).
     */
    public void loadConfig() {

        backupConfig.options()
                .header("Backup-storage file" + "\nDon't edit this file if you don't know what you are doing. "
                        + "\nThis file is used by Autorank to check when the latest backups were made.");

        backupConfig.addDefault("storage", 0);
        backupConfig.addDefault("playerdata", 0);

        backupConfig.options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Reload the backup-storage.yml file.
     */
    @SuppressWarnings("deprecation")
    public void reloadConfig() {
        if (backupConfigFile == null) {
            backupConfigFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "backups",
                    "backup-storage.yml");
        }
        backupConfig = YamlConfiguration.loadConfiguration(backupConfigFile);
    }

    /**
     * Save the backup-storage.yml file.
     */
    public void saveConfig() {
        if (backupConfig == null || backupConfigFile == null) {
            return;
        }
        try {
            getConfig().save(backupConfigFile);
        } catch (final IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + backupConfigFile, ex);
        }
    }
}
