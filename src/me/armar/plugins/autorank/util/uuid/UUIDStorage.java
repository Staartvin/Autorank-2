package me.armar.plugins.autorank.util.uuid;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    // Expiration date in hours
    private static final int expirationDate = 24;
    private final HashMap<String, File> configFiles = new HashMap<String, File>();
    private final HashMap<String, FileConfiguration> configs = new HashMap<String, FileConfiguration>();
    private final String desFolder;
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

    public void loadStorageFiles() {

        plugin.getLogger().info("Loading UUID storage files...");
        long startTime = System.currentTimeMillis();

        fileSuffixes.parallelStream().forEach(suffix -> {
            plugin.debugMessage("Loading uuids_" + suffix + " ...");

            reloadConfig(suffix);
            loadConfig(suffix);
        });

        plugin.getLogger().info("Loaded UUID storage in " + (System.currentTimeMillis() - startTime) /
                1000 + " seconds.");
    }

    private FileConfiguration findCorrectConfig(String playerName) {

        // Everything is now stored in lowercase.
        final String key = findMatchingKey(playerName.toLowerCase());

        return configs.get(key);
    }

    private String findMatchingKey(String text) {
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

    /**
     * Get the username that is stored in the local storage for the given UUID.
     *
     * @param uuid UUID to find the username for.
     * @return username if found, null otherwise.
     */
    private String getStoredUsername(final UUID uuid) {
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

                    String realName = config.getString(fPlayerName + ".realName", null);

                    // There is a real name, return it.
                    if (realName != null) {
                        return realName;
                    }

                    // Otherwise, we just return the lowercase name.
                    return fPlayerName;
                }
            }
        }

        return null;
    }

    private FileConfiguration getConfig(final String key) {
        final FileConfiguration config = configs.get(key);

        if (config == null) {
            this.reloadConfig(key);
        }

        return config;
    }

    /**
     * Get how many hours have gone since the last time the playername of a UUID has been updated.
     *
     * @param playerName Name of the player.
     * @return time in hours or -1 if no time was set.
     */
    private int getLastUpdateTime(String playerName) {

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

        return Math.round(difference / 3600000);
    }

    /**
     * Get the username of the given UUID.
     * Note that this method will do an exhaustive search for the username. It will first use the local UUID storage
     * of Autorank, then try to look for it using Mojangs API. It could therefore take a bit of time and it's wise to
     * call this method asynchronously.
     *
     * @param uuid UUID to check.
     * @return name of the user that corresponds to the UUID. If none was found, returns null.
     */
    protected CompletableFuture<String> getUsername(final UUID uuid) {

        return CompletableFuture.supplyAsync(() -> {

            // First look in the local storage.
            final String storedUsername = this.getStoredUsername(uuid);

            // We found a match, so return it.
            if (storedUsername != null)
                return storedUsername;

            // We couldn't find a match, so start looking up via Mojang API.
            // This might block, so be careful with synchronous calls!
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            // We couldn't find a player with this uuid, so perhaps he has never played before.
            if (offlinePlayer.getName() == null)
                return null;

            // Return the player name that we found through uuid.
            return offlinePlayer.getName();
        });
    }

    /**
     * Get the UUID of a player. This call can be potentially be blocking since we access Mojang API, so make sure to
     * run this off the main thread!
     *
     * @param playerName Name of the player
     * @return UUID of the player or null if nothing could be found.
     */
    protected CompletableFuture<UUID> getUUID(String playerName) {

        return CompletableFuture.supplyAsync(() -> {
            // First check the stored UUIDs to see if we can find anything in the local storage.
            UUID uuid = getStoredUUID(playerName);

            // UUID is not null, so return it happily.
            if (uuid != null) {
                return uuid;
            }

            // The search continues..
            // Use Bukkit to find the UUID.
            // Potentially blocking!
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

            // We found a match!
            if (offlinePlayer.getName() == null) {
                return offlinePlayer.getUniqueId();
            }

            // We found nothing at all - return null.
            return null;
        });
    }

    /**
     * Get the UUID that is stored in the local storage for the given name.
     *
     * @param playerName Name to use for finding the UUID.
     * @return UUID that matches this player name or null if none was found.
     */
    protected UUID getStoredUUID(String playerName) {

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

    /**
     * Check if a UUID is stored in the local storage of Autorank.
     *
     * @param uuid UUID to check
     * @return true if it stored, false if not.
     */
    protected boolean isStored(final UUID uuid) {
        return getStoredUsername(uuid) != null;
    }

    /**
     * Check to see if a stored playername might be outdated.
     *
     * @param playerName Name of the player to check.
     * @return true if the playername is outdated, false otherwise.
     */
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

    /**
     * Store a playername and UUID combination. If the UUID already exists for a playername, the playername will be
     * overwritten.
     *
     * @param playerName Name of the player.
     * @param uuid       UUID of the player.
     * @return True if the UUID could be stored. False if it couldn't be stored.
     */
    public CompletableFuture<Boolean> storeUUID(String playerName, final UUID uuid) {

        final String lowerCasePlayerName = playerName.toLowerCase();

        return CompletableFuture.supplyAsync(() -> {

            // Don't look them up.
            if (!isOutdated(lowerCasePlayerName)) {
                plugin.debugMessage("Not refreshing user " + lowerCasePlayerName + " because it's up-to-date.");
                return true;
            }

            FileConfiguration config;

            // Remove old name and uuid because apparently name was changed.
            if (isStored(uuid)) {

                // Change name to new name
                final String oldUser = getStoredUsername(uuid);

                if (oldUser != null) {
                    // Change config pointer to correct config
                    config = findCorrectConfig(oldUser);

                    // Name didn't change, it was just out of date.
                    if (oldUser.equalsIgnoreCase(lowerCasePlayerName)) {
                        // Don't do anything besides updating updateTime.
                        config.set(lowerCasePlayerName + ".updateTime", System.currentTimeMillis());

                        plugin.debugMessage("Refreshed user '" + playerName + "' with uuid " + uuid + "!");
                        return true; // Do not do anything else.
                    }

                    // Remove the old user as it correct anymore.
                    plugin.debugMessage("Deleting old user '" + oldUser + "'!");
                    config.set(oldUser, null);
                }
            }

            config = findCorrectConfig(lowerCasePlayerName);

            // Couldn't find a config for the username! (That's a bit odd though).
            if (config == null) {
                plugin.debugMessage("Could not store uuid " + uuid.toString() + " of player " + lowerCasePlayerName);
                return false;
            }

            config.set(lowerCasePlayerName + ".uuid", uuid.toString());
            config.set(lowerCasePlayerName + ".updateTime", System.currentTimeMillis());

            // Look for the real name so we can easily store it.
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            String realName = offlinePlayer.getName();

            // Couldn't find a real name, so try to look it up.
            if (realName == null) {
                try {
                    realName = UUIDManager.getPlayerName(uuid).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            // We found a real name, so store it as well.
            // The real name is the name of the player with proper capitalisation.
            // The real name is useful for leaderboards.
            config.set(lowerCasePlayerName + ".realName", realName);

            plugin.debugMessage("Stored user '" + playerName + "' with uuid " + uuid + "!");
            return true;
        });
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

    /**
     * Get all the player names that are stored in the local UUID storage of Autorank.
     *
     * @return a list of player names stored.
     */
    public List<String> getStoredPlayerNames() {
        // Return all playernames that are stored in the UUID folders

        List<String> playerNames = new ArrayList<>();

        for (Entry<String, FileConfiguration> entry : this.configs.entrySet()) {
            FileConfiguration config = entry.getValue();

            playerNames.addAll(config.getKeys(false));
        }

        return playerNames;
    }

}
