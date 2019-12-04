package me.armar.plugins.autorank.pathbuilder;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.builders.PathBuilder;
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
    private me.armar.plugins.autorank.pathbuilder.builders.PathBuilder builder;
    // A list of paths any player is able to take
    private List<Path> paths = new ArrayList<Path>();
    // PlayerDataConfig keeps track of what paths are active for a player.
    private PlayerDataConfig playerDataConfig;

    public PathManager(final Autorank plugin) {
        this.plugin = plugin;
        setBuilder(new me.armar.plugins.autorank.pathbuilder.builders.PathBuilder(plugin));

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

    public me.armar.plugins.autorank.pathbuilder.builders.PathBuilder getBuilder() {
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
     * Reset the completed requirements of all active paths of a player.
     *
     * @param uuid UUID of the player.
     */
    public void resetProgressOnActivePaths(UUID uuid) {
        this.playerDataConfig.setActivePaths(uuid, new ArrayList<>());
    }

    /**
     * Reset the progress of a player for a given path. It will reset both the completed requirements and
     * prerequisites of the given path.
     *
     * @param uuid UUID of the player
     * @param path Path to reset progress of.
     */
    public void resetProgressOfPath(UUID uuid, Path path) {
        this.playerDataConfig.setCompletedPrerequisites(uuid, path.getInternalName(), new ArrayList<>());
        this.playerDataConfig.setCompletedRequirements(uuid, path.getInternalName(), new ArrayList<>());
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
            if (!path.isEligible(player)) {
                continue;
            }

            // Add path to possibilities, if player meets all prerequisites
            possibilities.add(path);
        }

        return possibilities;
    }



    /**
     * Initialise paths from paths.yml file.
     */
    public void initialiseFromConfigs() {

        // Clear before starting
        paths.clear();

        List<Path> temp = builder.initialisePaths();

        if (temp == null) {
            plugin.getLogger().warning("The paths file was not configured correctly! See your log file for more " +
                    "info.");
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
     * assigned to the player if it is eligible ({@link Path#isEligible(Player)}) for the player.
     *
     * @param player Player to assign path to
     * @param path   Path to check
     * @throws IllegalArgumentException if the path is not eligible (see {@link Path#isEligible(Player)}).
     */
    public void assignPath(Player player, Path path) throws IllegalArgumentException {

        if (!path.isEligible(player)) {
            throw new IllegalArgumentException("Path is not eligible, so cannot be assigned to the player!");
        }

        UUID uuid = player.getUniqueId();

        String internalName = path.getInternalName();

        // Add path as an active path.
        this.playerDataConfig.addActivePath(player.getUniqueId(), internalName);

        // Only reset progress if there was no progress stored.
        if (!path.shouldStoreProgressOnDeactivation()) {
            // Reset progress of requirements
            this.playerDataConfig.setCompletedRequirements(player.getUniqueId(), internalName, new ArrayList<>());

            // Reset progress of prerequisites for a path.
            this.playerDataConfig.setCompletedPrerequisites(uuid, internalName, new ArrayList<>());
        }

        // Perform results upon choosing this path.
        path.performResultsUponChoosing(player);
    }

    /**
     * Deassign a path from a player. Depending on the property of {@link Path#shouldStoreProgressOnDeactivation()}
     * the progress of the player for the path gets stored or not. The path is set to inactive.
     *
     * @param uuid UUID of the player
     * @param path Path to deactivate.
     */
    public void deassignPath(UUID uuid, Path path) {

        // We can't deassign a path if it is not active.
        if (!path.isActive(uuid)) {
            return;
        }

        // Remove progress of a player if necessary
        if (!path.shouldStoreProgressOnDeactivation()) {
            plugin.getPathManager().resetProgressOfPath(uuid, path);
        }

        // Remove active path of a player.
        this.playerDataConfig.removeActivePath(uuid, path.getInternalName());
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
            if (path.isEligible(player) && path.isAutomaticallyAssigned()) {

                // If the path is deactivated, we will not automatically assign the path to the player again.
                // If we did, the player would constantly need to deactivate the path again.
                if (path.isDeactivated(player.getUniqueId())) {
                    continue;
                }

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

    /**
     * Get the storage of player data regarding paths and requirements.
     *
     * @return {@link PlayerDataConfig} object.
     */
    protected PlayerDataConfig getPlayerDataStorage() {
        return this.playerDataConfig;
    }
}
