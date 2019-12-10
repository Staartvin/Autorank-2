package me.armar.plugins.autorank.api;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <b>Autorank's API class:</b>
 * <p>
 * You, as a developer, can you use this class to get storage from players or storage
 * about paths. The API is never finished and if you want to see something
 * added, tell us!
 * <p>
 *
 * @author Staartvin
 */
public class API {

    private final Autorank plugin;

    public API(final Autorank instance) {
        plugin = instance;
    }

    /**
     * Get the Addon manager of Autorank.
     * <p>
     * This class stores information about the loaded addons
     *
     * @return {@linkplain AddOnManager} class
     */
    public AddOnManager getAddOnManager() {
        return plugin.getAddonManager();
    }

    /**
     * Get the global play time (playtime across all servers with the same MySQL
     * database linked) of a player.
     * <p>
     *
     * @param uuid UUID of the player
     * @return play time of a player. 0 if no entry was found.
     */
    public CompletableFuture<Integer> getGlobalPlayTime(final UUID uuid) {
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.DATABASE)) {
            return CompletableFuture.completedFuture(0);
        }

        return plugin.getStorageManager().getStorageProvider(StorageProvider.StorageType.DATABASE).getPlayerTime
                (TimeType.TOTAL_TIME, uuid);
    }

    /**
     * Get the local play time of this player on this server according to
     * Autorank (in minutes).<br>
     * This method will grab the time from the internal storage used by Autorank
     * and so this time does not depend on other plugins.
     *
     * @param uuid UUID of the player
     * @return play time of this player or 0 if not found.
     */
    public CompletableFuture<Integer> getLocalPlayTime(final UUID uuid) {
        return getPlayTime(TimeType.TOTAL_TIME, uuid);
    }

    /**
     * Get the play time of a player for a given type of time (daily, weekly, monthly).
     *
     * @param timeType Type of time
     * @param uuid     UUID of the player
     * @return play time of a player (in minutes).
     */
    public CompletableFuture<Integer> getPlayTime(TimeType timeType, UUID uuid) {
        return plugin.getStorageManager().getPrimaryStorageProvider().getPlayerTime(timeType, uuid);
    }

    /**
     * Get the local play time (play time on this server) of a player. The
     * returned time depends on what plugin is used for keeping track of time.
     * <br>
     * The time is always given in seconds.
     * <p>
     * <p>
     * Deprecated, use {@link #getTimeOfPlayer(UUID)} instead.
     *
     * @param playerName Name of the player
     * @return play time of a player. 0 when has never played before.
     */
    @Deprecated
    public int getTimeOfPlayer(@NonNull String playerName) {

        UUID uuid = null;

        try {
            uuid = UUIDManager.getUUID(playerName).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return this.getTimeOfPlayer(uuid);
    }

    public int getTimeOfPlayer(@NonNull UUID uuid) {
        return plugin.getPlayTimeManager().getTimeOfPlayer(uuid, true);
    }

    /**
     * Register a requirement that can be used in the Paths.yml file. The name
     * should be unique as that is the way Autorank will identify the
     * requirement.
     * <p>
     * The name will be the name that is used in the config.
     *
     * @param uniqueName Unique name identifier for the requirement
     * @param clazz      AbstractRequirement class that does all the logic
     */
    public void registerRequirement(final String uniqueName, final Class<? extends AbstractRequirement> clazz) {
        plugin.getLogger().info("Loaded custom requirement: " + uniqueName);

        plugin.registerRequirement(uniqueName, clazz);
    }

    /**
     * Register a result that can be used in the Paths.yml file. The name should
     * be unique as that is the way Autorank will identify the result.
     * <p>
     * The name will be the name that is used in the config.
     *
     * @param uniqueName Unique name identifier for the result
     * @param clazz      AbstractResult class that does all the logic
     */
    public void registerResult(final String uniqueName, final Class<? extends AbstractResult> clazz) {
        plugin.getLogger().info("Loaded custom result: " + uniqueName);

        plugin.registerResult(uniqueName, clazz);
    }

    /**
     * Get the active paths of a player. Returns empty list if no paths are active.
     *
     * @param uuid UUID of the player
     * @return List of {@link Path} objects or empty if player has no active path.
     */
    public List<Path> getActivePaths(UUID uuid) {
        return plugin.getPathManager().getActivePaths(uuid);
    }

    /**
     * Get the paths that a player completed.
     *
     * @param uuid UUID of the player
     * @return a list of {@link Path} objects that corresponds to the paths that
     * have been completed.
     */
    public List<Path> getCompletedPaths(UUID uuid) {
        return plugin.getPathManager().getCompletedPaths(uuid);
    }

    /**
     * Get all the paths that a player is allowed to start.
     *
     * @param uuid Player to check paths for.
     * @return a list of paths the player is able to start.
     */
    public List<Path> getEligiblePaths(UUID uuid) {
        return plugin.getPathManager().getEligiblePaths(uuid);
    }

    /**
     * Get the path with the given name. Note that this can either be the display name of the path (as shown to
     * players) or the internal name (as provided in the configuration file of Autorank).
     *
     * @param pathName Name of the path.
     * @return {@link Path} object if found, otherwise null.
     */
    public Path getPath(String pathName) {
        return plugin.getPathManager().getAllPaths().parallelStream()
                .filter(path -> path.getDisplayName().equalsIgnoreCase(pathName) || path.getInternalName().equalsIgnoreCase(pathName))
                .findFirst().orElse(null);
    }
}
