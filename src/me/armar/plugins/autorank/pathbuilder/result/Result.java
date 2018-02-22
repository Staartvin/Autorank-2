package me.armar.plugins.autorank.pathbuilder.result;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.entity.Player;

/**
 * This class represents a type of result that can be performed when a path is completed or a requirement is met.
 */
public abstract class Result {

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
     * Initialize the options of this result. For more info, see {@link me.armar.plugins.autorank.pathbuilder.requirement.Requirement#setOptions(String[] options)}.
     *
     * @param options Options to set for this result
     * @return true if successfully updated for this result, false otherwise
     */
    public abstract boolean setOptions(String[] options);

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
