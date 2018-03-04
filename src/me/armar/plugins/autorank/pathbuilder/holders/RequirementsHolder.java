package me.armar.plugins.autorank.pathbuilder.holders;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.RequirementCompleteEvent;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Since a requirement in Autorank's config can have multiple real requirements,
 * such as <br>
 * kill 10 cows OR kill 10 cats, this holder is introduced to check if either
 * one of the <br>
 * the requirements is met instead of implementing it in the code of a specific
 * requirement <br>
 * (which was super labor-intensive).
 * <p>
 * <br>
 * <br>
 * This class holds multiple requirements, but only represents one 'line' in the
 * paths file.
 *
 * @author Staartvin
 */
public class RequirementsHolder {

    private final Autorank plugin;

    private List<AbstractRequirement> requirements = new ArrayList<AbstractRequirement>();

    public RequirementsHolder(final Autorank plugin) {
        this.plugin = plugin;
    }

    /**
     * Add requirement to this RequirementsHolder.
     *
     * @param req AbstractRequirement to add
     */
    public void addRequirement(final AbstractRequirement req) {
        requirements.add(req);
    }

    /**
     * Get the description of the requirements.
     * If this requirementsholder contains multiple requirements, the string will made up of the following:
     * <AbstractRequirement 1 description> OR <AbstractRequirement 2 description> OR etc.
     *
     * @return a string representing the description (or combined description) of the requirements.
     */
    public String getDescription() {
        final StringBuilder builder = new StringBuilder("");

        final List<AbstractRequirement> reqs = this.getRequirements();
        final int size = reqs.size();

        if (size == 0) {
            return "";
        } else if (size == 1) {
            return reqs.get(0).getDescription();
        }

        final String original = reqs.get(0).getDescription();

        for (int i = 0; i < size; i++) {
            final AbstractRequirement r = reqs.get(i);

            String desc = r.getDescription();

            if (i == 0) {
                // First index
                builder.append(desc + " or ");
            } else {
                // Other indices

                // Find the part of this description that is the same as the first description
                // For example, let's say we have 2 requirements: kill 2 cows or kill 10 creepers.
                // As a description, we don't want to have 'Kill 2 cows or kill 10 creepers', as the 'kill' is redundant. 
                // Hence, we remove the 'redundant' part of the description by searching for the index where the two descriptions differ
                final int difIndex = this.getDifferenceIndex(original, desc);

                // Did not find the same index
                if (difIndex < 0) {
                    continue;
                }

                // Remove the redundant part of the description string.
                // AbstractResult is 'Kill 20 cows or 20 creepers'
                desc = desc.substring(difIndex);

                if (i == (size - 1)) {
                    builder.append(desc);
                } else {
                    builder.append(desc + " or ");
                }

            }
        }

        return builder.toString();
    }

    private int getDifferenceIndex(final String s1, final String s2) {
        for (int i = 0; i < s1.length(); i++) {
            try {
                final char c1 = s1.charAt(i);
                final char c2 = s2.charAt(i);

                if (Character.isDigit(c1) || Character.isDigit(c2))
                    return i;

                if (c2 != c1)
                    return i;
            } catch (final IndexOutOfBoundsException e) {
                return -1;
            }
        }

        return -1;
    }

    /**
     * Get the progress for a player for this requirementsholder. For more info, see {@link #getDescription()}.
     *
     * @param player Player to check
     * @return progress string in the format as {@link #getDescription()}.
     */
    public String getProgress(final Player player) {
        final StringBuilder builder = new StringBuilder("");

        final List<AbstractRequirement> reqs = this.getRequirements();
        final int size = reqs.size();

        if (size == 0) {
            return "";
        } else if (size == 1) {
            return reqs.get(0).getProgress(player);
        }

        final String original = reqs.get(0).getProgress(player);

        for (int i = 0; i < size; i++) {
            final AbstractRequirement r = reqs.get(i);

            String progress = r.getProgress(player);

            if (i == 0) {
                // First index
                builder.append(progress + " or ");
            } else {

                final int difIndex = this.getDifferenceIndex(original, progress);

                progress = progress.substring(difIndex);

                if (i == (size - 1)) {
                    builder.append(progress);
                } else {
                    builder.append(progress + " or ");
                }

            }
        }

        return builder.toString();
    }

    /**
     * Get the requirement id of this requirementsholder. Since all the requiremens that are part of this requirementsholder are essentialy specified as the same
     * requirement in the Paths file, we may assume that all requirement ids are the same.
     *
     * @return the requirement id of any of the requirements of this requirementsholder.
     */
    public int getReqID() {
        // All req ids are the same.
        for (final AbstractRequirement r : this.getRequirements()) {
            return r.getId();
        }

        return -1;
    }

    public List<AbstractRequirement> getRequirements() {
        return this.requirements;
    }

    public void setRequirements(final List<AbstractRequirement> requirements) {
        this.requirements = requirements;
    }

    public List<AbstractResult> getResults() {
        for (final AbstractRequirement r : this.getRequirements()) {
            return r.getAbstractResults();
        }

        return new ArrayList<AbstractResult>();
    }

    public boolean isOptional() {
        // If any requirement is optional, they are all optional
        for (final AbstractRequirement r : this.getRequirements()) {
            if (r.isOptional())
                return true;
        }

        return false;
    }

    /**
     * Check whether a player has completed any of the requirements in this RequirementsHolder.
     *
     * @param player       Player to check.
     * @param forceCommand whether this command is forced.
     * @return true if the player meets any of the requirements.
     */
    // Check if the player meets any of the requirements
    // Using OR logic.
    // If any of the requirements is true, you can return true since were using
    // OR logic.
    public boolean meetsRequirement(final Player player, boolean forceCommand) {

        UUID uuid = player.getUniqueId();

        for (final AbstractRequirement r : this.getRequirements()) {

            final int reqID = r.getId();

            // When optional, always true
            if (r.isOptional()) {
                return true;
            }

            if (this.isPrerequisite()) {
                // If this requirement doesn't auto complete and hasn't already
                // been completed, continue to next requirement.
                if (!r.useAutoCompletion() && !r.isCompleted(uuid)) {
                    continue;
                }
            } else {
                // If this requirement doesn't auto complete and hasn't already
                // been completed, continue to next requirement.
                if (!r.useAutoCompletion() && !r.isCompleted(uuid)) {
                    // If not forcing via /ar complete command, we return false.
                    if (!forceCommand) {
                        return false;
                    }

                    continue;
                }
            }

            // Player has completed it already, so we return true.
            if (r.isCompleted(uuid)) {
                return true;
            }

            if (!r.meetsRequirement(player)) {
                continue;
            } else {
                // Player meets requirement, thus perform results of
                // requirement
                // Perform results of a requirement as well

                // Player has not completed this requirement -> perform
                // results
                if (this.isPrerequisite()) {
                    // Do nothing for now, must be implemented in some future
                } else {
                    plugin.getPlayerDataConfig().addCompletedRequirement(uuid, reqID);
                }

                if (!this.isPrerequisite()) {
                    // Let player know he completed a requirement
                    player.sendMessage(Lang.COMPLETED_REQUIREMENT.getConfigValue(r.getId() + 1, r.getDescription()));
                }

                this.runResults(player);
                return true;
            }
        }

        return false;
    }

    public boolean useAutoCompletion() {
        for (final AbstractRequirement r : this.getRequirements()) {
            if (r.useAutoCompletion())
                return true;
        }

        return false;
    }

    /**
     * Run the results of this requirementsholder (if there are any).
     *
     * @param player Player to run it for.
     */
    public void runResults(final Player player) {

        // Fire event so it can be cancelled
        // Create the event here
        final RequirementCompleteEvent event = new RequirementCompleteEvent(player, this);
        // Call the event
        Bukkit.getServer().getPluginManager().callEvent(event);

        // Check if event is cancelled.
        if (event.isCancelled())
            return;

        // Apply result
        for (final AbstractResult realAbstractResult : this.getResults()) {
            realAbstractResult.applyResult(player);
        }
    }

    /**
     * Check whether this requirementsholder is used as a prerequisite.
     *
     * @return
     */
    public boolean isPrerequisite() {
        for (AbstractRequirement req : this.requirements) {
            if (req.isPreRequisite()) {
                return true;
            }
        }

        return false;
    }
}
