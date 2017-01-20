package me.armar.plugins.autorank.backup;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;

import com.google.common.io.Files;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;

/**
 * This class is used to backup several data files of Autorank.
 * @author Staartvin
 * 
 */
public class BackupManager {

	private final BackupDataManager backupDataManager;
	private final Autorank plugin;

	public BackupManager(final Autorank plugin) {
		this.plugin = plugin;
		backupDataManager = new BackupDataManager(plugin);

		backupDataManager.createNewFile();
	}

	/**
	 * Backup a file to a folder.
	 * 
	 * @param sourceFileName Path of file to backup
	 * @param storePath Path to backup the file to, can be null
	 */
	public void backupFile(final String sourceFileName, final String storePath) {
		// CAN ONLY COPY YML
		final String folderPath = plugin.getDataFolder().getAbsolutePath() + File.separator;
		final File sourceFile = new File(folderPath + sourceFileName);

		File copyFile = null;

		if (storePath == null) {
			copyFile = new File(
					folderPath + sourceFileName.replace(".yml", "") + "-backup-" + System.currentTimeMillis() + ".yml");
		} else {
			copyFile = new File(storePath.replace(".yml", "") + "-backup-" + System.currentTimeMillis() + ".yml");
		}

		// Create folder if it doesn't exist.
		copyFile.getParentFile().mkdirs();

		try {
			Files.copy(sourceFile, copyFile);
			plugin.debugMessage("Made backup of '" + sourceFileName + "'!");
		} catch (final IOException e) {
			plugin.getServer().getConsoleSender().sendMessage(
					"[Autorank] " + ChatColor.RED + "Could not find " + sourceFileName + ", so didn't backup.");
			//e.printStackTrace();
		}

	}

	/**
	 * Start the internal backup system of Autorank.
	 * This will make a backup of each data file every 24 hours.
	 */
	public void startBackupSystem() {
		// Makes a backup every day
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {

				// Older than a day
				if ((System.currentTimeMillis() - backupDataManager.getLatestBackup("data")) > 86400000) {
					plugin.debugMessage(ChatColor.RED + "Making a backup of all data files!");

					for (TimeType type : TimeType.values()) {
						String path = FlatFileManager.dataTypePaths.get(type);

						plugin.getBackupManager().backupFile(path, plugin.getDataFolder().getAbsolutePath()
								+ File.separator + "backups" + File.separator + path.replace("/data/", ""));
					}

					// Update latest backup time
					backupDataManager.getConfig().set("data", System.currentTimeMillis());
				}

				// Older than a day
				if ((System.currentTimeMillis() - backupDataManager.getLatestBackup("playerdata")) > 86400000) {
					plugin.debugMessage(ChatColor.RED + "Making a backup of PlayerData file!");

					// Before running, backup stuff.
					plugin.getBackupManager().backupFile("/playerdata/PlayerData.yml",
							plugin.getDataFolder().getAbsolutePath() + File.separator + "backups" + File.separator
									+ "PlayerData.yml");

					// Update latest backup time
					backupDataManager.getConfig().set("playerdata", System.currentTimeMillis());
				}

				// Save config
				backupDataManager.saveConfig();
			}
		}, 0, 1728000);
	}

}
