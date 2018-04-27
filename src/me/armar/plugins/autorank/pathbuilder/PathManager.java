package me.armar.plugins.autorank.pathbuilder;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.config.PlayerDataConfig;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Handles all things that have to do with paths checking
 * <p>
 * Date created: 16:32:49 5 aug. 2015
 *
 * @author Staartvin
 */
public class PathManager {

    private final Autorank plugin;
    private PathBuilder builder;
    // A list of paths any player is able to take
    private List<Path> paths = new ArrayList<Path>();
    // PlayerDataConfig keeps track of what paths are active for a player.
    private PlayerDataConfig playerDataConfig;

    public PathManager(final Autorank plugin) {
        this.plugin = plugin;
        setBuilder(new PathBuilder(plugin));

        // Initialize player data config.
        playerDataConfig = new PlayerDataConfig(plugin);

        playerDataConfig.loadConfig();
    }

    /**
     * Return a list of messages that represents debug information of the paths.
     *
     * @return a list of strings.
     */
    public List<String> debugPaths() {

        final List<String> messages = new ArrayList<String>();

        messages.add(" ------------------- Path debug info ------------------- ");

        for (Path path : paths) {
            String pathName = path.getInternalName();
            List<CompositeRequirement> requirements = path.getRequirements();
            List<CompositeRequirement> prerequisites = path.getPrerequisites();
            List<AbstractResult> abstractResults = path.getResults();

            int count = 1;

            messages.add("Path: " + pathName);

            messages.add("Display name: " + path.getDisplayName());

            messages.add("Prerequisites: ");

            for (CompositeRequirement prereq : prerequisites) {
                messages.add("    " + count + ". " + prereq.getDescription());
                count++;
            }

            // Reset count again
            count = 1;

            messages.add("Requirements: ");

            for (CompositeRequirement req : requirements) {
                messages.add("    " + count + ". " + req.getDescription());
                count++;
            }

            // Reset count again
            count = 1;

            messages.add("Results: ");

            for (AbstractResult res : abstractResults) {
                messages.add("    " + count + ". " + res.getDescription());
                count++;
            }

            messages.add("----------------------------");

        }

        return messages;
    }

    public PathBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(final PathBuilder builder) {
        this.builder = builder;
    }

    /**
     * Get paths that are active for a player
     *
     * @param uuid UUID of the player
     * @return paths that a player has chosen and is currently on.
     */
    public List<Path> getActivePaths(UUID uuid) {
        Collection<String> activePathNames = this.playerDataConfig.getActivePaths(uuid);

        List<Path> activePaths = new ArrayList<>();

        for (String activePathName : activePathNames) {
            Path activePath = findPathByInternalName(activePathName, false);

            if (activePath == null) {
                continue;
            }

            activePaths.add(activePath);
        }

        return activePaths;
    }

    /**
     * Check whether a player has a path set as active.
     *
     * @param uuid UUID of the player
     * @param path Path to check
     * @return true if the given path is active for the given player. False otherwise.
     */
    public boolean hasActivePath(UUID uuid, Path path) {
        return getActivePaths(uuid).contains(path);
    }

    /**
     * Reset the completed requirements of all active paths of a player.
     *
     * @param uuid UUID of the player.
     */
    public void resetProgressOnActivePaths(UUID uuid) {
        this.playerDataConfig.setActivePaths(uuid, new ArrayList<>());
    }

    /**
     * Remove all active paths a player currently has.
     *
     * @param uuid UUID of the player
     */
    public void resetActivePaths(UUID uuid) {
        for (String activePathName : this.playerDataConfig.getActivePaths(uuid)) {
            this.playerDataConfig.removeActivePath(uuid, activePathName);
        }
    }

    /**
     * Get the paths that a player has completed. A path has been completed when all requirements have been met by
     * the player. Note that completing a path does not mean that a player cannot do the path again, as some paths
     * are repeatable.
     *
     * @param uuid UUID of the player
     * @return a list of paths a player has completed.
     */
    public List<Path> getCompletedPaths(UUID uuid) {
        Collection<String> completedPathsNames = this.playerDataConfig.getCompletedPaths(uuid);

        List<Path> completedPaths = new ArrayList<>();

        for (String completedPathName : completedPathsNames) {
            Path completedPath = this.findPathByInternalName(completedPathName, false);

            if (completedPath == null) {
                continue;
            }

            completedPaths.add(completedPath);
        }

        return completedPaths;
    }

    /**
     * Remove all completed paths of a player.
     *
     * @param uuid UUID of the player
     */
    public void resetCompletedPaths(UUID uuid) {
        for (String completedPath : this.playerDataConfig.getCompletedPaths(uuid)) {
            this.playerDataConfig.removeCompletedPath(uuid, completedPath);
        }
    }


    /**
     * Add a completed requirement for a path for a player.
     *
     * @param uuid  UUID of the player
     * @param path  Path
     * @param reqId Id of the requirement.
     */
    public void addCompletedRequirement(UUID uuid, Path path, int reqId) {
        this.playerDataConfig.addCompletedRequirement(uuid, path.getInternalName(), reqId);
    }

    /**
     * Check whether a player has completed a requirement for a path.
     *
     * @param uuid  UUID of the player
     * @param path  Path to check
     * @param reqId Id of the requirement
     * @return true if the player has completed the requirement, false otherwise.
     */
    public boolean hasCompletedRequirement(UUID uuid, Path path, int reqId) {
        return this.playerDataConfig.hasCompletedRequirement(uuid, path.getInternalName(), reqId);
    }

    /**
     * Check whether a player has completed a path.
     *
     * @param uuid UUID of the player
     * @param path Path to check
     * @return true when the player has completed a path, false otherwise.
     */
    public boolean hasCompletedPath(UUID uuid, Path path) {
        return getCompletedPaths(uuid).contains(path);
    }

    /**
     * Get a list of all paths that are defined in the paths.yml file.
     *
     * @return a list of {@link Path} objects.
     */
    public List<Path> getAllPaths() {
        return Collections.unmodifiableList(paths);
    }

    /**
     * Get paths that a player can choose. A player can choose a path if he
     * meets the prerequisites of that path.
     *
     * @param player Player
     * @return List of paths that a player can choose.
     */
    public List<Path> getEligiblePaths(Player player) {
        List<Path> possibilities = new ArrayList<>();

        for (Path path : this.getAllPaths()) {

            // The path is not eligible, so skip it.
            if (!isPathEligible(player, path)) {
                continue;
            }

            // Add path to possibilities, if player meets all prerequisites
            possibilities.add(path);
        }

        return possibilities;
    }

    /**
     * Check whether a path is eligible for a player. A path is eligible for a player if all of the following apply:
     * <ul>
     * <li>The path is not active for the player.</li>
     * <li>The player has not completed the path yet, or the path is repeatable.</li>
     * <li>The player meets the prerequisites of the path.</li>
     * </ul>
     *
     * @param player Player to check
     * @param path   Path to check
     * @return true if the path is eligible for the given player.
     */
    public boolean isPathEligible(Player player, Path path) {
        // A path is not eligible when a player has already has it as active.
        if (hasActivePath(player.getUniqueId(), path)) {
            return false;
        }

        // If a path has been completed and cannot be repeated, the player cannot take this path again.
        if (hasCompletedPath(player.getUniqueId(), path) && !path.isRepeatable()) {
            return false;
        }

        // If a path does not meet the prerequisites of a path, the player cannot take the path.
        return path.meetsPrerequisites(player);
    }

    /**
     * Initialise paths from paths.yml file.
     */
    public void initialiseFromConfigs() {

        // Clear before starting
        paths.clear();

        List<Path> temp = builder.initialisePaths();

        if (temp == null) {
            plugin.getLogger().warning("The paths file was not configured correctly! See your log file for more info.");
            return;
        } else {
            paths = temp;
        }

        // Output paths in the console if debug is turned on.
        for (final String message : debugPaths()) {
            plugin.debugMessage(message);
        }

    }

    /**
     * Get the path that corresponds to the display name.
     *
     * @param displayName     The display name of the path
     * @param isCaseSensitive true if we only match paths that have the exact wording,
     *                        taking into account case sensitivity.
     * @return matching path or null if none found.
     */
    public Path findPathByDisplayName(String displayName, boolean isCaseSensitive) {
        for (final Path path : this.getAllPaths()) {

            if (isCaseSensitive) {
                if (path.getDisplayName().equals(displayName)) {
                    return path;
                }
            } else {
                if (path.getDisplayName().equalsIgnoreCase(displayName)) {
                    return path;
                }
            }
        }

        return null;
    }

    /**
     * Get the path that corresponds to the given internal name.
     *
     * @param internalName    The internal name of the path
     * @param isCaseSensitive true if we only match paths that have the exact wording,
     *                        taking into account case sensitivity.
     * @return matching path or null if none found.
     */
    public Path findPathByInternalName(String internalName, boolean isCaseSensitive) {
        for (final Path path : this.getAllPaths()) {

            if (isCaseSensitive) {
                if (path.getInternalName().equals(internalName)) {
                    return path;
                }
            } else {
                if (path.getInternalName().equalsIgnoreCase(internalName)) {
                    return path;
                }
            }
        }

        return null;
    }

    /**
     * Assign a path to a player. This means that the path is now set to active for a player. A path can only be
     * assigned to the player if it is eligible ({@link #isPathEligible(Player, Path)}) for the player.
     *
     * @param player Player to assign path to
     * @param path   Path to check
     * @throws IllegalArgumentException if the path is not eligible (see {@link #isPathEligible(Player, Path)}).
     */
    public void assignPath(Player player, Path path) throws IllegalArgumentException {

        if (!isPathEligible(player, path)) {
            throw new IllegalArgumentException("Path is not eligible, so cannot be assigned to the player!");
        }

        UUID uuid = player.getUniqueId();

        String internalName = path.getInternalName();

        // Add path as an active path.
        this.playerDataConfig.addActivePath(player.getUniqueId(), internalName);

        // Reset progress of requirements
        this.playerDataConfig.setCompletedRequirements(player.getUniqueId(), internalName, new ArrayList<>());

        // Reset progress of prerequisites for a path.
        this.playerDataConfig.setCompletedPrerequisites(uuid, internalName, new ArrayList<>());

        // Perform results upon choosing this path.
        path.performResultsUponChoosing(player);
    }

    /**
     * Try to automatically assign paths to a player.
     *
     * @param player Player to assign paths to.
     * @return a list of paths that have been been assigned to the player.
     */
    public List<Path> autoAssignPaths(Player player) {

        plugin.debugMessage("Trying to assign paths to " + player.getName());

        List<Path> assignedPaths = new ArrayList<>();

        for (Path path : getAllPaths()) {
            if (isPathEligible(player, path) && path.isAutomaticallyAssigned()) {
                assignPath(player, path);

                plugin.debugMessage("Assigned " + path.getDisplayName() + " to " + player.getName());

                // Send message to player
                player.sendMessage(Lang.AUTOMATICALLY_ASSIGNED_PATH.getConfigValue(path.getDisplayName()));

                assignedPaths.add(path);
            }
        }

        return assignedPaths;
    }

    /**
     * Complete a path for a player and run the results of the path.
     *
     * @param path   Path to complete
     * @param player Player to complete it for
     * @return true when all results of the given path are executed successfully.
     */
    public boolean completePath(Path path, Player player) {

        // Add progress of completed requirements
        this.playerDataConfig.addCompletedPath(player.getUniqueId(), path.getInternalName());

        // Remove path from active paths.
        this.playerDataConfig.removeActivePath(player.getUniqueId(), path.getInternalName());

        // Perform results on completion
        boolean result = path.performResults(player);

        // Try to assign new paths to a player
        plugin.getPathManager().autoAssignPaths(player);

        return result;
    }

    public void setLeaderboardExemption(UUID uuid, boolean value) {
        this.playerDataConfig.hasLeaderboardExemption(uuid, value);
    }

    /**
     * Check whether a player is exempted from the leaderboard or not.
     *
     * @param uuid UUID of the player
     * @return true if the player is exempted from the leaderboard.
     */
    public boolean hasLeaderboardExemption(UUID uuid) {
        return this.playerDataConfig.hasLeaderboardExemption(uuid);
    }
}
