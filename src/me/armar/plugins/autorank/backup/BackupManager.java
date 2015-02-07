package me.armar.plugins.autorank.backup;

import java.io.File;
import java.io.IOException;

import me.armar.plugins.autorank.Autorank;

import com.google.common.io.Files;

/**
 * Class that allows me to backup files before overwriting them.
 * Stores functions to backup.
 * <p>
 * Date created: 15:25:43 12 dec. 2014
 * 
 * @author Staartvin
 * 
 */
public class BackupManager {

	private final Autorank plugin;

	public BackupManager(final Autorank plugin) {
		this.plugin = plugin;
	}

	public void backupFile(final String sourceFileName) {
		// CAN ONLY COPY YML
		final String folderPath = plugin.getDataFolder().getAbsolutePath()
				+ File.separator;
		final File sourceFile = new File(folderPath + sourceFileName);
		final File copyFile = new File(folderPath
				+ sourceFileName.replace(".yml", "") + "-backup.yml");

		try {
			Files.copy(sourceFile, copyFile);
			plugin.debugMessage("Made backup of '" + sourceFileName + "'!");
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

}
