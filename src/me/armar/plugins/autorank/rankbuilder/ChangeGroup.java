package me.armar.plugins.autorank.rankbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.rankbuilder.holders.RequirementsHolder;

/**
 * Represents a group of changes, including all requirements and results.
 * <p>
 * Date created: 14:23:30 5 aug. 2015
 * 
 * @author Staartvin
 * 
 */
public class ChangeGroup {

	// A requirementsholder represents one or more requirements tied together
	private List<RequirementsHolder> requirementsHolders = new ArrayList<RequirementsHolder>();
	private List<Result> results = new ArrayList<Result>();

	// Parent group is the group that a player must be in for this ChangeGroup
	// to have effect.
	// Internal group is the internally used name (in the config) of this
	// ChangeGroup. It's linked to the parentGroup but has to be unique in the
	// config.
	// It therefore always contains a '-copy-*' part.
	// The display name is the name shown when this changegroup is a copy of
	// another changegroup.
	private String parentGroup, internalGroup, displayName;

	private final Autorank plugin;

	public ChangeGroup(final Autorank plugin, final List<RequirementsHolder> holders, final List<Result> results) {
		this.plugin = plugin;
		this.setRequirementHolders(holders);
		this.setResults(results);
	}

	public ChangeGroup(final Autorank plugin) {
		this.plugin = plugin;
	}

	public List<RequirementsHolder> getRequirementsHolders() {
		return requirementsHolders;
	}

	/*
	 * public void setRequirements(final List<Requirement> requirements) {
	 * this.requirements = requirements; }
	 */

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

			// final UUID uuid =
			// UUIDManager.getUUIDFromPlayer(player.getName());
			UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

			// Apply all 'main' results

			// Player already got this rank
			if (plugin.getPlayerDataHandler().hasCompletedRank(uuid, parentGroup)) {
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
		
		// final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

		// Player already got this rank
		if (plugin.getPlayerDataHandler().hasCompletedRank(uuid, parentGroup)) {
			return false;
		}

		for (final RequirementsHolder holder : this.getRequirementsHolders()) {
			if (holder == null)
				return false;

			// We don't do partial completion so we only need to check if a
			// player passes all requirements holders.
			if (!plugin.getConfigHandler().usePartialCompletion()) {
				if (!holder.meetsRequirement(player, uuid)) {
					return false;
				} else {
					continue;
				}
			}
			
			// Holder does not meet requirements, so not all requirements are met!
			if (!holder.meetsRequirement(player, uuid)) {
				return false;
			}
		
		}

		// When never returning false, return true at last!
		return true;
	}

	public List<RequirementsHolder> getFailedRequirementsHolders(final Player player) {
		final List<RequirementsHolder> holders = new ArrayList<RequirementsHolder>();

		for (RequirementsHolder holder : this.getRequirementsHolders() ) {
			if (holder != null)
				if (holder.meetsRequirement(player, player.getUniqueId())) {
					holders.add(holder);
				}
		}

		return holders;
	}
	
//	// Grabs all requirements of all holders
//	public List<Requirement> getAllRequirements() {
//		List<Requirement> requirements = new ArrayList<Requirement>();
//		
//		for (RequirementsHolder holder: this.getRequirementsHolders()) {
//			requirements.addAll(holder.getRequirements());
//		}
//		
//		return requirements;
//	}
	
	public void setRequirementHolders(List<RequirementsHolder> holders) {
		this.requirementsHolders = holders;
	}
	
	public void addRequirementHolder(RequirementsHolder holder) {
		requirementsHolders.add(holder);
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
