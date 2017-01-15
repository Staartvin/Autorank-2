package me.armar.plugins.autorank.api;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.requirement.Requirement;
import me.armar.plugins.autorank.pathbuilder.result.Result;

/**
 * <b>Autorank's API class:</b>
 * <p>
 * You, as a developer, can you use this class to get data from players or data
 * about paths. The API is never finished and if you want to see something
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
	 * Get the Addon manager of Autorank.
	 * <p>
	 * This class stores information about the loaded addons
	 * 
	 * @return {@linkplain AddOnManager} class
	 */
	public AddOnManager getAddOnManager() {
		return plugin.getAddonManager();
	}

	/**
	 * Get all {@linkplain RequirementsHolder}s for a player at the exact
	 * moment.
	 * This does not consider already finished requirement but just mirrors the
	 * Paths.yml file.
	 * 
	 * @param player Player to get the requirements from.
	 * @return a list of {@linkplain RequirementsHolder}s; An empty list when
	 *         none are found.
	 */
	public List<RequirementsHolder> getAllRequirements(final Player player) {
		return plugin.getPlayerChecker().getAllRequirementsHolders(player);
	}

	/**
	 * Get all {@linkplain RequirementsHolder}s that are not yet completed.
	 * 
	 * @param player Player to get the failed requirements for.
	 * @return list of {@linkplain RequirementsHolder}s that still have to be
	 *         completed.
	 */
	public List<RequirementsHolder> getFailedRequirements(final Player player) {
		return plugin.getPlayerChecker().getFailedRequirementsHolders(player);
	}

	/**
	 * Get the global play time (playtime across all servers with the same
	 * MySQL database linked) of a player.
	 * <p>
	 * 
	 * @param uuid UUID of the player
	 * @return play time of a player. 0 if no entry was found.
	 */
	public int getGlobalPlayTime(final UUID uuid) {
		return plugin.getMySQLManager().getGlobalTime(uuid);
	}

	/**
	 * Get the local play time of this player on this server according to
	 * Autorank (in minutes).<br>
	 * This method will grab the time from the internal storage used by Autorank and
	 * so this time does not depend on other plugins.
	 * 
	 * @param uuid UUID of the player
	 * @return play time of this player or 0 if not found.
	 */
	public int getLocalPlayTime(final UUID uuid) {
		return plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid);
	}

	/**
	 * Get the MySQL database name Autorank stores its global times in.
	 * 
	 * @return name of database
	 */
	public String getMySQLDatabase() {
		return plugin.getMySQLManager().getDatabaseName();
	}

	/**
	 * Get the local play time (play time on this server) of a player.
	 * The returned time depends on what plugin is used for keeping track of time.
	 * <br>
	 * The time is always given in seconds.
	 * <p>
	 * 
	 * @param player Player to get the time for
	 * @return play time of a player. 0 when has never played before.
	 */
	public int getTimeOfPlayer(final Player player) {
		return plugin.getPlaytimes().getTimeOfPlayer(player.getName(), true);
	}

	/**
	 * Register a requirement that can be used in the Paths.yml file.
	 * The name should be unique as that is the way Autorank will identify the
	 * requirement.
	 * <p>
	 * The name will be the name that is used in the config.
	 * 
	 * @param uniqueName Unique name identifier for the requirement
	 * @param clazz Requirement class that does all the logic
	 */
	public void registerRequirement(final String uniqueName, final Class<? extends Requirement> clazz) {
		plugin.getLogger().info("Loaded custom requirement: " + uniqueName);

		plugin.registerRequirement(uniqueName, clazz);
	}

	/**
	 * Register a result that can be used in the Paths.yml file.
	 * The name should be unique as that is the way Autorank will identify the
	 * result.
	 * <p>
	 * The name will be the name that is used in the config.
	 * 
	 * @param uniqueName Unique name identifier for the result
	 * @param clazz Result class that does all the logic
	 */
	public void registerResult(final String uniqueName, final Class<? extends Result> clazz) {
		plugin.getLogger().info("Loaded custom result: " + uniqueName);

		plugin.registerResult(uniqueName, clazz);
	}
}
