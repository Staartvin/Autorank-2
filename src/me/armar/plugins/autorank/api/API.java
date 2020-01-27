package me.armar.plugins.autorank.api;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.api.services.RequirementManager;
import me.armar.plugins.autorank.api.services.RequirementService;
import me.armar.plugins.autorank.api.services.ResultManager;
import me.armar.plugins.autorank.api.services.ResultService;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import org.bukkit.plugin.ServicePriority;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

        // Register requirement service.
        plugin.getServer().getServicesManager().register(RequirementManager.class, new RequirementService(instance),
                instance, ServicePriority.Normal);

        plugin.getLogger().info("Registered requirement service for adding custom requirements!");

        // Register result service.
        plugin.getServer().getServicesManager().register(ResultManager.class, new ResultService(instance),
                instance, ServicePriority.Normal);

        plugin.getLogger().info("Registered result service for adding custom results!");
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
        if (!plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            return CompletableFuture.completedFuture(0);
        }

        return plugin.getPlayTimeStorageManager().getStorageProvider(PlayTimeStorageProvider.StorageType.DATABASE).getPlayerTime
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
     * Get the play time of a player for a given type of time (daily, weekly, monthly). For info on the different
     * types of play time, see {@link me.armar.plugins.autorank.playtimes.PlayTimeManager}.
     *
     * @param timeType Type of time
     * @param uuid     UUID of the player
     * @return play time of a player (in minutes).
     * @deprecated You should use {@link #getPlayTime(TimeType, UUID, TimeUnit)} instead.
     */
    @Deprecated
    public CompletableFuture<Integer> getPlayTime(TimeType timeType, UUID uuid) {
        return plugin.getPlayTimeManager().getPlayTime(timeType, uuid);
    }

    /**
     * Get the play time of a player for a given type of time (daily, weekly, monthly). For info on the different
     * types of play time, see {@link me.armar.plugins.autorank.playtimes.PlayTimeManager}.
     *
     * @param timeType Type of time
     * @param uuid     UUID of the player
     * @param timeUnit Unit of time that should be returned
     * @return play time of a player (in given unit).
     */
    public CompletableFuture<Long> getPlayTime(TimeType timeType, UUID uuid, TimeUnit timeUnit) {
        return plugin.getPlayTimeManager().getPlayTime(timeType, uuid, timeUnit);
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
