package me.armar.plugins.autorank.pathbuilder;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a path that a player can take, including all requirements and
 * abstractResults.
 * <p>
 * Date created: 14:23:30 5 aug. 2015
 *
 * @author Staartvin
 */
public class Path {

    // The display name is the name that will be shown to the player.
    // The internal name is used to find the properties of the path in paths.yml
    private String displayName = "", internalName = "";

    private final Autorank plugin;

    // A requirements holder is a holder for one or more requirements that can
    // be met simultaneously.
    private List<RequirementsHolder> prerequisites = new ArrayList<>();

    private List<RequirementsHolder> requirements = new ArrayList<RequirementsHolder>();

    // Results that are performed when all requirements are met.
    private List<AbstractResult> abstractResults = new ArrayList<AbstractResult>();

    // Results that are performed when the path is assigned to the player.
    private List<AbstractResult> resultsUponChoosing = new ArrayList<AbstractResult>();

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
    public void addPrerequisite(final RequirementsHolder prerequisite) throws IllegalArgumentException, NullPointerException {

        if (prerequisite == null) {
            throw new NullPointerException("RequirementsHolder is null");
        }

        if (!prerequisite.isPrerequisite()) {
            throw new IllegalArgumentException("RequirementsHolder is not a prerequisite.");
        }

        this.prerequisites.add(prerequisite);
    }

    /**
     * Add a requirement to the path.
     *
     * @param requirement AbstractRequirement to add.
     * @throws NullPointerException if requirement == null
     */
    public void addRequirement(final RequirementsHolder requirement) throws NullPointerException {
        if (requirement == null) {
            throw new NullPointerException("RequirementsHolder is null");
        }

        requirements.add(requirement);
    }

    /**
     * Add a abstractResult to the path.
     *
     * @param abstractResult AbstractResult to add.
     * @throws NullPointerException if abstractResult == null
     */
    public void addResult(AbstractResult abstractResult) throws NullPointerException {

        if (abstractResult == null) {
            throw new NullPointerException("AbstractResult is null");
        }

        this.abstractResults.add(abstractResult);
    }

    public boolean applyChange(final Player player) {
        boolean result = true;

        if (meetRequirements(player)) {

            final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

            // Apply all 'main' abstractResults

            // Get chosen path of player
            Path currentPath = plugin.getPathManager().getCurrentPath(uuid);

            if (currentPath == null) {
                return false;
            }

            // Player already got this path
            if (plugin.getPlayerDataConfig().hasCompletedPath(uuid, currentPath.getInternalName())) {
                return false;
            }

            // Add progress of completed requirements
            plugin.getPlayerDataConfig().addCompletedPath(uuid, currentPath.getInternalName());

            // Remove path from started paths if it's completed.
            plugin.getPlayerDataConfig().removeStartedPath(uuid, currentPath.getInternalName());

            for (final AbstractResult r : this.getAbstractResults()) {
                if (r != null) {
                    if (!r.applyResult(player)) {
                        result = false;
                    }
                }
            }

            // After getting abstractResults, chosen path is reset.
            plugin.getPlayerDataConfig().setChosenPath(uuid, null);

            // Reset progress
            plugin.getPlayerDataConfig().setCompletedRequirements(uuid, null);

            // Try to assign a new path to a player
            plugin.getPathManager().autoAssignPath(player);

        } else {
            result = false;
        }

        return result;
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
     * Get the requirements that a player did not yet complete.
     *
     * @param player Player to check for.
     * @return a list of RequirementHolders that the player has failed.
     */
    public List<RequirementsHolder> getFailedRequirements(final Player player) {
        final List<RequirementsHolder> holders = new ArrayList<RequirementsHolder>();

        for (final RequirementsHolder holder : this.getRequirements()) {
            if (!holder.meetsRequirement(player, false)) {
                holders.add(holder);
            }
        }

        return holders;
    }

    /**
     * Get the requirements that a player has already completed.
     *
     * @param player Player to check for.
     * @return a list of RequirementHolders that the player passed.
     */
    public List<RequirementsHolder> getMetRequirements(final Player player) {
        List<RequirementsHolder> holders = new ArrayList<>();

        for (RequirementsHolder holder : this.getRequirements()) {
            if (holder.meetsRequirement(player, false)) {
                holders.add(holder);
            }
        }

        return holders;
    }

    public List<RequirementsHolder> getPrerequisites() {
        return prerequisites;
    }

    public List<RequirementsHolder> getRequirements() {
        return requirements;
    }

    public List<AbstractResult> getAbstractResults() {
        return abstractResults;
    }

    public boolean meetRequirements(final Player player) {

        UUID uuid = player.getUniqueId();

        // Get chosen path of player
        Path currentPath = plugin.getPathManager().getCurrentPath(uuid);

        if (currentPath == null) {
            return false;
        }

        // Player already completed this path
        if (plugin.getPlayerDataConfig().hasCompletedPath(uuid, currentPath.getInternalName())) {
            return false;
        }

        for (final RequirementsHolder holder : this.getRequirements()) {
            if (holder == null)
                return false;

            // We don't do partial completion so we only need to check if a
            // player passes all requirements holders.
            if (!plugin.getSettingsConfig().usePartialCompletion()) {
                if (!holder.meetsRequirement(player, false)) {
                    return false;
                } else {
                    continue;
                }
            }

            // Holder does not meet requirements, so not all requirements are
            // met!
            if (!holder.meetsRequirement(player, false)) {
                return false;
            }

        }

        // When never returning false, return true at last!
        return true;
    }

    public boolean meetsPrerequisites(Player player) {

        List<RequirementsHolder> preRequisites = this.getPrerequisites();

        for (RequirementsHolder preRequisite : preRequisites) {
            if (!preRequisite.meetsRequirement(player, false)) {
                // If one of the prerequisites does not hold, a player does not
                // meet all the prerequisites.
                return false;
            }
        }

        return true;
    }

    public void setAbstractResults(final List<AbstractResult> abstractResults) {
        this.abstractResults = abstractResults;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRequirements(final List<RequirementsHolder> holders) {
        this.requirements = holders;
    }

    /**
     * Perform the abstractResults upon choosing this path.
     *
     * @param player Player to perform them for.
     * @return true if all abstractResults were performed succesfully, false otherwise.
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

    @Override
    public String toString() {
        return displayName;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public List<AbstractResult> getResultsUponChoosing() {
        return resultsUponChoosing;
    }

    public void setResultsUponChoosing(List<AbstractResult> resultsUponChoosing) {
        this.resultsUponChoosing = resultsUponChoosing;
    }
}
