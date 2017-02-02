package me.armar.plugins.autorank.pathbuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.result.Result;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * Handles all things that have to do with paths checking
 * <p>
 * Date created: 16:32:49 5 aug. 2015
 * 
 * @author Staartvin
 * 
 */
public class PathManager {

    private PathBuilder builder;
    // A list of paths any player is able to take
    private List<Path> paths = new ArrayList<Path>();

    private final Autorank plugin;

    public PathManager(final Autorank plugin) {
        this.plugin = plugin;
        setBuilder(new PathBuilder(plugin));
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
            List<RequirementsHolder> requirements = path.getRequirements();
            List<RequirementsHolder> prerequisites = path.getPrerequisites();
            List<Result> results = path.getResults();

            int count = 1;

            messages.add("Path: " + pathName);

            messages.add("Display name: " + path.getDisplayName());

            messages.add("Prerequisites: ");

            for (RequirementsHolder prereq : prerequisites) {
                messages.add("    " + count + ". " + prereq.getDescription());
                count++;
            }

            // Reset count again
            count = 1;

            messages.add("Requirements: ");

            for (RequirementsHolder req : requirements) {
                messages.add("    " + count + ". " + req.getDescription());
                count++;
            }

            // Reset count again
            count = 1;

            messages.add("Results: ");

            for (Result res : results) {
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

    /**
     * Get the path that the player is currently on.
     * 
     * @param uuid
     *            UUID of the player
     * @return path of the player, or null if not found.
     */
    public Path getCurrentPath(UUID uuid) {
        String chosenPath = plugin.getPlayerDataConfig().getChosenPath(uuid);

        // Unknown path, so return null
        if (chosenPath.equalsIgnoreCase("unknown")) {
            return null;
        }

        return this.matchPathbyInternalName(chosenPath, true);
    }

    /**
     * Get a list of all paths that are defined in the paths.yml file.
     * 
     * @return a list of {@link Path} objects.
     */
    public List<Path> getPaths() {
        return new ArrayList<Path>(paths);
    }

    /**
     * Get possible paths a player can take. A player can choose a path if he
     * meets the prerequisites.
     * 
     * @param player
     *            Player
     * @return List of paths that a player can choose.
     */
    public List<Path> getPossiblePaths(Player player) {
        List<Path> possibilities = new ArrayList<>();

        for (Path path : this.getPaths()) {
            // Add path to possibilities, if player meets all prerequisites
            if (path.meetsPrerequisites(player)) {
                possibilities.add(path);
            }
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
            plugin.getLogger().warning("The paths file was not configured correctly! Log in to your server to get more info!");
            return;
        } else {
            paths = temp;
        }    

        for (final String message : debugPaths()) {
            plugin.debugMessage(message);
        }

    }

    /**
     * 
     * Get the path that corresponds to the given chosenPath string.
     * 
     * @param chosenPath
     *            The display name of the path
     * @param isCaseSensitive
     *            true if we only match paths that have the exact wording,
     *            taking into account case sensitivity.
     * @return matching path or null if none found.
     */
    public Path matchPathbyDisplayName(String chosenPath, boolean isCaseSensitive) {
        for (final Path path : this.getPaths()) {

            if (isCaseSensitive) {
                if (path.getDisplayName().equals(chosenPath)) {
                    return path;
                }
            } else {
                if (path.getDisplayName().equalsIgnoreCase(chosenPath)) {
                    return path;
                }
            }
        }

        return null;
    }

    /**
     * 
     * Get the path that corresponds to the given chosenPath string.
     * 
     * @param chosenPath
     *            The internal name of the path
     * @param isCaseSensitive
     *            true if we only match paths that have the exact wording,
     *            taking into account case sensitivity.
     * @return matching path or null if none found.
     */
    public Path matchPathbyInternalName(String chosenPath, boolean isCaseSensitive) {
        for (final Path path : this.getPaths()) {

            if (isCaseSensitive) {
                if (path.getInternalName().equals(chosenPath)) {
                    return path;
                }
            } else {
                if (path.getInternalName().equalsIgnoreCase(chosenPath)) {
                    return path;
                }
            }
        }

        return null;
    }

    /**
     * Assign a path to a player. It will reset their progress and run any
     * results that should be performed.
     * 
     * @param player
     *            Player to assign path to
     * @param pathName
     *            Name of the path
     */
    public void assignPath(Player player, String pathName) {
        // Set chosen path to target path
        plugin.getPlayerDataConfig().setChosenPath(player.getUniqueId(), pathName);

        // Reset progress
        plugin.getPlayerDataConfig().setCompletedRequirements(player.getUniqueId(), new ArrayList<Integer>());

        Path targetPath = plugin.getPathManager().matchPathbyInternalName(pathName, false);

        // Check if a player did not already start this path before (or complete
        // it).
        // If he did not, perform the results for choosing that path.
        if (plugin.getPlayerDataConfig().hasStartedPath(player.getUniqueId(), pathName)
                || plugin.getPlayerDataConfig().getCompletedPaths(player.getUniqueId()).contains(pathName)) {
            // Do not show anything - player already completed this path
        } else {
            // Perform results of path (if specified)
            targetPath.performResultsUponChoosing(player);

            // Add path to started path list
            plugin.getPlayerDataConfig().addStartedPath(player.getUniqueId(), pathName);
        }
    }

    /**
     * Try to automatically assign a path to a player.
     * 
     * @param player
     *            Player to assign a path to
     * @return Path that has been automatically assigned to the player or null
     *         if none was assigned.
     */
    public Path autoAssignPath(Player player) {

        // Player has already chosen a path, so we don't assign a new path
        if (plugin.getPathManager().getCurrentPath(player.getUniqueId()) != null) {
            return null;
        }

        // Get all paths that the player currently is able to choose.
        List<Path> possiblePaths = plugin.getPathManager().getPossiblePaths(player);

        // There is no path to choose.
        if (possiblePaths.size() < 1) {
            return null;
        }

        // Remove paths that should not be automatically chosen by Autorank
        for (Iterator<Path> iterator = possiblePaths.iterator(); iterator.hasNext();) {
            Path path = iterator.next();

            // Remove path if Autorank should not auto choose it
            if (!plugin.getPathsConfig().shouldAutoChoosePath(path.getInternalName())) {
                iterator.remove();
                continue;
            }
        }

        // A list of all priorities (without duplicates)
        List<Integer> priorities = new ArrayList<>();

        // Add all priorities to a list
        for (Path possiblePath : possiblePaths) {

            int priority = plugin.getPathsConfig().getPriorityOfPath(possiblePath.getInternalName());

            // Do not put in duplicates
            if (priorities.contains(priority)) {
                continue;
            }

            priorities.add(priority);
        }

        // Keep count to use for nth biggest element
        int count = 0;

        while (true) {
            // Get the nth highest path
            Integer tempHighest = AutorankTools.largestK(priorities.toArray(new Integer[priorities.size()]), count);

            // No highest found (array is empty)
            if (tempHighest == null) {
                return null;
            }

            int highestPriority = (int) tempHighest;

            // Get paths that have the highest priority
            List<Path> highestPriorityPaths = new ArrayList<>();

            for (Path path : possiblePaths) {
                if (plugin.getPathsConfig().getPriorityOfPath(path.getInternalName()) == highestPriority) {
                    highestPriorityPaths.add(path);
                }
            }

            // Get a list of paths that have been completed already
            List<String> completedPaths = plugin.getPlayerDataConfig().getCompletedPaths(player.getUniqueId());

            // Loop through each path to see if there are any non-completed
            // paths
            for (Path path : highestPriorityPaths) {

                // Path has already been completed
                if (completedPaths.contains(path.getInternalName())) {
                    continue;
                }

                // Assign path to the player
                plugin.getPathManager().assignPath(player, path.getInternalName());

                // Send message to player
                player.sendMessage(Lang.AUTOMATICALLY_ASSIGNED_PATH.getConfigValue(path.getDisplayName()));

                // Return the path that has been assigned to the player
                return path;
            }

            // All paths were already completed, so now look at those paths that
            // a player is allowed to repeat

            // Since all paths have been completed, just look at the first path
            // that is allowed to be repeated.
            for (Path path : highestPriorityPaths) {
                if (plugin.getPathsConfig().allowInfinitePathing(path.getInternalName())) {
                    
                    // Assign path to player
                    this.assignPath(player, path.getInternalName());

                    // Send message to player
                    player.sendMessage(Lang.AUTOMATICALLY_ASSIGNED_PATH.getConfigValue(path.getDisplayName()));

                    return path;
                }
            }

            // We did not find a path, so continue to next biggest elements
            count++;
        }
    }

    public void setBuilder(final PathBuilder builder) {
        this.builder = builder;
    }

}
