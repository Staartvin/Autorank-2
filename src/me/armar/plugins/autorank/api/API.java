package me.armar.plugins.autorank.api;

import me.armar.plugins.autorank.Autorank;

/**
 * <b>Autorank's API class:</b>
 * <p>
 * You, as a developer, can you use this class to get data from players or data about groups.
 * The API is never finished and if you want to see something added, tell us!
 * <p>
 * @author Staartvin
 *
 */
public class API {

	private Autorank plugin;
	
	public API(Autorank instance) {
		plugin = instance;
	}
	
	/**
	 * Gets the local play time (playtime on this server) of a player.
	 * <p>
	 * @param playerName player to check for.
	 * @return play time of a player. 0 when has never played before.
	 */
	public int getLocalPlayTime(String playerName) {
		return plugin.getLocalTime(playerName);
	}
	
	/**
	 * Gets the database name Autorank stores its global times in.
	 * @return name of database
	 */
	public String getMySQLDatabase() {
		return plugin.getMySQLWrapper().getDatabaseName();
	}
	
}
