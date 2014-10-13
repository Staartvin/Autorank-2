package me.armar.plugins.autorank.util.uuid.storage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * This class represents a multitude of files where are looked up uuids are
 * stored.
 * </br>Every player has its own uuid, which is stored with the time it was last
 * stored.
 * <p>
 * Date created: 15:35:30 13 okt. 2014
 * 
 * @author Staartvin
 * 
 */
public class UUIDStorage {

	private HashMap<String, FileConfiguration> configs = new HashMap<String, FileConfiguration>();
	private HashMap<String, File> configFiles = new HashMap<String, File>();

	private final Autorank plugin;

	private final String desFolder;

	// Expiration date in hours
	private final int expirationDate = 24;

	private List<String> fileSuffixes = Arrays.asList("a", "b", "c", "d", "e",
			"f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
			"s", "t", "u", "v", "w", "x", "y", "z", "other");

	public UUIDStorage(final Autorank instance) {
		this.plugin = instance;

		desFolder = plugin.getDataFolder() + "/uuids";

		//Run save task every 2 minutes
		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			public void run() {
				saveAllFiles();
			}
		}, 1200, 2400);
	}

	public void createNewFiles() {

		for (String suffix : fileSuffixes) {
			plugin.debugMessage("Loading uuids_" + suffix + " ...");

			reloadConfig(suffix);
			loadConfig(suffix);
		}

		// Convert old format to new UUID storage format
		//convertNamesToUUIDs();

		plugin.getLogger().info("Loaded stored uuids.");
	}

	public void saveAllFiles() {
		for (String suffix : fileSuffixes) {
			saveConfig(suffix);
		}
	}

	public FileConfiguration getConfig(String key) {
		FileConfiguration config = configs.get(key);

		if (config == null) {
			this.reloadConfig(key);
		}

		return config;
	}

	public void loadConfig(String key) {

		FileConfiguration config = configs.get(key);

		config.options()
				.header("This file stores all uuids of players that Autorank has looked up before."
						+ "\nEach file stores accounts with the starting letter of the player's name.");

		config.options().copyDefaults(true);
		saveConfig(key);
	}

	public void reloadConfig(String key) {
		File configFile = null;
		FileConfiguration config = null;

		if (!configFiles.containsKey(key) || configFiles.get(key) == null) {
			configFile = new File(desFolder, "uuids_" + key + ".yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);

		// Store new configs
		configs.put(key, config);
		configFiles.put(key, configFile);
	}

	public void saveConfig(String key) {
		File configFile = configFiles.get(key);
		FileConfiguration config = configs.get(key);

		if (config == null || configFile == null) {
			return;
		}

		try {
			getConfig(key).save(configFile);
		} catch (final IOException ex) {
			plugin.getLogger().log(Level.SEVERE,
					"Could not save config to " + configFile, ex);
		}
	}

	public String findMatchingKey(String text) {
		text = text.toLowerCase();

		for (String key : fileSuffixes) {
			// Don't check for that one.
			if (key.equals("other"))
				continue;

			// Check if name starts with letter
			if (text.startsWith(key)) {
				return key;
			}
		}

		// return 'uuids_other.yml'
		return "other";
	}

	public FileConfiguration findCorrectConfig(String playerName) {
		String key = findMatchingKey(playerName);

		FileConfiguration config = configs.get(key);

		return config;
	}

	public boolean isOutdated(String playerName) {
		int time = getLastUpdateTime(playerName);
		return (time > expirationDate || time < 0);
	}

	public UUID getStoredUUID(String playerName) {

		String uuidString = findCorrectConfig(playerName).getString(
				playerName + ".uuid", null);

		if (uuidString == null) {
			return null;
		}

		return UUID.fromString(uuidString);
	}

	public int getLastUpdateTime(String playerName) {
		long lastUpdateTime = findCorrectConfig(playerName).getLong(
				playerName + ".updateTime", -1);

		if (lastUpdateTime < 0) {
			return -1;
		}

		long difference = System.currentTimeMillis() - lastUpdateTime;

		int timeDifference = Math.round(difference / 3600000);

		return timeDifference;
	}

	public void storeUUID(String playerName, UUID uuid) {
		FileConfiguration config;

		// Remove old name and uuid because apparently name was changed.
		if (isAlreadyStored(uuid)) {
			// Change name to new name
			String oldUser = getPlayerName(uuid);

			// Change config pointer to correct config
			config = findCorrectConfig(oldUser);

			config.set(oldUser, null);
		}

		config = findCorrectConfig(playerName);

		config.set(playerName + ".uuid", uuid.toString());
		config.set(playerName + ".updateTime", System.currentTimeMillis());
	}

	public String getPlayerName(UUID uuid, String key) {
		FileConfiguration config = configs.get(key);

		for (String fPlayerName : config.getKeys(false)) {
			String fuuid = config.getString(fPlayerName + ".uuid");

			if (fuuid.equals(uuid.toString())) {
				return fPlayerName;
			}
		}

		return null;
	}

	public String getPlayerName(UUID uuid) {
		for (String suffix : fileSuffixes) {
			FileConfiguration config = getConfig(suffix);

			for (String fPlayerName : config.getKeys(false)) {
				String fuuid = config.getString(fPlayerName + ".uuid");

				if (fuuid.equals(uuid.toString())) {
					return fPlayerName;
				}
			}
		}

		return null;
	}

	public boolean isAlreadyStored(UUID uuid, String key) {
		return getPlayerName(uuid, key) != null;
	}

	public boolean isAlreadyStored(UUID uuid) {
		return getPlayerName(uuid) != null;
	}

}
