package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import org.bukkit.entity.Player;

/**
 * This class represents a type of result that can be performed when a path is completed or a requirement is met.
 */
public abstract class AbstractResult {

    private boolean isGlobal = false;

    // Used to check if this result has a custom description
    private String customDescription;

    /**
     * Apply this result to a player
     *
     * @param player Player to result
     * @return true when applied successfully, false otherwise.
     */
    public abstract boolean applyResult(Player player);

    public final Autorank getAutorank() {
        return Autorank.getInstance();
    }

    /**
     * Get the description of this result
     *
     * @return
     */
    public abstract String getDescription();

    /**
     * Initialize the options of this result. For more info, see
     * {@link AbstractRequirement#initRequirement(String[] options)}.
     *
     * @param options Options to set for this result
     * @return true if successfully updated for this result, false otherwise
     */
    public abstract boolean setOptions(String[] options);

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * Check if this result has a custom description set
     *
     * @return true if their is custom description, false otherwise.
     */
    public boolean hasCustomDescription() {
        return getCustomDescription() != null;
    }

    /**
     * Get the custom description of this result
     *
     * @return a custom description if it was set, null otherwise.
     */
    public String getCustomDescription() {
        return this.customDescription;
    }

    /**
     * Set the custom description of this result
     *
     * @param description the description to set it to.
     */
    public void setCustomDescription(String description) {
        this.customDescription = description;
    }

    /**
     * Check whether this result is global. When a result is global, it will only be performed if it hasn't been
     * performed on any other server that is linked to the same database as this instance of Autorank.
     *
     * @return true if this result is global, false otherwise.
     */
    public boolean isGlobal() {
        return isGlobal;
    }

    /**
     * Set whether this result is global. See {@link #isGlobal()} for more details.
     *
     * @param global Whether this result is global.
     */
    public void setGlobal(boolean global) {
        isGlobal = global;
    }
}
