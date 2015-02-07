package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;

import org.bukkit.entity.Player;

/**
 * Whenever you want to create a new requirement, you'll have to extend this
 * class.
 * Every requirement needs the following:
 * 
 * <p>
 * - Option to check if the requirement is optional.
 * <p>
 * - Results that will be performed when the requirement is completed. These
 * results have to be one of the results registered in Autorank.
 * <p>
 * - Option to check if the requirement will auto complete.
 * 
 * @author Staartvin
 * 
 */
public abstract class Requirement {

	private Autorank autorank;
	private boolean optional = false, autoComplete = false;
	private int reqId;
	private List<Result> results = new ArrayList<Result>();

	public final Autorank getAutorank() {
		return autorank;
	}

	/**
	 * Gets the dependency manager of Autorank that is used to connect to other
	 * plugins. <br>
	 * Can be used to get other information off of other plugins.
	 * 
	 * @return DependencyManager class
	 */
	public final DependencyManager getDependencyManager() {
		return autorank.getDependencyManager();
	}

	/**
	 * Gets the description of the requirement
	 * Make sure this is always a translatable message.
	 * 
	 * @return string containing description (in locale language)
	 */
	public abstract String getDescription();

	/**
	 * Gets the current progress of a player on a certain requirement.
	 * 
	 * @param player Player to check for
	 * @return String containing the progress
	 */
	public abstract String getProgress(Player player);

	/**
	 * Get the id of this requirement.
	 * This should get assigned automatically at setOptions().
	 * The id should always be dynamic.
	 * 
	 * @return id
	 */
	public int getReqId() {
		return reqId;
	}

	/**
	 * Gets the results when this requirement is finished
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
		return autorank.getHookedStatsPlugin();
	}

	/**
	 * Check if the requirement is completed already.
	 * 
	 * @param reqID Requirement id.
	 * @param uuid Player to check for
	 * @return true if completed, false otherwise.
	 */
	public final boolean isCompleted(final int reqID, final UUID uuid) {
		return autorank.getRequirementHandler().hasCompletedRequirement(reqID,
				uuid);
	}

	/**
	 * Is this an optional requirement?
	 * (Not a main requirement)
	 * 
	 * @return true when optional; false otherwise.
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Does it meet the requirements?
	 * This method gets called when someone does /ar check or /ar complete.
	 * It should always contain the following line:
	 * 
	 * <p>
	 * if (isCompleted(getReqId(), player.getName())) { return true; }
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

	public final void setAutorank(final Autorank autorank) {
		this.autorank = autorank;
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
	 * Setup requirement specific objects.
	 * 
	 * This method is called when Autorank sets up its config. <br>
	 * The requirement id, auto completion and optional values are assigned
	 * automatically.
	 * 
	 * @param options Each element in the list contains an array that has all variables in it.
	 * @return true if everything was setup correctly; false otherwise
	 */
	public abstract boolean setOptions(List<String[]> optionsList);

	/**
	 * Set the requirement id of this requirement
	 * 
	 * @param reqId id to set it to
	 */
	public void setReqId(final int reqId) {
		this.reqId = reqId;
	}

	/**
	 * Set the results of this requirement. <br>
	 * These results will be performed when this requirement is met.
	 * 
	 * @param results results to perform upon completion
	 */
	public void setResults(final List<Result> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Use auto completion for this?
	 * 
	 * @return true when auto complete; false otherwise
	 */
	public boolean useAutoCompletion() {
		return autoComplete;
	}
}
