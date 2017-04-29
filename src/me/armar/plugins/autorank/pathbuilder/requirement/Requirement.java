package me.armar.plugins.autorank.pathbuilder.requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.pathbuilder.result.Result;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;

/**
 * Whenever you want to create a new requirement, you'll have to extend this
 * class.
 * 
 * A requirement can be seen as a task a player has to complete. A path consists of multiple requirements that should all be met to complete the path.
 * 
 * @author Staartvin
 * 
 */
public abstract class Requirement {

    private boolean optional = false, autoComplete = false, isPreRequisite = false;
    private int reqId;
    private List<Result> results = new ArrayList<Result>();
    private String world = null;

    public final Autorank getAutorank() {
        return Autorank.getInstance();
    }

    /**
     * Get the Dependencymanager of Autorank that is used to connect to other
     * plugins. <br>
     * Can be used to get information from other plugins.
     * 
     * @return DependencyManager class
     */
    public final DependencyManager getDependencyManager() {
        return getAutorank().getDependencyManager();
    }

    /**
     * Get the description of the requirement. Make sure this is always a
     * translatable message.
     * 
     * @return string containing description (in locale language)
     */
    public abstract String getDescription();

    /**
     * Get the current progress of a player on a certain requirement.
     * 
     * @param player
     *            Player to check for
     * @return String containing the progress
     */
    public abstract String getProgress(Player player);

    /**
     * Get the id of this requirement. This should get assigned automatically at
     * setOptions(). The id should always be dynamic.
     * 
     * @return id
     */
    public int getId() {
        return reqId;
    }

    /**
     * Get the results when this requirement is finished
     * 
     * @return A list of results that has to be done.
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * Get the current running stats plugin.
     * 
     * @return stats plugin that Autorank uses for stat data
     */
    public StatsPlugin getStatsPlugin() {
        return getAutorank().getHookedStatsPlugin();
    }

    /**
     * If requirement is world specific, what world does it apply to?
     * 
     * @return the world that this requirement is specific to.
     */
    public String getWorld() {
        return world;
    }

    /**
     * Check if the requirement is completed already.
     * 
     * @param reqID
     *            Requirement id.
     * @param uuid
     *            Player to check for
     * @return true if completed, false otherwise.
     */
    public final boolean isCompleted(final int reqID, final UUID uuid) {
        return getAutorank().getPlayerDataConfig().hasCompletedRequirement(reqID, uuid);
    }

    /**
     * Check whether this requirement is optional.
     * 
     * @return true when optional; false otherwise.
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Check whether this requirement is world specific.
     * 
     * @return true if it is, false otherwise.
     */
    public boolean isWorldSpecific() {
        return world != null;
    }

    /**
     * Check whether a player meets this requirement. If a requirement is
     * optional, a player will always meet the requirement.
     * 
     * @param player
     *            Player to check for
     * @return true if it meets the requirements; false otherwise
     */
    public abstract boolean meetsRequirement(Player player);

    /**
     * Set whether this requirement auto completes itself
     * 
     * @param autoComplete
     *            true if auto complete; false otherwise
     */
    public void setAutoComplete(final boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    /**
     * Set whether this requirement is optional or not
     * 
     * @param optional
     *            true if optional; false otherwise
     */
    public void setOptional(final boolean optional) {
        this.optional = optional;
    }

    /**
     * Set up a requirement. You should initiliaze the requirement with an empty constructor.
     * Secondly, the {@link #setOptions(String[])} method must be called to supply the requirement with data.
     * Lastly, you can use {@link #meetsRequirement(Player)} to check whether a player meets the requirement.
     * 
     * The options parameter is an array that will contain the string as passed through the Paths.yml
     * 
     * @param options
     *            Each element is an element supplied by the config.
     * @return true if everything was setup correctly; false otherwise
     */
    public abstract boolean setOptions(String[] options);

    /**
     * Set the requirement id of this requirement
     * 
     * @param reqId
     *            id to set it to
     */
    public void setId(final int reqId) {
        this.reqId = reqId;
    }

    /**
     * Set the results of this requirement. <br>
     * These results will be performed when this requirement is met.
     * 
     * @param results
     *            results to perform upon completion
     */
    public void setResults(final List<Result> results) {
        this.results = results;
    }

    /**
     * Sets the world that this requirement is specific to.
     * 
     * @param world
     *            name of the world
     */
    public void setWorld(final String world) {
        this.world = world;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * Check whether this requirement will automatically complete.
     * 
     * @return true when auto complete; false otherwise
     */
    public boolean useAutoCompletion() {
        return autoComplete;
    }

    /**
     * Check whether this requirement is a prerequisite.
     * @return true if it is a prerequisite, false otherwise.
     */
    public boolean isPreRequisite() {
        return isPreRequisite;
    }

    /**
     * Set whether this requirement is a prerequisite requirement.
     * @param preRequisite value to set it to.
     */
    public void setPreRequisite(boolean preRequisite) {
        isPreRequisite = preRequisite;
    }
}
