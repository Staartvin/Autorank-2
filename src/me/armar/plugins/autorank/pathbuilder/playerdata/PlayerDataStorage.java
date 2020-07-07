package me.armar.plugins.autorank.pathbuilder.playerdata;

import io.reactivex.annotations.NonNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * This abstract class represents a data storage that stores player data. Note that not all of the methods will
 * return meaningful data, as it might not have been implemented by the data storage.
 */
public interface PlayerDataStorage {

    // --------- Requirements ---------

    /**
     * Get a list of completed requirements of a player for a given path.
     * This list is reset when a player completes the path.
     *
     * @param uuid UUID of the player
     * @return a list of requirements a player has completed for a given path.
     */
    Collection<Integer> getCompletedRequirements(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Check whether a player completed a specific requirement of a given path.
     *
     * @param uuid          UUID of the player
     * @param pathName      Name of the path.
     * @param requirementId ID of requirement
     * @return true if the player completed the given requirement. False
     * otherwise.
     */
    boolean hasCompletedRequirement(@NonNull UUID uuid, @NonNull String pathName, int requirementId);

    /**
     * Add a completed requirement of a path for a given player.
     *
     * @param uuid          UUID of the player
     * @param pathName      Name of the path
     * @param requirementId Id of the requirement that has been completed.
     */
    void addCompletedRequirement(@NonNull UUID uuid, @NonNull String pathName, int requirementId);

    /**
     * Set the completed requirements of a player for a path.
     *
     * @param uuid         UUID of the player
     * @param pathName     Name of the path
     * @param requirements Requirements ids to set as completed.
     */
    void setCompletedRequirements(@NonNull UUID uuid, @NonNull String pathName,
                                  @NonNull Collection<Integer> requirements);

    // --------- Completed requirements where results are not performed yet ---------

    /**
     * Get all requirements of a path that were completed by a player but where the results have not been performed
     * yet as the player was not online.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path to get the requirements for.
     * @return list of requirement ids that the player completed in the given path
     */
    Collection<Integer> getCompletedRequirementsWithMissingResults(@NonNull UUID uuid,
                                                                   @NonNull String pathName);

    /**
     * Add a requirement that was completed by a player but where the results have not yet been performed as the player
     * was not online.
     *
     * @param uuid          UUID of the player
     * @param pathName      Name of the path that was completed.
     * @param requirementId ID of the requirement that was completed.
     */
    void addCompletedRequirementWithMissingResults(@NonNull UUID uuid, @NonNull String pathName,
                                                   int requirementId);

    /**
     * Remove a requirement that was completed by a player but where the results have not yet been performed as the
     * player was not online.
     *
     * @param uuid          UUID of the player
     * @param pathName      Name of the path to remove.
     * @param requirementId ID of the requirement that was completed
     */
    void removeCompletedRequirementWithMissingResults(@NonNull UUID uuid, @NonNull String pathName,
                                                      int requirementId);

    /**
     * Check whether a requirement was completed by a player but where the results have not yet been performed, as the
     * player was not yet online.
     *
     * @param uuid          UUID of the player
     * @param pathName      Name of the path to check.
     * @param requirementId ID of the requirement to check.
     * @return true if the path was completed but the results are not performed yet, false otherwise.
     */
    boolean hasCompletedRequirementWithMissingResults(@NonNull UUID uuid, @NonNull String pathName,
                                                      int requirementId);

    // --------- Prerequisites ---------

    /**
     * Get a list of completed prerequisites of a player for a given path. <br>
     * This list is reset when a player chooses the path.
     *
     * @param uuid UUID of the player
     * @return a list of prerequisites a player has completed for a given path.
     */
    Collection<Integer> getCompletedPrerequisites(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Check whether a player completed a specific prerequisite of a given path.
     *
     * @param uuid           UUID of the player
     * @param pathName       Name of the path.
     * @param prerequisiteId ID of prerequisite
     * @return true if the player completed the given prerequisite. False
     * otherwise.
     */
    boolean hasCompletedPrerequisite(@NonNull UUID uuid, @NonNull String pathName, int prerequisiteId);

    /**
     * Add a completed prerequisite of a path for a given player.
     *
     * @param uuid           UUID of the player
     * @param pathName       Name of the path
     * @param prerequisiteId Id of the prerequisite that has been completed.
     */
    void addCompletedPrerequisite(@NonNull UUID uuid, @NonNull String pathName, int prerequisiteId);

    /**
     * Set the completed prerequisites of a player for a path.
     *
     * @param uuid          UUID of the player
     * @param pathName      Name of the path
     * @param prerequisites Prerequisites ids to set as completed.
     */
    void setCompletedPrerequisites(@NonNull UUID uuid, @NonNull String pathName,
                                   @NonNull Collection<Integer> prerequisites);

    // --------- Chosen paths where results are not performed yet ---------

    /**
     * Get all paths that a player has chosen but the results have not been performed yet, as the player was not
     * online.
     *
     * @param uuid UUID of the player
     * @return a list of path names.
     */
    Collection<String> getChosenPathsWithMissingResults(@NonNull UUID uuid);

    /**
     * Add a path that was chosen by a player but where the results have not yet been performed as the player was
     * not online.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path that was chosen.
     */
    void addChosenPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Remove a path that was chosen by a player but where the results have not yet been performed as the player
     * was not online.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path to remove.
     */
    void removeChosenPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Check whether a path was chosen by a player but where the results have not yet been performed, as the
     * player was not yet online.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path to check.
     * @return true if the path was chosen but the results are not performed yet, false otherwise.
     */
    boolean hasChosenPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName);


    // --------- Active paths ---------

    /**
     * Get active paths for a player
     *
     * @param uuid UUID of the player
     * @return collection of paths that are active for the given player.
     */
    Collection<String> getActivePaths(@NonNull UUID uuid);

    /**
     * Check whether a path is active for a player.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of path
     * @return true if the given path is active for the given player.
     */
    boolean hasActivePath(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Add a path that a player is active.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path that is active.
     */
    void addActivePath(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Remove an active path from a player.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path that is active.
     */
    void removeActivePath(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Set the paths that are active for a player.
     *
     * @param uuid  UUID of the player
     * @param paths Paths that are set to active.
     */
    void setActivePaths(@NonNull UUID uuid, @NonNull Collection<String> paths);

    // --------- Completed paths ---------

    /**
     * Get a list of paths that a player completed.
     *
     * @param uuid UUID of the player
     * @return a list of path names that the given player completed.
     */
    Collection<String> getCompletedPaths(@NonNull UUID uuid);

    /**
     * Check whether a player has completed a specific path.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of path
     * @return true if the given player has completed the given path. False
     * otherwise.
     */
    boolean hasCompletedPath(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Add a path that a player has completed.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path that was completed.
     */
    void addCompletedPath(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Remove a completed path from the list of completed paths.
     *
     * @param uuid UUID of the player
     */
    void removeCompletedPath(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Set the paths a player has completed.
     *
     * @param uuid  UUID of the player
     * @param paths Names of the paths that the player has completed.
     */
    void setCompletedPaths(@NonNull UUID uuid, @NonNull Collection<String> paths);

    /**
     * Get the number of times a path has been completed by a user.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path
     * @return number of times a path has been completed, or zero if it hasn't been completed before.
     */
    int getTimesCompletedPath(@NonNull UUID uuid, @NonNull String pathName);


    /**
     * Get the time (in minutes) since the given player has completed the given path. If the path has not been
     * completed, the result will be empty.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path
     * @return time in minutes since this path has been completed, or empty if it hasn't been completed yet.
     */
    Optional<Long> getTimeSinceCompletionOfPath(UUID uuid, String pathName);

    /**
     * Reset the progress of all paths of a player.
     *
     * @param uuid UUID of the player
     */
    void resetProgressOfAllPaths(UUID uuid);

    /**
     * Reset the progress of a path of a player
     *
     * @param uuid     UUID of the player
     * @param pathName Path to reset the progress of.
     */
    void resetProgressOfPath(UUID uuid, String pathName);

    // --------- Completed paths where results are not performed yet ---------

    /**
     * Get all paths that a player has completed but the results have not been performed yet, as the player was not
     * online.
     *
     * @param uuid UUID of the player
     * @return a list of path names.
     */
    Collection<String> getCompletedPathsWithMissingResults(@NonNull UUID uuid);

    /**
     * Add a path that was completed by a player but where the results have not yet been performed as the player was
     * not online.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path that was completed.
     */
    void addCompletedPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Remove a path that was completed by a player but where the results have not yet been performed as the player
     * was not online.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path to remove.
     */
    void removeCompletedPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName);

    /**
     * Check whether a path was completed by a player but where the results have not yet been performed, as the
     * player was not yet online.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path to check.
     * @return true if the path was completed but the results are not performed yet, false otherwise.
     */
    boolean hasCompletedPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName);

    // --------- Details of a player ---------

    /**
     * Check whether a player is exempted from appearing on any leaderboard.
     *
     * @param uuid UUID of the player
     * @return true if the given player is not allowed to be shown on any leaderboard. False otherwise.
     */
    boolean hasLeaderboardExemption(@NonNull UUID uuid);

    /**
     * Set whether a player is exempted from appearing on any leaderboard.
     *
     * @param uuid  UUID of the player
     * @param value Value to set the exemption status to.
     */
    void setLeaderboardExemption(@NonNull UUID uuid, boolean value);

    /**
     * Check whether a player is exempted from checking their progress on paths. This means both automated and manual
     * checks (using /ar check).
     *
     * @param uuid UUID of the player
     * @return true if the given player is not allowed to be checked by Autorank.
     */
    boolean hasAutoCheckingExemption(@NonNull UUID uuid);

    /**
     * Set whether a player is exempted from automatic and manual checking of paths.
     *
     * @param uuid  UUID of the player
     * @param value Value to set the exemption status to.
     */
    void setAutoCheckingExemption(@NonNull UUID uuid, boolean value);

    /**
     * Check whether a player is exempted from building up time. If he is, Autorank will not count any of their time.
     *
     * @param uuid UUID of the player
     * @return true if the given player's time is not counted.
     */
    boolean hasTimeAdditionExemption(@NonNull UUID uuid);

    /**
     * Set whether a player is exempted from building up time.
     *
     * @param uuid  UUID of the player
     * @param value Value to set the exemption status to.
     */
    void setTimeAdditionExemption(@NonNull UUID uuid, boolean value);

    // --------- Data of this storage type ---------

    /**
     * Get the type of this data storage.
     *
     * @return a {@link me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataManager.PlayerDataStorageType}
     * object.
     */
    PlayerDataManager.PlayerDataStorageType getDataStorageType();

}
