package me.armar.plugins.autorank.statsapi;

import me.armar.plugins.autorank.Autorank;
import nl.lolmewn.stats.StatType;
import nl.lolmewn.stats.api.StatsAPI;
import nl.lolmewn.stats.player.Stat;
import nl.lolmewn.stats.player.StatsPlayer;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class StatsHandler {

	private Autorank plugin;
	private StatsAPI statsAPI;

	public StatsHandler(Autorank instance) {
		plugin = instance;
	}

	public boolean setupStatsAPI() {
		Plugin statsPlugin = plugin.getServer().getPluginManager()
				.getPlugin("Stats");

		// There are multiple stat plugins so we check for the correct author.
		if (statsPlugin == null
				|| !statsPlugin.getDescription().getAuthors()
						.contains("Lolmewn") || !statsPlugin.isEnabled()) {
			return false;
		}

		RegisteredServiceProvider<StatsAPI> stats = plugin.getServer()
				.getServicesManager()
				.getRegistration(nl.lolmewn.stats.api.StatsAPI.class);
		if (stats != null) {
			statsAPI = stats.getProvider();
		}
		return (statsAPI != null);
	}

	public int getTotalBlocksBroken(String player) {
		return statsAPI.getTotalBlocksBroken(player);
	}

	public int getTotalBlocksPlaced(String player) {
		return statsAPI.getTotalBlocksPlaced(player);
	}

	public StatsPlayer getStats(String player) {
		return statsAPI.getStatsPlayer(player);
	}

	/**
	 * Get the stats of a player, a new stat will be created if it didn't exist
	 * yet.
	 * 
	 * @param statType StatType to get from the player
	 * @param player Player to get the stats of.
	 * @return Requested stat of the player
	 */
	public Stat getStatType(StatType statType, String player) {
		StatsPlayer sPlayer = getStats(player);
		return sPlayer.getStat(statType, true);
	}

	public StatsAPI getStatsAPI() {
		return statsAPI;
	}

	public boolean isEnabled() {
		return (statsAPI != null);
	}

	public int getTotalTimesVoted(String player) {
		Stat voteStat = getStatType(StatType.VOTES, player);

		int value = 0;

		for (Object[] vars : voteStat.getAllVariables()) {
			value += voteStat.getValue(vars);
		}
		return value;
	}
}
