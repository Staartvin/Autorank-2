package me.armar.plugins.autorank.playerchecker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.rankbuilder.ChangeGroup;
import me.armar.plugins.autorank.rankbuilder.ChangeGroupManager;
import me.armar.plugins.autorank.util.AutorankTools;

/*
 * PlayerChecker is where the magic happens :P It has a RankChangeBuilder that reads 
 * the config and makes new RankChange objects. It sends the names of the needed results 
 * and requirements to AdditionalRequirementBuilder and ResultBuilder. Those are dynamic 
 * factories because they don't have any hardcoded classes to build. You register all 
 * the requirements or results when the plugin is started. Because of this other 
 * plugins / addons can register their own custom requirements and results very easily.
 * 
 * So: PlayerChecker has a list of RankChanges and a RankChange has a list of AdditionalRequirement and Results.
 * 
 */
public class PlayerChecker {

	private final Autorank plugin;
	private ChangeGroupManager changeGroupManager;

	//private final Map<String, List<RankChange>> rankChanges = new HashMap<String, List<RankChange>>();

	public PlayerChecker(final Autorank plugin) {
		this.plugin = plugin;
		this.changeGroupManager = new ChangeGroupManager(plugin);
	}

	/*public void addRankChange(final String name, final RankChange change) {
		if (rankChanges.get(name) == null) {
			final List<RankChange> list = new ArrayList<RankChange>();
			list.add(change);
			rankChanges.put(name, list);
		} else {
			rankChanges.get(name).add(change);
		}
	}*/

	public boolean checkPlayer(final Player player) {

		// Do not rank a player when he is excluded
		if (AutorankTools.isExcluded(player))
			return false;

		// only first group - will cause problems
		String groupName = plugin.getPermPlugHandler().getPrimaryGroup(player);

		List<ChangeGroup> changes = changeGroupManager
				.getChangeGroups(groupName);

		if (changes == null || changes.size() == 0) {
			return false;
		}

		String chosenPath = plugin.getPlayerDataHandler().getChosenPath(
				player.getUniqueId());

		if (!plugin.getPlayerDataHandler().checkValidChosenPath(player)) {
			chosenPath = "unknown";
		}

		ChangeGroup changeGroup = this.getChangeGroupManager()
				.matchChangeGroup(groupName, chosenPath);

		if (changeGroup == null)
			return false;

		// TODO account for chosen path

		return changeGroup.applyChange(player);
	}

	public List<Requirement> getAllRequirements(final Player player) {

		// only first group - will cause problems
		String groupName = plugin.getPermPlugHandler().getPrimaryGroup(player);

		ChangeGroup chosenChangeGroup = changeGroupManager.matchChangeGroup(
				groupName,
				plugin.getPlayerDataHandler().getChosenPath(
						player.getUniqueId()));

		if (chosenChangeGroup == null) {

			// Get all requirements of all changegroups together
			List<Requirement> reqs = new ArrayList<Requirement>();

			for (ChangeGroup changeGroup : changeGroupManager
					.getChangeGroups(groupName)) {
				for (Requirement req : changeGroup.getRequirements()) {
					reqs.add(req);
				}
			}

			return reqs;

		}

		return chosenChangeGroup.getRequirements();

		/*final List<RankChange> changes = rankChanges.get(group);
		if (changes != null) {
			for (final RankChange change : changes) {
				result.put(change, change.getReq());
			}
		}*/
	}

	public List<Requirement> getFailedRequirements(final Player player) {

		// only first group - will cause problems
		String groupName = plugin.getPermPlugHandler().getPrimaryGroup(player);

		ChangeGroup chosenChangeGroup = changeGroupManager.matchChangeGroup(
				groupName,
				plugin.getPlayerDataHandler().getChosenPath(
						player.getUniqueId()));

		if (chosenChangeGroup == null) {

			// Get all requirments of all changegroups together
			List<Requirement> reqs = new ArrayList<Requirement>();

			for (ChangeGroup changeGroup : changeGroupManager
					.getChangeGroups(groupName)) {
				for (Requirement req : changeGroup
						.getFailedRequirements(player)) {
					reqs.add(req);
				}
			}

			return reqs;
		}

		return chosenChangeGroup.getFailedRequirements(player);

		/*final List<RankChange> changes = rankChanges.get(group);
		if (changes != null) {
			for (final RankChange change : changes) {
				result.put(change, change.getReq());
			}
		}*/
	}

	public List<String> toStringList() {
		return changeGroupManager.debugChangeGroups(true);
	}

	public void doLeaderboardExemptCheck(Player player) {
		plugin.getPlayerDataHandler().hasLeaderboardExemption(
				player.getUniqueId(),
				player.hasPermission("autorank.leaderboard.exempt"));
	}

	public ChangeGroupManager getChangeGroupManager() {
		return changeGroupManager;
	}

	public List<String> getRequirementsInStringList(
			final List<Requirement> reqs, final List<Integer> metRequirements) {
		// Converts requirements into a list of readable requirements

		final List<String> messages = new ArrayList<String>();

		messages.add(ChatColor.GRAY + " ------------ ");

		for (int i = 0; i < reqs.size(); i++) {
			final Requirement req = reqs.get(i);
			final int reqID = req.getReqId();

			if (req != null) {
				final StringBuilder message = new StringBuilder("     "
						+ ChatColor.GOLD + (i + 1) + ". ");
				if (metRequirements.contains(reqID)) {
					message.append(ChatColor.RED + req.getDescription()
							+ ChatColor.BLUE + " ("
							+ Lang.DONE_MARKER.getConfigValue() + ")");
				} else {
					message.append(ChatColor.RED + req.getDescription());
				}

				if (req.isOptional()) {
					message.append(ChatColor.AQUA + " ("
							+ Lang.OPTIONAL_MARKER.getConfigValue() + ")");
				}

				messages.add(message.toString());

			}
		}

		return messages;

	}

	public List<Integer> getMetRequirements(List<Requirement> reqs,
			Player player) {
		final List<Integer> metRequirements = new ArrayList<Integer>();

		boolean onlyOptional = true;

		// Check if we only have optional requirements
		for (final Requirement req : reqs) {
			if (!req.isOptional())
				onlyOptional = false;
		}

		if (onlyOptional) {
			List<Integer> optionalRequirements = new ArrayList<Integer>();

			for (Requirement req : reqs) {
				optionalRequirements.add(req.getReqId());
			}

			return optionalRequirements;
		}

		for (final Requirement req : reqs) {
			final int reqID = req.getReqId();

			// Use auto completion
			if (req.useAutoCompletion()) {
				// Do auto complete
				if (req.meetsRequirement(player)) {
					// Player meets the requirement -> give him results

					// Doesn't need to check whether this requirement was already done
					if (!plugin.getConfigHandler().usePartialCompletion())
						continue;

					metRequirements.add(reqID);
					continue;
				} else {

					// Only check if player has done this when partial completion is used
					if (plugin.getConfigHandler().usePartialCompletion()) {
						// Player does not meet requirements, but has done this already
						if (plugin.getPlayerDataHandler()
								.hasCompletedRequirement(reqID,
										player.getUniqueId())) {
							metRequirements.add(reqID);
							continue;
						}
					}

					// If requirement is optional, we do not check.
					if (req.isOptional()) {
						continue;
					}

					// Player does not meet requirements -> do nothing
					continue;
				}
			} else {

				if (!plugin.getConfigHandler().usePartialCompletion()) {

					// Doesn't auto complete and doesn't meet requirement, then continue searching
					if (!req.meetsRequirement(player)) {

						// If requirement is optional, we do not check.
						if (req.isOptional()) {
							continue;
						}

						continue;
					} else {
						// Player does meet requirement, continue searching
						continue;
					}

				}

				// Do not auto complete
				if (plugin.getPlayerDataHandler().hasCompletedRequirement(
						reqID, player.getUniqueId())) {
					// Player has completed requirement already
					metRequirements.add(reqID);
					continue;
				} else {

					// If requirement is optional, we do not check.
					if (req.isOptional()) {
						continue;
					}

					continue;
				}
			}
		}
		return metRequirements;
	}

}
