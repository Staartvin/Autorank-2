package me.armar.plugins.autorank.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;

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
	 * Gets the local play time (playtime on this server) of a player.
	 * <p>
	 * 
	 * @param playerName player to check for.
	 * @return play time of a player. 0 when has never played before.
	 */
	public int getLocalPlayTime(final String playerName) {
		return plugin.getLocalTime(playerName);
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
	 * Gets the global play time (playtime across all servers with the same
	 * MySQL database linked) of a player.
	 * <p>
	 * 
	 * @param playerName player to check for.
	 * @return play time of a player. -1 if no entry was found.
	 */
	public int getGlobalPlayTime(final String playerName) {
		return plugin.getGlobalTime(playerName);
	}

	/**
	 * Gets all requirements for a player at the exact moment.
	 * This does not consider already finished requirement but just mirrors the
	 * config file.
	 * 
	 * @param player Player to get the requirements from.
	 * @return a list of requirement. An empty list when none are found.
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
}
