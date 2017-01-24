package me.armar.plugins.autorank.pathbuilder.holders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.RequirementCompleteEvent;
import me.armar.plugins.autorank.pathbuilder.requirement.Requirement;
import me.armar.plugins.autorank.pathbuilder.result.Result;

/**
 * Since a requirement in Autorank's config can have multiple real requirements,
 * such as <br>
 * kill 10 cows OR kill 10 cats, this holder is introduced to check if either
 * one of the <br>
 * the requirements is met instead of implementing it in the code of a specific
 * requirement <br>
 * (which was super labor-intensive).
 * 
 * <br>
 * <br>
 * This class holds multiple requirements, but only represents one 'line' in the
 * advanced config.
 * 
 * @author Staartvin
 *
 */
public class RequirementsHolder {

    // Is this requirements holder used as a prerequisite
    private boolean isPrerequisite = false;

    private final Autorank plugin;

    private List<Requirement> requirements = new ArrayList<Requirement>();

    public RequirementsHolder(final Autorank plugin) {
        this.plugin = plugin;
    }

    public void addRequirement(final Requirement req) {
        requirements.add(req);
    }

    public String getDescription() {
        final StringBuilder builder = new StringBuilder("");

        final List<Requirement> reqs = this.getRequirements();
        final int size = reqs.size();

        if (size == 0) {
            return "";
        } else if (size == 1) {
            return reqs.get(0).getDescription();
        }

        final String original = reqs.get(0).getDescription();

        for (int i = 0; i < size; i++) {
            final Requirement r = reqs.get(i);

            String desc = r.getDescription();

            if (i == 0) {
                // First index
                builder.append(desc + " or ");
            } else {

                final int difIndex = this.getDifferenceIndex(original, desc);

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

    public String getProgress(final Player player) {
        final StringBuilder builder = new StringBuilder("");

        final List<Requirement> reqs = this.getRequirements();
        final int size = reqs.size();

        if (size == 0) {
            return "";
        } else if (size == 1) {
            return reqs.get(0).getProgress(player);
        }

        final String original = reqs.get(0).getProgress(player);

        for (int i = 0; i < size; i++) {
            final Requirement r = reqs.get(i);

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

    public int getReqID() {
        // All req ids are the same.
        for (final Requirement r : this.getRequirements()) {
            return r.getReqId();
        }

        return -1;
    }

    public List<Requirement> getRequirements() {
        return this.requirements;
    }

    public List<Result> getResults() {
        for (final Requirement r : this.getRequirements()) {
            return r.getResults();
        }

        return new ArrayList<Result>();
    }

    public boolean isOptional() {
        // If any requirement is optional, they are all optional
        for (final Requirement r : this.getRequirements()) {
            if (r.isOptional())
                return true;
        }

        return false;
    }

    // Check if the player meets any of the requirements
    // Using OR logic.
    // If any of the requirements is true, you can return true since were using
    // OR logic.
    public boolean meetsRequirement(final Player player, final UUID uuid, boolean forceCommand) {

        boolean result = false;

        for (final Requirement r : this.getRequirements()) {

            final int reqID = r.getReqId();

            // When optional, always true
            if (r.isOptional()) {
                return true;
            }

            if (this.isPrerequisite) {
                // If this requirement doesn't auto complete and hasn't already
                // been completed, return false;
                if (!r.useAutoCompletion() && !plugin.getPlayerDataConfig().hasCompletedPrerequisite(reqID, uuid)) {
                    return false;
                }
            } else {
                // If this requirement doesn't auto complete and hasn't already
                // been completed, return false;
                if (!r.useAutoCompletion() && !plugin.getPlayerDataConfig().hasCompletedRequirement(reqID, uuid)) {
                    // If not forcing via /ar complete command, we return false.
                   if (!forceCommand) {
                       return false;
                   }
                    
                }
            }

            if (this.isPrerequisite) {
                // Player has completed it already but this requirement is NOT
                // derankable
                // If it is derankable, we don't want this method to return true
                // when it is already completed.
                if (plugin.getPlayerDataConfig().hasCompletedPrerequisite(reqID, uuid)) {
                    return true;
                }
            } else {
                // Player has completed it already but this requirement is NOT
                // derankable
                // If it is derankable, we don't want this method to return true
                // when it is already completed.
                if (plugin.getPlayerDataConfig().hasCompletedRequirement(reqID, uuid)) {
                    return true;
                }
            }

            if (!r.meetsRequirement(player)) {
                continue;
            } else {
                // Player meets requirement, thus perform results of
                // requirement
                // Perform results of a requirement as well
                final List<Result> results = r.getResults();

                // Player has not completed this requirement -> perform
                // results
                if (this.isPrerequisite) {
                    // plugin.getPlayerDataConfig().addCompletedPrerequisite(uuid,
                    // reqID);
                } else {
                    plugin.getPlayerDataConfig().addCompletedRequirement(uuid, reqID);
                }

                boolean noErrors = true;
                for (final Result realResult : results) {

                    if (!realResult.applyResult(player)) {
                        noErrors = false;
                    }
                }

                result = noErrors;
                break; // We performed results for a requirement, so we should
                       // stop now.
            }
        }

        return result;
    }

    public void setRequirements(final List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public boolean useAutoCompletion() {
        for (final Requirement r : this.getRequirements()) {
            if (r.useAutoCompletion())
                return true;
        }

        return false;
    }

    public void runResults(final Player player) {

        // Fire event so it can be cancelled
        // Create the event here/
        // TODO Implement logic for events with RequirementHolder
        final RequirementCompleteEvent event = new RequirementCompleteEvent(player, this);
        // Call the event
        Bukkit.getServer().getPluginManager().callEvent(event);

        // Check if event is cancelled.
        if (event.isCancelled())
            return;

        // Apply result
        for (final Result realResult : this.getResults()) {
            realResult.applyResult(player);
        }
    }

    public boolean isPrerequisite() {
        return isPrerequisite;
    }

    public void setPrerequisite(boolean isPrerequisite) {
        this.isPrerequisite = isPrerequisite;
    }
}
