package me.armar.plugins.autorank.pathbuilder.result;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;

public abstract class Result {

    private Autorank plugin;

    /**
     * Apply this result to a player
     * @param player Player to result
     * @return true when applied successfully, false otherwise.
     */
    public abstract boolean applyResult(Player player);

    public final Autorank getAutorank() {
        return plugin;
    }

    /**
     * Get the description of this result
     * @return
     */
    public abstract String getDescription();

    public final void setAutorank(final Autorank autorank) {
        this.plugin = autorank;
    }

    /**
     * Initialize the options of this result. For more info, see {@link me.armar.plugins.autorank.pathbuilder.requirement.Requirement#setOptions(String[] options)}.
     * @param options Options to set for this result
     * @return true if successfully updated for this result, false otherwise
     */
    public abstract boolean setOptions(String[] options);

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
