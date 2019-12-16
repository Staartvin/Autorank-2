package me.armar.plugins.autorank.pathbuilder;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.RequirementCompleteEvent;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataManager;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a path that a player can take, including all requirements and
 * results.
 * <p>
 * Date created: 14:23:30 5 aug. 2015
 *
 * @author Staartvin
 */
public class Path {

    private final Autorank plugin;

    // The display name is the name that will be shown to the player.
    // The internal name is used to find the properties of the path in paths.yml
    private String displayName = "", internalName = "";

    // Description of a path
    private String description = "";

    // Can a player redo this path even if it has completed this path already.
    private boolean isRepeatable = false;

    // Will this path automatically be assigned to a player if eligible?
    private boolean isAutomaticallyAssigned = false;

    // A CompositeRequirement is a holder for one or more requirements that can
    // be met simultaneously.
    // A prerequisite is a requirement that needs to be met before a player can choose this path.
    private List<CompositeRequirement> prerequisites = new ArrayList<>();

    // Requirements need to be met by a player to complete this path.
    private List<CompositeRequirement> requirements = new ArrayList<CompositeRequirement>();

    // Results that are performed when all requirements are met.
    private List<AbstractResult> results = new ArrayList<AbstractResult>();

    // Results that are performed when the path is assigned to the player.
    private List<AbstractResult> resultsUponChoosing = new ArrayList<AbstractResult>();

    // Whether a player on this path can complete requirements at different times. If partial completion is true,
    // players can complete a requirement and then come back later to complete a different one. They don't need to
    // complete all requirements at the same time.
    private boolean allowPartialCompletion = true;

    // This variable is true when players can only see this path in '/ar view list' if they meet the prerequisites of
    // the path. If it is false, the path will always be shown.
    private boolean onlyShowIfPrerequisitesMet = false;

    // Whether Autorank should store progress of a player's path when he deactivates this path.
    private boolean storeProgressOnDeactivation = false;

    public Path(final Autorank plugin) {
        this.plugin = plugin;
    }

    /**
     * Add a prerequisite to the path.
     *
     * @param prerequisite Prerequisite to add.
     * @throws IllegalArgumentException if !prerequisite.isPrerequisite()
     * @throws NullPointerException     if prerequisite == null
     */
    public void addPrerequisite(final CompositeRequirement prerequisite) throws IllegalArgumentException,
            NullPointerException {

        if (prerequisite == null) {
            throw new NullPointerException("CompositeRequirement is null");
        }

        if (!prerequisite.isPrerequisite()) {
            throw new IllegalArgumentException("CompositeRequirement is not a prerequisite.");
        }

        this.prerequisites.add(prerequisite);
    }

    /**
     * Add a requirement to this path.
     *
     * @param requirement Requirement to add.
     * @throws NullPointerException if requirement == null
     */
    public void addRequirement(final CompositeRequirement requirement) throws NullPointerException {
        if (requirement == null) {
            throw new NullPointerException("CompositeRequirement is null");
        }

        requirements.add(requirement);
    }

    /**
     * Add an result to this path.
     *
     * @param result Result to add.
     * @throws NullPointerException if result == null
     */
    public void addResult(AbstractResult result) throws NullPointerException {

        if (result == null) {
            throw new NullPointerException("Given result is null");
        }

        this.results.add(result);
    }

    /**
     * Add an result that is executed when a player chooses this path.
     *
     * @param result Result to add.
     * @throws NullPointerException if result == null
     */
    public void addResultUponChoosing(AbstractResult result) throws NullPointerException {

        if (result == null) {
            throw new NullPointerException("Given result is null");
        }

        this.resultsUponChoosing.add(result);
    }

    /**
     * Check the progress of a player for this path. When all requirements are met, the path is marked as complete
     * and the results are performed.
     *
     * @param uuid Player to check.
     * @return true when the player has completed this path, false otherwise.
     */
    public boolean checkPathProgress(UUID uuid) {
        // Player does not meet all requirements, so don't do anything.
        if (!this.meetsAllRequirements(uuid)) {
            return false;
        }

        // Player meets all requirements, so complete this path.
        plugin.getPathManager().completePath(this, uuid);

        return true;
    }

    /**
     * Get the display name of the path. If no display name was specified, this will return the name of the path in the
     * paths.yml file.
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Set the display name of this path.
     *
     * @param displayName Display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the requirements that a player does not meet
     *
     * @param uuid          Player to check for.
     * @param checkProgress whether to take into account the progress of a player (already completed requirements)
     * @return a list of RequirementHolders that the player has failed.
     */
    public List<CompositeRequirement> getFailedRequirements(UUID uuid, boolean checkProgress) {
        final List<CompositeRequirement> failedRequirements = new ArrayList<CompositeRequirement>();

        for (final CompositeRequirement holder : this.getRequirements()) {
            if (!holder.meetsRequirement(uuid)) {

                // If we care about progress of a player, we should check if he completed the requirement already.
                if (checkProgress && hasCompletedRequirement(uuid, holder.getRequirementId())) {
                    continue;
                }

                failedRequirements.add(holder);
            }
        }

        return failedRequirements;
    }

    /**
     * Get the requirements of this path that a player has completed.
     *
     * @param uuid UUID of the player
     * @return list of requirements.
     */
    public List<CompositeRequirement> getCompletedRequirements(UUID uuid) {
        List<CompositeRequirement> completedRequirements = new ArrayList<>();

        for (CompositeRequirement requirement : this.getRequirements()) {
            if (plugin.getPathManager().hasCompletedRequirement(uuid, this, requirement.getRequirementId())) {
                completedRequirements.add(requirement);
            }
        }

        return completedRequirements;
    }

    /**
     * Get all prerequisites for this path.
     *
     * @return a list of prerequisites
     */
    public List<CompositeRequirement> getPrerequisites() {
        return prerequisites;
    }

    /**
     * Get all requirements for this path.
     *
     * @return a list of requirements.
     */
    public List<CompositeRequirement> getRequirements() {
        return requirements;
    }

    /**
     * Set the requirements for this path.
     *
     * @param requirements Requirements to set.
     */
    public void setRequirements(final List<CompositeRequirement> requirements) {
        this.requirements = requirements;
    }

    /**
     * Get all results that will be run when this path is completed.
     *
     * @return a list of results.
     */
    public List<AbstractResult> getResults() {
        return results;
    }

    /**
     * Set the results for this path.
     *
     * @param results Results to run when path is completed.
     */
    public void setResults(final List<AbstractResult> results) {
        this.results = results;
    }

    /**
     * Check whether a player meets all requirements for this path.
     *
     * @param uuid Player to check
     * @return true when a player has completed all requirements at least once.
     */
    public boolean meetsAllRequirements(final UUID uuid) {

        // Path is never met if it is not active.
        if (!this.isActive(uuid)) {
            return false;
        }

        boolean meetAllRequirements = true;

        // If we do not allow partial completion, we do not run any intermediary results of requirements. We only
        // check if a player completed all requirements or not.
        if (!this.allowPartialCompletion()) {
            for (final CompositeRequirement holder : this.getRequirements()) {
                if (!holder.meetsRequirement(uuid)) {
                    // If player does not meet the requirement, we can immediately return false.
                    return false;
                }
            }

            // All requirements are met, so we return true.
            return true;
        }

        for (final CompositeRequirement holder : this.getRequirements()) {
            if (holder == null)
                return false;

            // Skip completed requirements.
            if (this.hasCompletedRequirement(uuid, holder
                    .getRequirementId())) {
                continue;
            }

            if (holder.meetsRequirement(uuid)) {
                // Optional requirements can only be completed by performing /ar complete, so don't perform them
                // automatically.
                if (holder.isOptional()) {
                    continue;
                }

                // Meets requirement, so perform results
                this.completeRequirement(uuid, holder.getRequirementId());
            } else {
                meetAllRequirements = false;
            }

        }

        // Do we meet all requirements?
        return meetAllRequirements;
    }

    /**
     * Mark a requirement as complete.
     *
     * @param uuid  UUID of the player.
     * @param reqId Id of requirement that is met.
     */
    public void completeRequirement(UUID uuid, int reqId) {

        CompositeRequirement requirement = this.getRequirement(reqId);

        if (requirement == null) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        Player player = offlinePlayer.getPlayer();

        // Check whether the user is online or not.
        if (player == null) {
            // The user is not online, so we register that they will get their results when they come back online.
            this.plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent(s ->
                    s.addCompletedRequirementWithMissingResults(uuid, this.getInternalName(), reqId));
        } else {
            // Notify player.
            player.sendMessage(
                    ChatColor.GREEN + Lang.SUCCESSFULLY_COMPLETED_REQUIREMENT.getConfigValue(reqId + ""));
            player.sendMessage(ChatColor.AQUA + requirement.getDescription());
        }


        // Fire event on main thread.
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            // Fire event so it can be cancelled
            // Create the event here
            final RequirementCompleteEvent event = new RequirementCompleteEvent(uuid, requirement);
            // Call the event
            Bukkit.getServer().getPluginManager().callEvent(event);

            // Check if event is cancelled.
            if (event.isCancelled())
                return;

            // Run results
            if (player != null) {
                requirement.runResults(player);
            }

            // Log that a player has passed this requirement
            plugin.getPathManager().addCompletedRequirement(uuid, this, reqId);
        });
    }

    /**
     * Check whether a player meets all prerequisites of this path.
     *
     * @param uuid Player to check
     * @return true if the player meets all prerequisites. False otherwise.
     */
    public boolean meetsPrerequisites(UUID uuid) {

        List<CompositeRequirement> preRequisites = this.getPrerequisites();

        for (CompositeRequirement preRequisite : preRequisites) {
            if (!preRequisite.meetsRequirement(uuid)) {
                // If one of the prerequisites does not hold, a player does not
                // meet all the prerequisites.
                return false;
            }
        }

        return true;
    }

    /**
     * Execute the results that should be run when this path is chosen by this path.
     *
     * @param player Player to perform them for.
     * @return true if all results were performed successfully, false otherwise.
     */
    public boolean performResultsUponChoosing(Player player) {
        boolean success = true;

        for (AbstractResult r : this.getResultsUponChoosing()) {

            if (r.isGlobal()) {
                boolean hasCompletedPathGlobally =
                        plugin.getPlayerDataManager().getDataStorage(PlayerDataManager.PlayerDataStorageType.GLOBAL)
                                .map(s -> s.hasCompletedPath(player.getUniqueId(), this.getInternalName())).orElse(false);

                // Don't perform this result if the path has already been completed on another server.
                if (hasCompletedPathGlobally) continue;
            }

            if (!r.applyResult(player)) {
                success = false;
            }
        }

        return success;
    }

    /**
     * Execute the results when a path has been completed.
     *
     * @param player Player to execute the results for.
     * @return whether all results were successfully performed.
     */
    public boolean performResults(Player player) {
        boolean success = true;

        for (AbstractResult r : this.getResults()) {

            if (r.isGlobal()) {
                boolean hasCompletedPathGlobally =
                        plugin.getPlayerDataManager().getDataStorage(PlayerDataManager.PlayerDataStorageType.GLOBAL)
                                .map(s -> s.hasCompletedPath(player.getUniqueId(), this.getInternalName())).orElse(false);

                // Don't perform this result if the path has already been completed on another server.
                if (hasCompletedPathGlobally) continue;
            }

            if (!r.applyResult(player)) {
                success = false;
            }
        }

        return success;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Get the internal name of this path. The internal name of this path is the String that is used in the Paths.yml
     * file.
     *
     * @return internal name.
     */
    public String getInternalName() {
        return internalName;
    }

    /**
     * Set the internal name of this path.
     *
     * @param internalName Internal name to set.
     */
    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    /**
     * Get the results that should be performed when this path is chosen by a player.
     *
     * @return list of results
     */
    public List<AbstractResult> getResultsUponChoosing() {
        return resultsUponChoosing;
    }

    /**
     * Set the results that should be performed when this path is chosen by a player.
     *
     * @param resultsUponChoosing Results to be performed.
     */
    public void setResultsUponChoosing(List<AbstractResult> resultsUponChoosing) {
        this.resultsUponChoosing = resultsUponChoosing;
    }

    /**
     * Check whether a player can complete requirements in steps instead of meeting all requirements at the same time.
     *
     * @return true if players can complete requirements one by one. False if they need to complete the requirements
     * at the same time.
     */
    public boolean allowPartialCompletion() {
        return allowPartialCompletion;
    }

    public void setAllowPartialCompletion(boolean allowPartialCompletion) {
        this.allowPartialCompletion = allowPartialCompletion;
    }

    /**
     * Check whether this path is repeatable. If a path is repeatable, a player can complete this path as many times
     * as it likes.
     *
     * @return true when this path is repeatable, false otherwise.
     */
    public boolean isRepeatable() {
        return isRepeatable;
    }

    /**
     * Set whether this path is repeatable or not. See {@link #isRepeatable()}.
     *
     * @param repeatable whether this path is repeatable.
     */
    public void setRepeatable(boolean repeatable) {
        isRepeatable = repeatable;
    }

    /**
     * Get the description of this path.
     *
     * @return description of this path
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this path.
     *
     * @param description description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Check whether this path is active for a player.
     *
     * @param uuid UUID of the player
     * @return true if the path is active for the given player. False otherwise.
     */
    public boolean isActive(UUID uuid) {
        return plugin.getPlayerDataManager().getPrimaryDataStorage().map(storage -> storage.hasActivePath(uuid,
                this.getInternalName())).orElse(false);
    }

    /**
     * Check whether a player has completed a requirement.
     *
     * @param uuid  UUID of the player
     * @param reqId Id of the requirement.
     * @return true if the player has completed the requirement.
     */
    public boolean hasCompletedRequirement(UUID uuid, int reqId) {
        return plugin.getPathManager().hasCompletedRequirement(uuid, this, reqId);
    }

    /**
     * Whether this path is automatically assigned to a player if the path is eligible. See
     * {@link Path#isEligible(UUID)}.
     *
     * @return true when this path can automatically be assigned to a player/
     */
    public boolean isAutomaticallyAssigned() {
        return isAutomaticallyAssigned;
    }

    /**
     * Set whether this path can automatically be assigned to a player when possible.
     *
     * @param automaticallyAssigned value to set it to.
     */
    public void setAutomaticallyAssigned(boolean automaticallyAssigned) {
        isAutomaticallyAssigned = automaticallyAssigned;
    }

    /**
     * Check whether this path is only shown to a player when all prerequisites are met. If this method returns true,
     * players will only be able to see this path if they meet all prerequisites. If it is false, players will always
     * see this path.
     *
     * @return whether a player will see this path if they meet the prerequisites or not.
     */
    public boolean onlyShowIfPrerequisitesMet() {
        return onlyShowIfPrerequisitesMet;
    }

    /**
     * Set whether this path should only be shown to a player when they meet all prerequisites.
     *
     * @param onlyShowIfPrerequisitesMet value to set.
     */
    public void setOnlyShowIfPrerequisitesMet(boolean onlyShowIfPrerequisitesMet) {
        this.onlyShowIfPrerequisitesMet = onlyShowIfPrerequisitesMet;
    }

    /**
     * Check whether Autorank should store the progress of a player's path when he deactivates a path. If he leaves
     * the path and the progress is stored, he can return to the path at any time and start where he left of.
     *
     * @return true if progress should be stored, false otherwise.
     */
    public boolean shouldStoreProgressOnDeactivation() {
        return storeProgressOnDeactivation;
    }

    /**
     * Set whether Autorank should store progress of a path.
     *
     * @param storeProgressOnDeactivation Value to set it to.
     */
    public void setStoreProgressOnDeactivation(boolean storeProgressOnDeactivation) {
        this.storeProgressOnDeactivation = storeProgressOnDeactivation;
    }

    /**
     * Get the progress of this path for a given player in percentage (0 means no requirements completed, 1 means
     * all requirements completed).
     *
     * @param uuid UUID to check
     * @return progress on this path or 0 if it hasn't been started.
     */
    public double getProgress(UUID uuid) {
        return this.getCompletedRequirements(uuid).size() * 1.0d / this.getRequirements().size();
    }

    /**
     * Get how many times a player has completed this path.
     *
     * @return number of times that this path has been completed by the given player.
     */
    public int getTimesCompleted(UUID uuid) {
        return plugin.getPlayerDataManager().getPrimaryDataStorage().map(s -> s.getTimesCompletedPath(uuid,
                this.getInternalName())).orElse(0);
    }

    /**
     * Get whether a player has completed this path.
     *
     * @param uuid UUID of the player
     * @return true if it has, false otherwise.
     */
    public boolean hasCompletedPath(UUID uuid) {
        return this.getTimesCompleted(uuid) > 0;
    }

    /**
     * Check if a player has deactivated this path. A path is considered to be de-activated if there is progress on the
     * path, but the status of the path is not 'active'.
     *
     * @param uuid UUID of the player
     * @return true if the path is deactivated, false otherwise.
     */
    public boolean isDeactivated(UUID uuid) {
        // Check if the path is not active and the player has completed at least one requirement.
        return !this.isActive(uuid) && this.getCompletedRequirements(uuid).size() > 0;
    }

    /**
     * Check whether this path is eligible for a player. A path is eligible for a player if all of the following apply:
     * <ul>
     * <li>The path is not active for the player.</li>
     * <li>The player has not completed the path yet, or the path is repeatable.</li>
     * <li>The player meets the prerequisites of the path.</li>
     * <li>The player has not deactivated the path manually.</li>
     * </ul>
     *
     * @param uuid Player to check
     * @return true if the path is eligible for the given player.
     */
    public boolean isEligible(UUID uuid) {
        // A path is not eligible when a player has already has it as active.
        if (isActive(uuid)) {
            return false;
        }

        // If a path has been completed and cannot be repeated, the player cannot take this path again.
        if (this.hasCompletedPath(uuid) && !this.isRepeatable()) {
            return false;
        }

        // If a path does not meet the prerequisites of a path, the player cannot take the path.
        return this.meetsPrerequisites(uuid);
    }

    /**
     * Get the requirement that corresponds to the given requirement id.
     *
     * @param id ID of the requirement to find.
     * @return Requirement if found, otherwise null.
     */
    public CompositeRequirement getRequirement(int id) {
        return this.getRequirements().stream().filter(compositeRequirement ->
                compositeRequirement.getRequirementId() == id).findFirst().orElse(null);
    }
}
