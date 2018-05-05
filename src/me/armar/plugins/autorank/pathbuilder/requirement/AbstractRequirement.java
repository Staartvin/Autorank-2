package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.staartvin.plugins.pluginlibrary.Library;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Whenever you want to create a new requirement, you'll have to extend this
 * class.
 * <p>
 * A requirement can be seen as a task a player has to complete. A path consists of multiple requirements that should all be met to complete the path.
 *
 * @author Staartvin
 */
public abstract class AbstractRequirement {

    private boolean optional = false, autoComplete = false, isPreRequisite = false;
    private int reqId;
    private List<AbstractResult> abstractResults = new ArrayList<AbstractResult>();
    private String world, customDescription;
    private List<String> errorMessages = new ArrayList<>();

    // A list of third-party plugins that are needed to use this requirement.
    private List<Library> dependencies = new ArrayList<>();

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
     * @param player Player to check for
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
     * Set the requirement id of this requirement
     *
     * @param reqId id to set it to
     */
    public void setId(final int reqId) {
        this.reqId = reqId;
    }

    /**
     * Get the abstractResults when this requirement is finished
     *
     * @return A list of abstractResults that has to be done.
     */
    public List<AbstractResult> getAbstractResults() {
        return abstractResults;
    }

    /**
     * Set the abstractResults of this requirement. <br>
     * These abstractResults will be performed when this requirement is met.
     *
     * @param abstractResults abstractResults to perform upon completion
     */
    public void setAbstractResults(final List<AbstractResult> abstractResults) {
        this.abstractResults = abstractResults;
    }

    /**
     * Get the current running stats plugin.
     *
     * @return stats plugin that Autorank uses for stat storage
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
     * Sets the world that this requirement is specific to.
     *
     * @param world name of the world
     */
    public void setWorld(final String world) {
        this.world = world;
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
     * Set whether this requirement is optional or not
     *
     * @param optional true if optional; false otherwise
     */
    public void setOptional(final boolean optional) {
        this.optional = optional;
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
     * @param player Player to check for
     * @return true if it meets the requirements; false otherwise
     */
    public abstract boolean meetsRequirement(Player player);

    /**
     * Set whether this requirement auto completes itself
     *
     * @param autoComplete true if auto complete; false otherwise
     */
    public void setAutoComplete(final boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    /**
     * Set up a requirement. You should initiliaze the requirement with an empty constructor.
     * Secondly, the {@link #setOptions(String[])} method must be called to supply the requirement with storage.
     * Lastly, you can use {@link #meetsRequirement(Player)} to check whether a player meets the requirement.
     * <p>
     * The options parameter is an array that will contain the string as passed through the Paths.yml
     *
     * @param options Each element is an element supplied by the config.
     * @return true if everything was setup correctly; false otherwise
     */
    public abstract boolean setOptions(String[] options);

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
     *
     * @return true if it is a prerequisite, false otherwise.
     */
    public boolean isPreRequisite() {
        return isPreRequisite;
    }

    /**
     * Set whether this requirement is a prerequisite requirement.
     *
     * @param preRequisite value to set it to.
     */
    public void setPreRequisite(boolean preRequisite) {
        isPreRequisite = preRequisite;
    }

    /**
     * Add an error message to clarify what went wrong with this requirement.
     * For example, the requirement was not correctly specified, incorrect storage was provided, insufficient storage
     * was provided, etc.
     *
     * @param message Message to add.
     */
    public void registerWarningMessage(String message) {
        if (message == null) {
            return;
        }

        if (!errorMessages.contains(message)) {
            this.errorMessages.add(message);
        }
    }

    /**
     * Get the error messages that were registered by the requirement.
     * Note that, usually, error messages are only registered after the {@link #setOptions(String[])} method is called.
     * Hence, calling {@link #getErrorMessages()} before calling {@link #setOptions(String[])} is useless.
     *
     * @return a list of error messages.
     */
    public List<String> getErrorMessages() {
        return this.errorMessages;
    }


    /**
     * Get a list of dependencies (third-party plugins) this requirement uses.
     *
     * @return a list of dependencies of this requirement.
     */
    public List<Library> getDependencies() {
        return dependencies;
    }

    /**
     * Add a dependency for this requirement.
     *
     * @param library Library to add as a dependency
     */
    public void addDependency(Library library) {
        if (library == null) {
            return;
        }

        dependencies.add(library);
    }

    /**
     * Check whether this requirement has a custom description by an admin.
     *
     * @return true if there is a custom description, false otherwise.
     */
    public boolean hasCustomDescription() {
        return getCustomDescription() != null;
    }

    public String getCustomDescription() {
        return this.customDescription;
    }

    /**
     * Set the custom description of this requirement
     *
     * @param description custom description
     */
    public void setCustomDescription(String description) {
        this.customDescription = description;
    }
}
