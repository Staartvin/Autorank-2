package me.armar.plugins.autorank.rankbuilder;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.entity.Player;

/**
 * Represents a group of changes, including all requirements and results.
 * <p>
 * Date created: 14:23:30 5 aug. 2015
 * 
 * @author Staartvin
 * 
 */
public class ChangeGroup {

	private List<Requirement> requirements;
	private List<Result> results;

	// Parent group is the group that a player must be in for this ChangeGroup to have effect.
	// Internal group is the internally used name (in the config) of this ChangeGroup. It's linked to the parentGroup but has to be unique in the config.
	// It therefore always contains a '-copy-*' part.
	// The display name is the name shown when this changegroup is a copy of another changegroup.
	private String parentGroup, internalGroup, displayName;

	private final Autorank plugin;

	public ChangeGroup(final Autorank plugin, final List<Requirement> reqs,
			final List<Result> results) {
		this.plugin = plugin;
		this.setRequirements(reqs);
		this.setResults(results);
	}

	public ChangeGroup(final Autorank plugin) {
		this.plugin = plugin;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(final List<Requirement> requirements) {
		this.requirements = requirements;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(final List<Result> results) {
		this.results = results;
	}

	public String getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(final String parentGroup) {
		this.parentGroup = parentGroup;
	}

	public String getInternalGroup() {
		return internalGroup;
	}

	public void setInternalGroup(final String internalGroup) {
		this.internalGroup = internalGroup;
	}

	public boolean applyChange(final Player player) {
		boolean result = true;

		if (checkRequirements(player)) {

			final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

			// Apply all 'main' results

			// Player already got this rank
			if (plugin.getPlayerDataHandler().hasCompletedRank(uuid,
					parentGroup)) {
				return false;
			}

			// Add progress of completed requirements
			plugin.getPlayerDataHandler().addCompletedRanks(uuid, parentGroup);

			for (final Result r : this.getResults()) {
				if (r != null) {
					if (!r.applyResult(player)) {
						result = false;
					}
				}
			}
		} else {
			result = false;
		}

		return result;
	}

	public boolean checkRequirements(final Player player) {
		boolean result = true;

		final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

		// Player already got this rank
		if (plugin.getPlayerDataHandler().hasCompletedRank(uuid, parentGroup)) {
			return false;
		}

		for (final Requirement r : this.getRequirements()) {
			if (r == null)
				return false;

			final int reqID = r.getReqId();

			// When optional, always true
			if (r.isOptional())
				continue;

			// We don't do partial completion so we only need to check if a player passes all requirements.
			if (!plugin.getConfigHandler().usePartialCompletion()) {
				if (!r.meetsRequirement(player)) {
					return false;
				} else {
					continue;
				}
			}

			// If this requirement doesn't auto complete and hasn't already been completed, return false;
			if (!r.useAutoCompletion()
					&& !plugin.getPlayerDataHandler().hasCompletedRequirement(
							reqID, uuid)) {
				return false;
			}

			if (!r.meetsRequirement(player)) {

				// Player does not meet requirement, but has completed it already
				if (plugin.getPlayerDataHandler().hasCompletedRequirement(
						reqID, uuid)) {
					continue;
				}

				return false;
			} else {
				// Player meets requirement, thus perform results of requirement
				// Perform results of a requirement as well
				final List<Result> results = r.getResults();

				// Player has not completed this requirement -> perform results
				if (!plugin.getPlayerDataHandler().hasCompletedRequirement(
						reqID, uuid)) {
					plugin.getPlayerDataHandler()
							.addPlayerProgress(uuid, reqID);
				} else {
					// Player already completed this -> do nothing
					continue;
				}

				boolean noErrors = true;
				for (final Result realResult : results) {

					if (!realResult.applyResult(player)) {
						noErrors = false;
					}
				}
				result = noErrors;
			}
		}

		return result;
	}

	public List<Requirement> getFailedRequirements(final Player player) {
		final List<Requirement> failed = new CopyOnWriteArrayList<Requirement>();
		failed.addAll(this.getRequirements());

		for (final Requirement r : failed) {
			if (r != null)
				if (r.meetsRequirement(player)) {
					failed.remove(r);
				}
		}

		return failed;
	}

	@Override
	public String toString() {
		return displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}
}
