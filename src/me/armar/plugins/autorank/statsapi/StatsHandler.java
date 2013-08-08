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
	
	/**
	 * Gets the total blocks of a certain id and damage value placed
	 * @param player Player to check for
	 * @param id Item ID to check for
	 * @param damageValue Damage value to check for. (negative number to not skip check)
	 * @return amount player placed of a block
	 */
	public int getBlocksPlaced(String player, int id, int damageValue) {
		if (!isEnabled()) return 0;
		
		Stat blockStat = getStatType(StatType.BLOCK_PLACE, player);
		boolean checkDamageValue = false;
		
		
		if (damageValue > 0) {
			checkDamageValue = true;
		}
		
		int value = 0;

		for (Object[] vars : blockStat.getAllVariables()) {
			
			if (checkDamageValue) {
				// VAR 0 = blockID, VAR 1 = damageValue, VAR 2 = (1 = break, 0 = place)
				if ((Integer) vars[0] == id && (Byte) vars[1] == damageValue) {
					value += blockStat.getValue(vars);
				}
			} else {
				if ((Integer) vars[0] == id) {
					value += blockStat.getValue(vars);
				}
			}
		}
		return value;
	}
	
	/**
	 * Gets the total blocks of a certain id and damage value broken
	 * @param player Player to check for
	 * @param id Item ID to check for
	 * @param damageValue Damage value to check for. (negative number to not skip check)
	 * @return amount player broke of a block
	 */
	public int getBlocksBroken(String player, int id, int damageValue) {
		if (!isEnabled()) return 0;
		
		Stat blockStat = getStatType(StatType.BLOCK_BREAK, player);
		boolean checkDamageValue = false;
		
		
		if (damageValue > 0) {
			checkDamageValue = true;
		}
		
		int value = 0;

		for (Object[] vars : blockStat.getAllVariables()) {
			
			if (checkDamageValue) {
				// VAR 0 = blockID, VAR 1 = damageValue, VAR 2 = (1 = break, 0 = place)
				if ((Integer) vars[0] == id && (Byte) vars[1] == damageValue) {
					value += blockStat.getValue(vars);
				}
			} else {
				if ((Integer) vars[0] == id) {
					value += blockStat.getValue(vars);
				}
			}
		}
		return value;
	}
}
