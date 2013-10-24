package me.armar.plugins.autorank.statsapi;

import me.armar.plugins.autorank.Autorank;
import nl.lolmewn.stats.api.Stat;
import nl.lolmewn.stats.api.StatsAPI;
import nl.lolmewn.stats.player.StatData;
import nl.lolmewn.stats.player.StatsPlayer;

import org.bukkit.entity.EntityType;
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
		return (int) statsAPI.getTotalBlocksBroken(player);
	}

	public int getTotalBlocksPlaced(String player) {
		return (int) statsAPI.getTotalBlocksPlaced(player);
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
	public StatData getStatType(Stat statType, String player) {
		StatsPlayer sPlayer = getStats(player);
		return sPlayer.getStatData(statType, false);
	}

	public StatsAPI getStatsAPI() {
		return statsAPI;
	}

	public boolean isEnabled() {
		return (statsAPI != null);
	}

	public int getTotalTimesVoted(String playerName) {
		StatsPlayer player = getStats(playerName);
		StatData stat = player.getStatData(statsAPI.getStat("Votes"), false);

		int value = 0;

		for (Object[] vars : stat.getAllVariables()) {
			value += stat.getValue(vars);
		}
		return value;
	}
	
	public int getDamageTaken(String playerName) {
		StatsPlayer player = getStats(playerName);
		StatData stat = player.getStatData(statsAPI.getStat("Damage taken"), false);

		int value = 0;

		for (Object[] vars : stat.getAllVariables()) {
			value += stat.getValue(vars);
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
	public int getBlocksPlaced(String playerName, int id, int damageValue) {
		if (!isEnabled()) return 0;
		
		StatsPlayer player = getStats(playerName);
		StatData blockStat = player.getStatData(statsAPI.getStat("Block place"), false);
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
	
	public int getTotalMobsKilled(String playerName, String mobName) {
		if (!isEnabled()) return 0;
		
		StatsPlayer player = getStats(playerName);
		StatData blockStat = player.getStatData(statsAPI.getStat("Kill"), false);
		
		EntityType mob = getEntityType(mobName);
		
		boolean checkEntityType = false;
		
		if (mob != null) {
			checkEntityType = true;
		}
		
		int value = 0;

		for (Object[] vars : blockStat.getAllVariables()) {
			
			// var 0 is mob type
			
				if (checkEntityType) {
					if (getEntityType(vars[0].toString()) != null && getEntityType(vars[0].toString()).equals(mob)) {
						value += blockStat.getValue(vars);
					}
				} else {
					value += blockStat.getValue(vars);
				}
		}
		return value;
	}
	
	public EntityType getEntityType(String entityName) {
		try {
			return EntityType.valueOf(entityName.toUpperCase());	
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets the total blocks of a certain id and damage value broken
	 * @param player Player to check for
	 * @param id Item ID to check for
	 * @param damageValue Damage value to check for. (negative number to not skip check)
	 * @return amount player broke of a block
	 */
	public int getBlocksBroken(String playerName, int id, int damageValue) {
		if (!isEnabled()) return 0;
		
		StatsPlayer player = getStats(playerName);
		StatData blockStat = player.getStatData(statsAPI.getStat("Block break"), false);
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
