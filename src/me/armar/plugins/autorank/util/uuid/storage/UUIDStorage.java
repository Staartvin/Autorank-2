package me.armar.plugins.autorank.util.uuid.storage;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * This class represents a multitude of files where are looked up uuids are
 * stored. </br>
 * Every player has its own uuid, which is stored with the time it was last
 * stored.
 * <p>
 * Date created: 15:35:30 13 okt. 2014
 *
 * @author Staartvin
 */
public class UUIDStorage {

    private final HashMap<String, File> configFiles = new HashMap<String, File>();
    private final HashMap<String, FileConfiguration> configs = new HashMap<String, FileConfiguration>();

    private final String desFolder;

    // Expiration date in hours
    private final int expirationDate = 24;

    private final List<String> fileSuffixes = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
            "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "other");

    private final Autorank plugin;

    public UUIDStorage(final Autorank instance) {
        this.plugin = instance;

        desFolder = plugin.getDataFolder() + "/uuids";

        // Run save task every 2 minutes
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                saveAllFiles();
            }
        }, AutorankTools.TICKS_PER_MINUTE, AutorankTools.TICKS_PER_MINUTE * 2);
    }

    public void createNewFiles() {

        plugin.getLogger().info("Loading UUID storage files...");
        long startTime = System.currentTimeMillis();

        for (final String suffix : fileSuffixes) {
            plugin.debugMessage("Loading uuids_" + suffix + " ...");

            reloadConfig(suffix);
            loadConfig(suffix);
        }

        plugin.getLogger().info("Loaded UUID storage in " + (System.currentTimeMillis() - startTime) /
                1000 + " seconds.");
    }

    public FileConfiguration findCorrectConfig(String playerName) {

        // Everything is now stored in lowercase.
        playerName = playerName.toLowerCase();

        final String key = findMatchingKey(playerName);

        final FileConfiguration config = configs.get(key);

        return config;
    }

    public String findMatchingKey(String text) {
        text = text.toLowerCase();

        for (final String key : fileSuffixes) {
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

    public String getCachedPlayerName(final UUID uuid) {
        for (final String suffix : fileSuffixes) {
            final FileConfiguration config = getConfig(suffix);

            if (config == null) {
                return null;
            }

            for (final String fPlayerName : config.getKeys(false)) {
                final String fuuid = config.getString(fPlayerName + ".uuid");

                // Skip this player, as there is no uuid
                if (fuuid == null)
                    continue;

                if (fuuid.equals(uuid.toString())) {
                    return fPlayerName;
                }
            }
        }

        return null;
    }

    public String getCachedPlayerName(final UUID uuid, final String key) {
        final FileConfiguration config = configs.get(key);

        if (config == null) {
            return null;
        }

        for (final String fPlayerName : config.getKeys(false)) {
            final String fuuid = config.getString(fPlayerName + ".uuid");

            if (fuuid.equals(uuid.toString())) {
                return fPlayerName;
            }
        }

        return null;
    }

    public FileConfiguration getConfig(final String key) {
        final FileConfiguration config = configs.get(key);

        if (config == null) {
            this.reloadConfig(key);
        }

        return config;
    }

    public int getLastUpdateTime(String playerName) {

        // Everything is now stored in lowercase.
        playerName = playerName.toLowerCase();

        FileConfiguration fileConfiguration = findCorrectConfig(playerName);

        if (fileConfiguration == null) {
            return -1;
        }

        final long lastUpdateTime = fileConfiguration.getLong(playerName + ".updateTime", -1);

        if (lastUpdateTime < 0) {
            return -1;
        }

        final long difference = System.currentTimeMillis() - lastUpdateTime;

        final int timeDifference = Math.round(difference / 3600000);

        return timeDifference;
    }

    public String getRealName(final UUID uuid) {
        // Returns the real name of the player, or the cached lower case name if
        // no real name exists.
        final String cachedName = this.getCachedPlayerName(uuid);

        if (cachedName == null)
            return null;

        final FileConfiguration config = this.findCorrectConfig(cachedName);

        if (config == null)
            return null;

        final Object realNameObject = config.get(cachedName + ".realName", null);

        return (realNameObject != null ? realNameObject.toString() : null);
    }

    public UUID getStoredUUID(String playerName) {

        // Everything is now stored in lowercase.
        playerName = playerName.toLowerCase();

        FileConfiguration fileConfiguration = findCorrectConfig(playerName);

        if (fileConfiguration == null) {
            return null;
        }

        final String uuidString = fileConfiguration.getString(playerName + ".uuid", null);

        if (uuidString == null) {
            return null;
        }

        return UUID.fromString(uuidString);
    }

    public boolean hasRealName(final UUID uuid) {
        return getRealName(uuid) != null;
    }

    public boolean isAlreadyStored(final UUID uuid) {
        return getCachedPlayerName(uuid) != null;
    }

    public boolean isAlreadyStored(final UUID uuid, final String key) {
        return getCachedPlayerName(uuid, key) != null;
    }

    public boolean isOutdated(String playerName) {

        // Everything is now stored in lowercase.
        playerName = playerName.toLowerCase();

        final int time = getLastUpdateTime(playerName);
        return (time > expirationDate || time < 0);
    }

    public void loadConfig(final String key) {

        final FileConfiguration config = configs.get(key);

        config.options().header("This file stores all uuids of players that Autorank has looked up before."
                + "\nEach file stores accounts with the starting letter of the player's name.");

        config.options().copyDefaults(true);
        saveConfig(key);
    }

    public void reloadConfig(final String key) {
        File configFile = null;
        FileConfiguration config = null;

        configFile = new File(desFolder, "uuids_" + key + ".yml");

        config = YamlConfiguration.loadConfiguration(configFile);

        // Store new configs
        configs.put(key, config);
        configFiles.put(key, configFile);
    }

    public void saveAllFiles() {
        for (final String suffix : fileSuffixes) {
            saveConfig(suffix);
        }
    }

    public void saveConfig(final String key) {
        final File configFile = configFiles.get(key);
        final FileConfiguration config = configs.get(key);

        if (config == null || configFile == null) {
            return;
        }

        try {
            getConfig(key).save(configFile);
        } catch (final IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public void storeUUID(String playerName, final UUID uuid, final String realName) {
        FileConfiguration config;

        // Everything is now stored in lowercase.
        playerName = playerName.toLowerCase();

        // Remove old name and uuid because apparently name was changed.
        if (isAlreadyStored(uuid)) {
            // Change name to new name
            final String oldUser = getCachedPlayerName(uuid);

            // Change config pointer to correct config
            config = findCorrectConfig(oldUser);

            // If this player does not have a real name yet, go add it.
            if (this.getRealName(uuid) == null) {
                config.set(playerName + ".realName", realName);
            }

            // Name didn't change, it was just out of date.
            if (oldUser.equals(playerName)) {
                // Don't do anything besides updating updateTime.
                config.set(playerName + ".updateTime", System.currentTimeMillis());

                // plugin.debugMessage("Refreshed user '" + playerName
                // + "' with uuid " + uuid + "!");
                return;
            }

            config.set(oldUser, null);

            // plugin.debugMessage("Deleting old user '" + oldUser + "'!");
        }

        config = findCorrectConfig(playerName);

        if (config == null) {
            plugin.debugMessage("Could not store uuid " + uuid.toString() + " of player " + playerName);
            return;
        }

        config.set(playerName + ".uuid", uuid.toString());
        config.set(playerName + ".updateTime", System.currentTimeMillis());

        if (realName != null) {
            config.set(playerName + ".realName", realName);
            // The real name is the name of the player with proper
            // capitalisation.
            // The real name is useful for leaderboards.
        }

        // plugin.debugMessage("Stored user '" + playerName + "' with uuid "
        // + uuid + "!");
    }

    public void transferUUIDs() {
        // Since Autorank 3.7.1, all names of players are stored lowercase. For
        // version that update from pre-3.7.1, all names should be converted to
        // lowercase as well.
        // This method checks every uuid and converts it to lowercase. It
        // returns how many uuids it changed.

        if (plugin.getInternalPropertiesConfig().hasTransferredUUIDs())
            return; // UUIDs were already successfully converted.

        plugin.getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED
                + "Since the uuid storage have not been converted yet, I need to convert your UUID files to a new " +
                "format.");
        plugin.getServer().getConsoleSender().sendMessage(
                "[Autorank] " + ChatColor.RED + "Converting UUID files to new format (3.7.1), this may take a while.");

        // Get every name in every file and change it to lowercase.
        for (final String suffix : fileSuffixes) {
            final FileConfiguration config = getConfig(suffix);

            if (config == null) {
                continue;
            }

            // All names that are in this config
            final Set<String> names = config.getKeys(false);

            for (final String name : names) {
                // Get old values
                final String uuidString = config.getString(name + ".uuid");
                final long updateTime = config.getLong(name + ".updateTime", 0);

                // Delete old name
                config.set(name, null);

                // Add new (lowercase) name
                config.set(name.toLowerCase() + ".uuid", uuidString);
                config.set(name.toLowerCase() + ".updateTime", updateTime);
            }
        }

        plugin.getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.GREEN
                + "All UUID files were properly converted. Please restart your server!");

        // Changed all names, now update boolean in internal properties.
        plugin.getInternalPropertiesConfig().hasTransferredUUIDs(true);
    }

    public List<String> getStoredPlayerNames() {
        // Return all playernames that are stored in the UUID folders

        List<String> playerNames = new ArrayList<>();

        for (Entry<String, FileConfiguration> entry : this.configs.entrySet()) {
            FileConfiguration config = entry.getValue();

            for (String playerName : config.getKeys(false)) {
                playerNames.add(playerName);
            }
        }

        return playerNames;

    }

}
