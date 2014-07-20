package me.armar.plugins.autorank.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.entity.Player;

/**
 * <b>Autorank's API class:</b>
 * <p>
 * You, as a developer, can you use this class to get data from players or data
 * about groups. The API is never finished and if you want to see something
 * added, tell us!
 * <p>
 * 
 * @author Staartvin
 * 
 */
public class API {

	private final Autorank plugin;

	public API(final Autorank instance) {
		plugin = instance;
	}

	/**
	 * Get the addon manager of Autorank.
	 * <p>
	 * This class stores information about the loaded addons
	 * 
	 * @return {@link me.armar.plugins.autorank.addons.AddOnManager} class
	 */
	public AddOnManager getAddonManager() {
		return plugin.getAddonManager();
	}

	/**
	 * Gets all requirements for a player at the exact moment.
	 * This does not consider already finished requirement but just mirrors the
	 * config file.
	 * 
	 * @param player Player to get the requirements from.
	 * @return a list of requirements; An empty list when none are found.
	 */
	public List<Requirement> getAllRequirements(final Player player) {
		final Map<RankChange, List<Requirement>> failed = plugin
				.getPlayerChecker().getAllRequirements(player);

		final Set<RankChange> keySet = failed.keySet();
		List<Requirement> reqs = new ArrayList<Requirement>();

		for (final RankChange rank : keySet) {
			reqs = failed.get(rank);
		}

		return reqs;
	}

	/**
	 * Gets all requirements that are not yet completed.
	 * 
	 * @param player Player to get the failed requirements for.
	 * @return list of requirements that still have to be completed.
	 */
	public List<Requirement> getFailedRequirements(final Player player) {
		final List<Requirement> failedRequirements = new ArrayList<Requirement>();

		final List<Requirement> allRequirements = getAllRequirements(player);

		for (final Requirement req : allRequirements) {
			if (!req.meetsRequirement(player)) {
				failedRequirements.add(req);
			}
		}

		return failedRequirements;
	}

	/**
	 * Gets the global play time (playtime across all servers with the same
	 * MySQL database linked) of a player.
	 * <p>
	 * 
	 * @deprecated use getGlobalPlayTime(UUID uuid) instead.
	 * @param player Player to check for.
	 * @return play time of a player. -1 if no entry was found.
	 */
	public int getGlobalPlayTime(final Player player) {
		UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		
		return getGlobalPlayTime(uuid);
	}
	
	public int getGlobalPlayTime(final UUID uuid) {
		return plugin.getPlaytimes().getGlobalTime(uuid);
	}

	/**
	 * Gets the local play time of this player on this server according to
	 * Autorank. <br>
	 * This method will grab the time from the data.yml used by Autorank and <br>
	 * this is not dependend on other plugins.
	 * 
	 * @deprecated use getLocalPlayTime(UUID uuid) instead.
	 * @param player Player to get the time for.
	 * @return play time of this player or 0 if not found.
	 */
	public int getLocalTime(final Player player) {
		UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		
		return plugin.getPlaytimes().getLocalTime(uuid);
	}
	
	public int getLocalPlayTime(final UUID uuid) {
		return plugin.getPlaytimes().getLocalTime(uuid);
	}

	/**
	 * Gets the database name Autorank stores its global times in.
	 * 
	 * @return name of database
	 */
	public String getMySQLDatabase() {
		return plugin.getMySQLWrapper().getDatabaseName();
	}

	/**
	 * Gets the permission group that the player will be ranked up to after
	 * he completes all requirements.
	 * <p>
	 * <b>NOTE:</b> This does not mean the player will always be ranked up to
	 * this group. If a requirement has its own <i>'rank change'</i> result, the
	 * player will be ranked up to that group and not the 'global results'
	 * group.
	 * 
	 * @param player Player to get the next rank up for.
	 * @return The name of the group the player will be ranked to; null when no
	 *         rank up.
	 */
	public String getNextRankupGroup(final Player player) {
		return plugin.getPlayerChecker().getNextRankupGroup(player);
	}

	/**
	 * Gets the permission groups a player is part of.
	 * 
	 * @param player Player to get the groups of
	 * @return A list of permission groups
	 */
	public List<String> getPermissionGroups(final Player player) {
		final String[] groups = plugin.getPermPlugHandler()
				.getPermissionPlugin().getPlayerGroups(player);

		final List<String> permGroups = new ArrayList<String>();

		// Convert array into list
		for (final String group : groups) {
			permGroups.add(group);
		}

		return permGroups;
	}

	/**
	 * Gets the primary permissions group of a player.
	 * 
	 * @param player Player to get the primary group of
	 * @return Name of the group that appears first.
	 */
	public String getPrimaryGroup(final Player player) {
		final List<String> groups = getPermissionGroups(player);

		if (groups.size() < 1) {
			throw new IllegalArgumentException("Groups of player '"
					+ player.getName() + "' are empty.");
		}

		return groups.get(0);
	}

	/**
	 * Gets the local play time (playtime on this server) of a player. <br>
	 * The time given depends on what plugin is used for keeping track of time. <br>
	 * The time is always given in seconds.
	 * <p>
	 * 
	 * @param player Player to get the time for
	 * @return play time of a player. 0 when has never played before.
	 */
	public int getTimeOfPlayer(final Player player) {
		return plugin.getPlaytimes().getTimeOfPlayer(player.getName());
	}

	/**
	 * Register a requirement that can be used in the advanced config.
	 * The name should be unique as that is the way Autorank will identify the
	 * requirement.
	 * <p>
	 * The name will be the name that is used in the config.
	 * 
	 * @param uniqueName Unique name identifier for the requirement
	 * @param clazz Requirement class that does all the logic
	 */
	public void registerRequirement(final String uniqueName,
			final Class<? extends Requirement> clazz) {
		plugin.getLogger().info("Loaded custom requirement: " + uniqueName);

		plugin.registerRequirement(uniqueName, clazz);
	}

	/**
	 * Register a result that can be used in the advanced config.
	 * The name should be unique as that is the way Autorank will identify the
	 * result.
	 * <p>
	 * The name will be the name that is used in the config.
	 * 
	 * @param uniqueName Unique name identifier for the result
	 * @param clazz Result class that does all the logic
	 */
	public void registerResult(final String uniqueName,
			final Class<? extends Result> clazz) {
		plugin.getLogger().info("Loaded custom result: " + uniqueName);

		plugin.registerResult(uniqueName, clazz);
	}
}
