package me.armar.plugins.autorank.pathbuilder;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import org.bukkit.ChatColor;
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
     * @param player Player to check.
     * @return true when the player has completed this path, false otherwise.
     */
    public boolean checkPathProgress(Player player) {
        // Player does not meet all requirements, so don't do anything.
        if (!this.meetsAllRequirements(player)) {
            return false;
        }

        // Player meets all requirements, so complete this path.
        return plugin.getPathManager().completePath(this, player);
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
     * @param player Player to check for.
     * @param checkProgress whether to take into account the progress of a player (already completed requirements)
     * @return a list of RequirementHolders that the player has failed.
     */
    public List<CompositeRequirement> getFailedRequirements(final Player player, boolean checkProgress) {
        final List<CompositeRequirement> failedRequirements = new ArrayList<CompositeRequirement>();

        for (final CompositeRequirement holder : this.getRequirements()) {
            if (!holder.meetsRequirement(player)) {

                // If we care about progress of a player, we should check if he completed the requirement already.
                if (checkProgress && hasCompletedRequirement(player.getUniqueId(), holder
                        .getRequirementId())) {
                    continue;
                }

                failedRequirements.add(holder);
            }
        }

        return failedRequirements;
    }

    /**
     * Get the requirements of this path that a player has completed.
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
     * @param player Player to check
     * @return true when a player has completed all requirements at least once.
     */
    public boolean meetsAllRequirements(final Player player) {

        UUID uuid = player.getUniqueId();

        // Path is never met if it is not active.
        if (!this.isActive(uuid)) {
            return false;
        }

        boolean meetAllRequirements = true;

        // If we do not allow partial completion, we do not run any intermediary results of requirements. We only
        // check if a player completed all requirements or not.
        if (!this.allowPartialCompletion()) {
            for (final CompositeRequirement holder : this.getRequirements()) {
                if (!holder.meetsRequirement(player)) {
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
            if (this.hasCompletedRequirement(player.getUniqueId(), holder
                    .getRequirementId())) {
                System.out.println("Requirement " + holder.getDescription() + " has already been completed");
                continue;
            }

            if (holder.meetsRequirement(player)) {
                // Optional requirements can only be completed by performing /ar complete, so don't perform them
                // automatically.
                if (holder.isOptional()) {
                    System.out.println("Requirement " + holder.getDescription() + " is met, but optional, so not " +
                            "performing results automatically.");
                    continue;
                }

                System.out.println("Requirement " + holder.getDescription() + " is met, performing results");
                // Meets requirement, so perform results
                this.completeRequirement(player, holder.getRequirementId());
            } else {
                System.out.println("Requirement " + holder.getDescription() + " is not met");
                meetAllRequirements = false;
            }

        }

        // Do we meet all requirements?
        return meetAllRequirements;
    }

    /**
     * Mark a requirement as complete.
     *
     * @param player Player to run results for.
     * @param reqId  Id of requirement that is met.
     */
    public void completeRequirement(Player player, int reqId) {

        CompositeRequirement requirement = this.getRequirements().stream().filter(compositeRequirement ->
                compositeRequirement.getRequirementId() == reqId).findFirst().get();

        // Notify player.
        player.sendMessage(
                ChatColor.GREEN + Lang.SUCCESSFULLY_COMPLETED_REQUIREMENT.getConfigValue(reqId + ""));
        player.sendMessage(ChatColor.AQUA + requirement.getDescription());

        // Run results
        requirement.runResults(player);

        // Log that a player has passed this requirement
        plugin.getPathManager().addCompletedRequirement(player.getUniqueId(), this, reqId);
    }

    /**
     * Check whether a player meets all prerequisites of this path.
     *
     * @param player Player to check
     * @return true if the player meets all prerequisites. False otherwise.
     */
    public boolean meetsPrerequisites(Player player) {

        List<CompositeRequirement> preRequisites = this.getPrerequisites();

        for (CompositeRequirement preRequisite : preRequisites) {
            if (!preRequisite.meetsRequirement(player)) {
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
        return plugin.getPathManager().hasActivePath(uuid, this);
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
     * {@link PathManager#isPathEligible(Player, Path)}.
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
}
