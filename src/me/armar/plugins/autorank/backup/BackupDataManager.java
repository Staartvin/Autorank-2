package me.armar.plugins.autorank.backup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.armar.plugins.autorank.Autorank;

/**
 * This class is used to create a backup-data.yml that will store what files
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
     * Create a new backup data file if it did not exist already. <br>
     * If it already exists, it will be loaded into memory.
     */
    public void createNewFile() {
        reloadConfig();
        saveConfig();

        loadConfig();

        plugin.getLogger().info("Backup data file loaded (backup-data.yml)");
    }

    /**
     * Get the backup-data.yml file.
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
     * @param file
     *            File to check
     * @return the last time a file was backed up (UNIX timestamp), or -1 if it
     *         was never backed up.
     */
    public long getLatestBackup(final String file) {
        return backupConfig.getLong(file, -1);
    }

    /**
     * Load the backup data file of Autorank (backup-data.yml).
     */
    public void loadConfig() {

        backupConfig.options()
                .header("Backup-data file" + "\nDon't edit this file if you don't know what you are doing. "
                        + "\nThis file is used by Autorank to check when the latest backups were made.");

        backupConfig.addDefault("data", 0);
        backupConfig.addDefault("playerdata", 0);

        backupConfig.options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Reload the backup-data.yml file.
     */
    @SuppressWarnings("deprecation")
    public void reloadConfig() {
        if (backupConfigFile == null) {
            backupConfigFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "backups",
                    "backup-data.yml");
        }
        backupConfig = YamlConfiguration.loadConfiguration(backupConfigFile);

        // Look for defaults in the jar
        final InputStream defConfigStream = plugin.getResource("backup-data.yml");
        if (defConfigStream != null) {
            final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            backupConfig.setDefaults(defConfig);
        }
    }

    /**
     * Save the backup-data.yml file.
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
