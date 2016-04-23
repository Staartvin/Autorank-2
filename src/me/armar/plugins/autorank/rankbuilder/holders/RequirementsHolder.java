package me.armar.plugins.autorank.rankbuilder.holders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;

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

	private Autorank plugin;

	public RequirementsHolder(Autorank plugin) {
		this.plugin = plugin;
	}

	private List<Requirement> requirements = new ArrayList<Requirement>();
	
	private boolean isDerankable = false;

	public void addRequirement(Requirement req) {
		requirements.add(req);
	}

	public List<Requirement> getRequirements() {
		return this.requirements;
	}

	// Check if the player meets any of the requirements
	// Using OR logic.
	// If any of the requirements is true, you can return true since were using
	// OR logic.
	public boolean meetsRequirement(Player player, UUID uuid) {
		
		boolean result = false;

		for (Requirement r : this.getRequirements()) {
			
			final int reqID = r.getReqId();

			// When optional, always true
			if (r.isOptional()) {
				return true;
			}

			// If this requirement doesn't auto complete and hasn't already
			// been completed, return false;
			if (!r.useAutoCompletion() && !plugin.getPlayerDataHandler().hasCompletedRequirement(reqID, uuid)) {
				return false;
			}

			// Player has completed it already but this requirement is NOT derankable
			// If it is derankable, we don't want this method to return true when it is already completed.
			if (plugin.getPlayerDataHandler().hasCompletedRequirement(reqID, uuid) && !this.isDerankable()) {
				return true;
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
				plugin.getPlayerDataHandler().addPlayerProgress(uuid, reqID);

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

	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}

	public boolean isOptional() {
		// If any requirement is optional, they are all optional
		for (Requirement r : this.getRequirements()) {
			if (r.isOptional())
				return true;
		}

		return false;
	}

	public boolean useAutoCompletion() {
		for (Requirement r : this.getRequirements()) {
			if (r.useAutoCompletion())
				return true;
		}

		return false;
	}

	public int getReqID() {
		// All req ids are the same.
		for (Requirement r : this.getRequirements()) {
			return r.getReqId();
		}

		return -1;
	}

	public List<Result> getResults() {
		for (Requirement r : this.getRequirements()) {
			return r.getResults();
		}

		return new ArrayList<Result>();
	}

	public String getDescription() {
		StringBuilder builder = new StringBuilder("");

		List<Requirement> reqs = this.getRequirements();
		int size = reqs.size();

		if (size == 0) {
			return "";
		} else if (size == 1) {
			return reqs.get(0).getDescription();
		}

		String original = reqs.get(0).getDescription();

		for (int i = 0; i < size; i++) {
			Requirement r = reqs.get(i);

			String desc = r.getDescription();

			if (i == 0) {
				// First index
				builder.append(desc + " or ");
			} else {

				int difIndex = this.getDifferenceIndex(original, desc);

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

	public String getProgress(Player player) {
		StringBuilder builder = new StringBuilder("");

		List<Requirement> reqs = this.getRequirements();
		int size = reqs.size();

		if (size == 0) {
			return "";
		} else if (size == 1) {
			return reqs.get(0).getProgress(player);
		}

		String original = reqs.get(0).getProgress(player);

		for (int i = 0; i < size; i++) {
			Requirement r = reqs.get(i);

			String progress = r.getProgress(player);

			if (i == 0) {
				// First index
				builder.append(progress + " or ");
			} else {

				int difIndex = this.getDifferenceIndex(original, progress);

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

	private int getDifferenceIndex(String s1, String s2) {
		for (int i = 0; i < s1.length(); i++) {
			try {
				char c1 = s1.charAt(i);
				char c2 = s2.charAt(i);

				if (Character.isDigit(c1) || Character.isDigit(c2))
					return i;

				if (c2 != c1)
					return i;
			} catch (IndexOutOfBoundsException e) {
				return -1;
			}
		}

		return -1;
	}

	/**
	 * Check whether a player could be deranked if he does not meet this requirement.
	 * @return true if he can be demoted, false otherwise.
	 */
	public boolean isDerankable() {
		return isDerankable;
	}

	public void setDerankable(boolean isDerankable) {
		this.isDerankable = isDerankable;
	}
}
