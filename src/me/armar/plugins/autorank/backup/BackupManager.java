package me.armar.plugins.autorank.backup;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import me.armar.plugins.autorank.Autorank;

/**
 * Class that allows me to backup files before overwriting them.
 * Stores functions to backup.
 * <p>
 * Date created:  15:25:43
 * 12 dec. 2014
 * @author Staartvin
 *
 */
public class BackupManager {

	private Autorank plugin;
	
	public BackupManager(Autorank plugin) {
		this.plugin = plugin;
	}
	
	public void backupFile(String sourceFileName) {
		// CAN ONLY COPY YML
		final String folderPath = plugin.getDataFolder().getAbsolutePath()
				+ File.separator;
		File sourceFile = new File(folderPath + sourceFileName);
		File copyFile = new File(folderPath + sourceFileName.replace(".yml", "") + "-backup.yml");
		
		try {
			Files.copy(sourceFile, copyFile);
			plugin.debugMessage("Made backup of '" + sourceFileName + "'!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
