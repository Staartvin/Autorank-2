package me.armar.plugins.autorank.statsapi;

import me.armar.plugins.autorank.Autorank;
import nl.lolmewn.stats.api.Stat;
import nl.lolmewn.stats.api.StatsAPI;
import nl.lolmewn.stats.player.StatData;
import nl.lolmewn.stats.player.StatsPlayer;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class StatsHandler {

	private Autorank plugin;
	private StatsAPI statsAPI;
	
	private String[] compatibleVersions = {"1.37"};

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
	
	/**
	 * Check whether the given version of Stats is compatible with Autorank
	 * @param version version of Stats that is running on this system.
	 * @return true if compatible; false otherwise
	 */
	public boolean compatibleStatsVersion(String version) {
		for (String v: compatibleVersions) {
			if (version.contains(v)) {
				return true;
			}
		}
		
		return false;
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

	public int getNormalStat(String playerName, String statName, World world) {
		StatsPlayer player = getStats(playerName);
		StatData stat;

		int value = 0;

		if (world != null) {
			stat = player.getStatData(statsAPI.getStat(statName),
					world.getName(), true);

			for (Object[] vars : stat.getAllVariables()) {
				value += stat.getValue(vars);
			}

		} else {
			// We want global (no specific world) so we loop over every world.

			for (World serverWorld : plugin.getServer().getWorlds()) {

				stat = player.getStatData(statsAPI.getStat(statName),
						serverWorld.getName(), true);

				for (Object[] vars : stat.getAllVariables()) {
					value += stat.getValue(vars);
				}
			}
		}

		return value;
	}

	/**
	 * Gets the total blocks of a certain id and damage value placed/broken
	 * 
	 * @param playerName Player to check for
	 * @param id Item ID to check for
	 * @param damageValue Damage value to check for. (negative number to not
	 *            skip check)
	 * @param world World to check in. Null for global.
	 * @param statType Either "Block break" or "Block place"
	 * @return amount player placed/broke of a block
	 */
	public int getBlocksStat(String playerName, int id, int damageValue,
			World world, String statType) {
		if (!isEnabled())
			return 0;

		StatsPlayer player = getStats(playerName);
		StatData blockStat;
		int value = 0;
		boolean checkDamageValue = false;

		if (damageValue > 0) {
			checkDamageValue = true;
		}

		// Implement world logic

		if (world != null) {
			blockStat = player
					.getStatData(statsAPI.getStatExact(statType),
							world.getName(), true);

			for (Object[] vars : blockStat.getAllVariables()) {

				if (checkDamageValue) {
					// VAR 0 = blockID, VAR 1 = damageValue, VAR 2 = (1 = break, 0 = place)
					if ((Integer) vars[0] == id
							&& (Byte) vars[1] == damageValue) {
						value += blockStat.getValue(vars);
					}
				} else {
					if ((Integer) vars[0] == id) {
						value += blockStat.getValue(vars);
					}
				}
			}
		} else {
			// We want global (no specific world) so we loop over every world.
			for (World serverWorld : plugin.getServer().getWorlds()) {
				blockStat = player.getStatData(
						statsAPI.getStatExact(statType),
						serverWorld.getName(), true);

				for (Object[] vars : blockStat.getAllVariables()) {

					if (checkDamageValue) {
						// VAR 0 = blockID, VAR 1 = damageValue, VAR 2 = (1 = break, 0 = place)
						if ((Integer) vars[0] == id
								&& (Byte) vars[1] == damageValue) {
							value += blockStat.getValue(vars);
						}
					} else {
						if ((Integer) vars[0] == id) {
							value += blockStat.getValue(vars);
						}
					}
				}
			}
		}

		return value;
	}

	public int getTotalMobsKilled(String playerName, String mobName, World world) {
		if (!isEnabled())
			return 0;
		
		StatsPlayer player = getStats(playerName);

		StatData blockStat;
		EntityType mob = getEntityType(mobName);
		boolean checkEntityType = false;
		int value = 0;

		if (mob != null) {
			checkEntityType = true;
		}

		// Implement world logic
		
		if (world != null) {
			blockStat = player.getStatData(statsAPI.getStatExact("Kill"),
					world.getName(), true);

			for (Object[] vars : blockStat.getAllVariables()) {

				// var 0 is mob type

				if (checkEntityType) {
					if (getEntityType(vars[0].toString()) != null
							&& getEntityType(vars[0].toString()).equals(mob)) {
						value += blockStat.getValue(vars);
					}
				} else {
					value += blockStat.getValue(vars);
				}
			}
		} else {
			// We want global (no specific world) so we loop over every world.
			for (World serverWorld : plugin.getServer().getWorlds()) {
				blockStat = player.getStatData(statsAPI.getStatExact("Kill"),
						serverWorld.getName(), true);

				for (Object[] vars : blockStat.getAllVariables()) {

					// var 0 is mob type

					if (checkEntityType) {
						if (getEntityType(vars[0].toString()) != null
								&& getEntityType(vars[0].toString())
										.equals(mob)) {
							value += blockStat.getValue(vars);
						}
					} else {
						value += blockStat.getValue(vars);
					}
				}
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
	
	public boolean areBetaFunctionsEnabled() {
		if (statsAPI != null) {
			return statsAPI.isUsingBetaFunctions();
		}
		return false;
	}
}
