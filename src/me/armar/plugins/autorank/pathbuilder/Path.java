package me.armar.plugins.autorank.pathbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.result.Result;

/**
 * Represents a path that a player can take, including all requirements and
 * results.
 * <p>
 * Date created: 14:23:30 5 aug. 2015
 * 
 * @author Staartvin
 * 
 */
public class Path {

	// The display name is the name that will be shown to the player.
	private String displayName = "";
	private final Autorank plugin;
	// A requirements holder is a holder for one or more requirements that can be met simultaneously. 
	private List<RequirementsHolder> prerequisites = new ArrayList<>();

	private List<RequirementsHolder> requirements = new ArrayList<RequirementsHolder>();

	private List<Result> results = new ArrayList<Result>();

	public Path(final Autorank plugin) {
		this.plugin = plugin;
	}

	public void addPrerequisite(final RequirementsHolder prerequisite) {
		this.prerequisites.add(prerequisite);
	}

	public void addRequirement(final RequirementsHolder requirement) {
		requirements.add(requirement);
	}

	public void addResult(Result result) {
		this.results.add(result);
	}

	public boolean applyChange(final Player player) {
		boolean result = true;

		/*if (this.checkDerankableRequirements(player)) {
			return false;
		}*/

		if (meetRequirements(player)) {

			// final UUID uuid =
			// UUIDManager.getUUIDFromPlayer(player.getName());
			final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

			// Apply all 'main' results

			// Get chosen path of player
			Path currentPath = plugin.getPathManager().getCurrentPath(uuid);

			if (currentPath == null) {
				return false;
			}

			// Player already got this path
			if (plugin.getPlayerDataConfig().hasCompletedPath(uuid, currentPath.getDisplayName())) {
				return false;
			}

			// Add progress of completed requirements
			plugin.getPlayerDataConfig().addCompletedPath(uuid, currentPath.getDisplayName());

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

	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Check whether a player should be deranked based on its requirements
	 * 
	 * @param player Player to check.
	 * @return true if the player will be deranked, false otherwise.
	 */
	/*public boolean checkDerankableRequirements(final Player player) {
		// Player can never be deranked with this option set to false.
		if (!plugin.getConfigHandler().allowDeranking())
			return false;
	
		// final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());
	
		for (final RequirementsHolder holder : this.getRequirements()) {
			if (holder == null)
				continue;
	
			// Holder does not meet requirements, so not all requirements are
			// met!
			if (!holder.meetsRequirement(player, uuid)) {
				if (holder.isDerankable()) {
					// Does not meet requirement and is derankable, so demote.
	
					// We don't know the previous group, so we can't demote.
					if (this.getPreviousGroup() == null) {
						continue;
					}
	
					plugin.debugMessage("Demote player " + player.getName() + " to " + this.getPreviousGroup()
							+ " since he doesn't meet a requirement (that requirement is also derankable).");
	
					// When rank is changed: reset progress and update last
					// known group
					plugin.getPlayerDataHandler().setPlayerProgress(uuid, new ArrayList<Integer>());
	
					plugin.getPlayerDataHandler().setLastKnownGroup(uuid, this.getPreviousGroup());
	
					// Reset chosen path as the player is moved to another group
					plugin.getPlayerDataHandler().setChosenPath(uuid, null);
	
					plugin.getPermPlugHandler().getPermissionPlugin().demotePlayer(player, null, this.getParentGroup(),
							this.getPreviousGroup());
	
					// Find the commands that have to be run
					final List<String> commands = plugin.getConfigHandler().getCommandsOnDerank(this.getParentGroup(),
							plugin.getConfigHandler().getRequirementNameOfId(this.getParentGroup(), holder.getReqID()));
	
					// Run the commands
					for (final String command : commands) {
						final String cmd = command.replace("&p", player.getName());
	
						if (!Bukkit.isPrimaryThread()) {
							plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
	
								@Override
								public void run() {
									// Run command sync if we are currently not in main thread.
									plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
								}
	
							});
						} else {
							plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
						}
	
					}
	
					// Send player message telling them they have deranked
					player.sendMessage(
							Lang.DERANK_MESSAGE.getConfigValue(this.getPreviousGroup(), holder.getDescription()));
					return true;
				}
			}
	
		}
	
		// When never returning true, return true at false!
		return false;
	}*/

	public List<RequirementsHolder> getFailedRequirementsHolders(final Player player) {
		final List<RequirementsHolder> holders = new ArrayList<RequirementsHolder>();

		for (final RequirementsHolder holder : this.getRequirements()) {
			if (holder != null)
				if (holder.meetsRequirement(player, player.getUniqueId())) {
					holders.add(holder);
				}
		}

		return holders;
	}

	public List<RequirementsHolder> getPrerequisites() {
		return requirements;
	}

	public List<RequirementsHolder> getRequirements() {
		return requirements;
	}

	// // Grabs all requirements of all holders
	// public List<Requirement> getAllRequirements() {
	// List<Requirement> requirements = new ArrayList<Requirement>();
	//
	// for (RequirementsHolder holder: this.getRequirementsHolders()) {
	// requirements.addAll(holder.getRequirements());
	// }
	//
	// return requirements;
	// }

	public List<Result> getResults() {
		return results;
	}

	public boolean meetRequirements(final Player player) {

		// final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

		// Get chosen path of player
		Path currentPath = plugin.getPathManager().getCurrentPath(uuid);

		if (currentPath == null) {
			return false;
		}

		// Player already completed this path
		if (plugin.getPlayerDataConfig().hasCompletedPath(uuid, currentPath.getDisplayName())) {
			return false;
		}

		for (final RequirementsHolder holder : this.getRequirements()) {
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

			// Holder does not meet requirements, so not all requirements are
			// met!
			if (!holder.meetsRequirement(player, uuid)) {
				return false;
			}

		}

		// When never returning false, return true at last!
		return true;
	}

	public boolean meetsPrerequisites(Player player) {

		List<RequirementsHolder> preRequisites = this.getPrerequisites();

		for (RequirementsHolder preRequisite : preRequisites) {
			if (!preRequisite.meetsRequirement(player, player.getUniqueId())) {
				// If one of the prerequisites does not hold, a player does not meet all the prerequisites.
				return false;
			}
		}

		return true;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setRequirements(final List<RequirementsHolder> holders) {
		this.requirements = holders;
	}

	public void setResults(final List<Result> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
