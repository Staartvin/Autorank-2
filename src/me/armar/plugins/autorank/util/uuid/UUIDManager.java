package me.armar.plugins.autorank.util.uuid;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This class allows developers to easily get UUIDs from names <br>
 * or to do the reverse. <br>
 * It has an implemented cache system, which makes sure it doesn't do a lookup
 * <br>
 * when not needed.
 * <p>
 * Date created: 17:13:57 2 apr. 2014
 *
 * @author Staartvin
 */
public class UUIDManager {

    // Whether to use cache or not
    private static final boolean useCache = true;
    //    private static Map<UUID, String> playerNameCache = new HashMap<UUID, String>(); // Store names that were
    //    looked up
//    private static Map<String, UUID> uuidCache = new HashMap<String, UUID>(); // Store UUIDs that were looked up.
    private static final Autorank plugin;

    static {
        plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
    }

    /**
     * Get the player name that belongs the given UUID.
     *
     * @param uuid UUID to get the playername for.
     * @return the name of the player it belongs to, or null if none was found.
     */
    public static CompletableFuture<String> getPlayerName(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {

            if (uuid == null) {
                return null;
            }

            try {
                Map<UUID, String> names = getPlayerNames(Collections.singletonList(uuid)).get();

                for (Map.Entry<UUID, String> entry : names.entrySet()) {
                    return entry.getValue();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return null;
        });
    }


    /**
     * See {@link #getPlayerName(UUID)}, but then for a lot of uuids at once.
     *
     * @param uuids List of UUIDs to check
     * @return Map of key-value pairs where the key corresponds to a UUID and the value is the corresponding player
     * name.
     */
    public static CompletableFuture<Map<UUID, String>> getPlayerNames(final List<UUID> uuids) {

        return CompletableFuture.supplyAsync(() -> {

            List<UUID> uuidsToSearch = new ArrayList<>(uuids);

            Map<UUID, String> cachedData = new HashMap<>();

            // First look if we have playernames that are stored and we can use.
            for (UUID uuid : uuids) {

                String playerName = null;
                try {
                    playerName = plugin.getUUIDStorage().getUsername(uuid).get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore the trace, continue looking.
                    continue;
                }

                if (playerName == null) {
                    continue;
                }

                // We found a stored player name so we put that in the cached data.
                cachedData.put(uuid, playerName);
                // Remove this uuid, because we don't have to search for it anymore.
                uuidsToSearch.remove(uuid);

            }

            // For all left-overs, use the fetcher.
            if (!uuids.isEmpty()) {
                NameFetcher fetcher = new NameFetcher(uuidsToSearch);

                Map<UUID, String> response = null;

                try {
                    response = fetcher.call();

                    // Store all looked-up names in the data.
                    cachedData.putAll(response);
                } catch (final Exception e) {
                    if (e instanceof IOException) {
                        Bukkit.getLogger().warning("Tried to contact Mojang page for UUID lookup but failed.");
                    }
                    e.printStackTrace();
                }
            }

            // Before returning, make sure to cache all data.
            //uuidCache.putAll(cachedData);

            // Store retrieved data in UUID storage so we can re-use it.
//            cachedData.forEach((key, value) -> plugin.getUUIDStorage().storeUUID(value, key));

            return cachedData;
        });
    }

    /**
     * Get the UUID corresponding to the given player name.
     *
     * @param playerName Name of the player
     * @return UUID of the player or null if none was found.
     */
    public static CompletableFuture<UUID> getUUID(final String playerName) {
        return CompletableFuture.supplyAsync(() -> {

            if (playerName == null) {
                return null;
            }

            try {
                Map<String, UUID> uuids = getUUIDs(Collections.singletonList(playerName)).get();

                for (Map.Entry<String, UUID> entry : uuids.entrySet()) {
                    return entry.getValue();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return null;
        });
    }


    /**
     * See {@link #getUUID(String)}, but then for a lot of player names at once.
     *
     * @param playerNames List of player names to check
     * @return Map of key-value pairs where the key corresponds to a player name and the value is the corresponding
     * UUID.
     */
    public static CompletableFuture<Map<String, UUID>> getUUIDs(final List<String> playerNames) {

        return CompletableFuture.supplyAsync(() -> {

            List<String> playerNamesToSearch = new ArrayList<>(playerNames);

            Map<String, UUID> cachedData = new HashMap<>();

            // First look if we have uuids that are stored and we can use.
            for (String playerName : playerNames) {

                UUID storedUUID = null;
                try {
                    storedUUID = plugin.getUUIDStorage().getUUID(playerName).get();
                } catch (InterruptedException | ExecutionException e) {
                    // Ignore the trace, continue looking.
                    continue;
                }

                if (storedUUID == null) {
                    continue;
                }

                // We found a stored UUID so we put that in the cached data.
                cachedData.put(playerName, storedUUID);
                // Remove this player name, because we don't have to search for it anymore.
                playerNamesToSearch.remove(playerName);

            }

            // For all left-overs, use the fetcher.
            if (!playerNamesToSearch.isEmpty()) {
                UUIDFetcher fetcher = new UUIDFetcher(playerNamesToSearch);

                Map<String, UUID> response = null;

                try {
                    response = fetcher.call();

                    // Store all looked-up names in the data.
                    cachedData.putAll(response);

                } catch (final Exception e) {
                    if (e instanceof IOException) {
                        Bukkit.getLogger().warning("Tried to contact Mojang page for UUID lookup but failed.");
                    }
                    e.printStackTrace();
                }
            }

            // Before returning, make sure to cache all data.
            //uuidCache.putAll(cachedData);

            // Store retrieved data in UUID storage so we can re-use it.
//            cachedData.forEach((key, value) -> plugin.getUUIDStorage().storeUUID(key, value));

            return cachedData;
        });
    }


}
