package me.armar.plugins.autorank.backup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Class used to get data from backups Autorank is regularly doing. <br>
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

	public void createNewFile() {
		reloadConfig();
		saveConfig();

		loadConfig();

		plugin.getLogger().info("Backup data file loaded (backup-data.yml)");
	}

	public FileConfiguration getConfig() {
		if (backupConfig == null) {
			this.reloadConfig();
		}
		return backupConfig;
	}

	public void loadConfig() {

		backupConfig
				.options()
				.header("Backup-data file"
						+ "\nDon't edit this file if you don't know what you are doing. "
						+ "\nThis file is used by Autorank to check when the latest backups were made.");

		backupConfig.addDefault("data", 0);
		backupConfig.addDefault("playerdata", 0);

		backupConfig.options().copyDefaults(true);
		saveConfig();
	}

	@SuppressWarnings("deprecation")
	public void reloadConfig() {
		if (backupConfigFile == null) {
			backupConfigFile = new File(plugin.getDataFolder()
					.getAbsolutePath() + File.separator + "backups",
					"backup-data.yml");
		}
		backupConfig = YamlConfiguration.loadConfiguration(backupConfigFile);

		// Look for defaults in the jar
		final InputStream defConfigStream = plugin
				.getResource("backup-data.yml");
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			backupConfig.setDefaults(defConfig);
		}
	}

	public void saveConfig() {
		if (backupConfig == null || backupConfigFile == null) {
			return;
		}
		try {
			getConfig().save(backupConfigFile);
		} catch (final IOException ex) {
			plugin.getLogger().log(Level.SEVERE,
					"Could not save config to " + backupConfigFile, ex);
		}
	}

	public long getLatestBackup(final String file) {
		return backupConfig.getLong(file, -1);
	}
}
