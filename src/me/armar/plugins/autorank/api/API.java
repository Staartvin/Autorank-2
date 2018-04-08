package me.armar.plugins.autorank.api;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.storage.flatfile.FlatFileManager.TimeType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
     * Get all {@linkplain RequirementsHolder}s for a player at the exact
     * moment. This does not consider already finished requirement but just
     * mirrors the Paths.yml file.
     *
     * @param player Player to get the requirements from.
     * @return a list of {@linkplain RequirementsHolder}s; An empty list when
     * none are found.
     */
    public List<RequirementsHolder> getAllRequirements(final Player player) {
        return plugin.getPlayerChecker().getAllRequirementsHolders(player);
    }

    /**
     * Get all {@linkplain RequirementsHolder}s that are not yet completed.
     *
     * @param player Player to get the failed requirements for.
     * @return list of {@linkplain RequirementsHolder}s that still have to be
     * completed.
     */
    public List<RequirementsHolder> getFailedRequirements(final Player player) {
        return plugin.getPlayerChecker().getFailedRequirementsHolders(player);
    }

    /**
     * Get all {@linkplain RequirementsHolder}s that the player has already completed.
     * If the player does not have a current path, it will return an empty list.
     *
     * @param player Player to get completed requirements for.
     * @return a list of completed requirements.
     */
    public List<RequirementsHolder> getCompletedRequirements(final Player player) {
        return plugin.getPlayerChecker().getCompletedRequirementsHolders(player);
    }

    /**
     * Get the global play time (playtime across all servers with the same MySQL
     * database linked) of a player.
     * <p>
     *
     * @param uuid UUID of the player
     * @return play time of a player. 0 if no entry was found.
     */
    public int getGlobalPlayTime(final UUID uuid) {
        return plugin.getMySQLManager().getGlobalTime(uuid);
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
    public int getLocalPlayTime(final UUID uuid) {
        return plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid);
    }

    /**
     * Get the MySQL database name Autorank stores its global times in.
     *
     * @return name of database
     */
    public String getMySQLDatabase() {
        return plugin.getMySQLManager().getDatabaseName();
    }

    /**
     * Get the local play time (play time on this server) of a player. The
     * returned time depends on what plugin is used for keeping track of time.
     * <br>
     * The time is always given in seconds.
     * <p>
     *
     * @param player Player to get the time for
     * @return play time of a player. 0 when has never played before.
     */
    public int getTimeOfPlayer(final Player player) {
        return plugin.getPlaytimes().getTimeOfPlayer(player.getName(), true);
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
     * Get the active path of a player. Returns null if player has no path.
     *
     * @param uuid UUID of the player
     * @return {@link Path} object or null if player has no active path.
     */
    public Path getActivePath(UUID uuid) {
        return plugin.getPathManager().getCurrentPath(uuid);
    }

    /**
     * Get the paths that a player completed.
     *
     * @param uuid UUID of the player
     * @return a list of {@link Path} objects that corresponds to the paths that
     * have been completed.
     */
    public List<Path> getCompletedPaths(UUID uuid) {
        List<String> completedPathsString = plugin.getPlayerDataConfig().getCompletedPaths(uuid);

        List<Path> completedPaths = new ArrayList<>();

        for (String pathString : completedPathsString) {
            Path path = plugin.getPathManager().matchPathbyInternalName(pathString, false);

            if (path != null) {
                completedPaths.add(path);
            }
        }

        return completedPaths;
    }

    /**
     * Get the paths that a player has started but not yet completed.
     *
     * @param uuid UUID of the player
     * @return a list of {@link Path} objects
     */
    public List<Path> getStartedPaths(UUID uuid) {
        List<String> startedPathsString = plugin.getPlayerDataConfig().getStartedPaths(uuid);

        List<Path> startedPaths = new ArrayList<>();

        for (String pathString : startedPathsString) {
            Path path = plugin.getPathManager().matchPathbyInternalName(pathString, false);

            if (path != null) {
                startedPaths.add(path);
            }
        }

        return startedPaths;
    }

    /**
     * Get all the paths that a player is allowed to start.
     *
     * @param player Player to check paths for.
     * @return a list of paths the player is able to start.
     */
    public List<Path> getEligiblePaths(Player player) {
        return plugin.getPathManager().getEligiblePaths(player);
    }
}
