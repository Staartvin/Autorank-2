package me.armar.plugins.autorank.hooks.statsapi;

import me.armar.plugins.autorank.Autorank;
import nl.lolmewn.stats.api.Stat;
import nl.lolmewn.stats.api.StatsAPI;
import nl.lolmewn.stats.player.StatData;
import nl.lolmewn.stats.player.StatsPlayer;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class StatsAPIHandler {

	private final Autorank plugin;
	private StatsAPI statsAPI;

	private final String[] compatibleVersions = { "1.37" };

	public StatsAPIHandler(final Autorank instance) {
		plugin = instance;
	}

	public boolean setupStatsAPI() {
		final Plugin statsPlugin = plugin.getServer().getPluginManager()
				.getPlugin("Stats");

		// There are multiple stat plugins so we check for the correct author.
		if (statsPlugin == null
				|| !statsPlugin.getDescription().getAuthors()
						.contains("Lolmewn") || !statsPlugin.isEnabled()) {
			return false;
		}

		final RegisteredServiceProvider<StatsAPI> stats = plugin.getServer()
				.getServicesManager()
				.getRegistration(nl.lolmewn.stats.api.StatsAPI.class);
		if (stats != null) {
			statsAPI = stats.getProvider();
		}
		return (statsAPI != null);
	}

	/**
	 * Check whether the given version of Stats is compatible with Autorank
	 * 
	 * @param version version of Stats that is running on this system.
	 * @return true if compatible; false otherwise
	 */
	public boolean compatibleStatsVersion(final String version) {
		for (final String v : compatibleVersions) {
			if (version.contains(v)) {
				return true;
			}
		}

		return false;
	}

	public int getTotalBlocksBroken(final String playerName, final World world) {
		String statName = "Block break";

		final StatsPlayer player = getStats(playerName);
		StatData stat;

		int value = 0;

		if (world != null) {
			// Do check for one world
			stat = player.getStatData(statsAPI.getStat(statName), world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				value += stat.getValue(vars);
			}
		} else {
			// Do global check
			for (final World w : plugin.getServer().getWorlds()) {
				stat = player.getStatData(statsAPI.getStat(statName),
						w.getName(), true);

				for (final Object[] vars : stat.getAllVariables()) {
					value += stat.getValue(vars);
				}
			}
		}

		return value;
	}

	public int getTotalBlocksPlaced(final String playerName, final World world) {
		String statName = "Block place";

		final StatsPlayer player = getStats(playerName);
		StatData stat;

		int value = 0;

		if (world != null) {
			// Do check for one world
			stat = player.getStatData(statsAPI.getStat(statName), world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				value += stat.getValue(vars);
			}
		} else {
			// Do global check
			for (final World w : plugin.getServer().getWorlds()) {
				stat = player.getStatData(statsAPI.getStat(statName),
						w.getName(), true);

				for (final Object[] vars : stat.getAllVariables()) {
					value += stat.getValue(vars);
				}
			}
		}

		return value;
	}

	public int getTotalPlayTime(final String playerName, final World world) {
		String statName = "Playtime";
		
		final StatsPlayer player = getStats(playerName);
		StatData stat;

		int value = 0;

		if (world != null) {
			// Do check for one world
			stat = player.getStatData(statsAPI.getStat(statName), world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				value += stat.getValue(vars);
			}
		} else {
			// Do global check
			for (final World w : plugin.getServer().getWorlds()) {
				stat = player.getStatData(statsAPI.getStat(statName),
						w.getName(), true);

				for (final Object[] vars : stat.getAllVariables()) {
					value += stat.getValue(vars);
				}
			}
		}

		return value;
	}

	public StatsPlayer getStats(final String player) {
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
	public StatData getStatType(final Stat statType, final String player) {
		final StatsPlayer sPlayer = getStats(player);
		return sPlayer.getStatData(statType, false);
	}

	public StatsAPI getStatsAPI() {
		return statsAPI;
	}

	public boolean isEnabled() {
		return (statsAPI != null);
	}

	public int getNormalStat(final String playerName, final String statName,
			final World world) {
		final StatsPlayer player = getStats(playerName);
		StatData stat;

		int value = 0;

		if (world != null) {
			stat = player.getStatData(statsAPI.getStat(statName),
					world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				value += stat.getValue(vars);
			}

		} else {
			// We want global (no specific world) so we loop over every world.

			for (final World serverWorld : plugin.getServer().getWorlds()) {

				stat = player.getStatData(statsAPI.getStat(statName),
						serverWorld.getName(), true);

				for (final Object[] vars : stat.getAllVariables()) {
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
	public int getBlocksStat(final String playerName, final int id,
			final int damageValue, final World world, final String statType) {
		if (!isEnabled())
			return 0;

		final StatsPlayer player = getStats(playerName);
		StatData blockStat;
		int value = 0;
		boolean checkDamageValue = false;

		if (damageValue > 0) {
			checkDamageValue = true;
		}

		// Implement world logic

		if (world != null) {
			blockStat = player.getStatData(statsAPI.getStatExact(statType),
					world.getName(), true);

			for (final Object[] vars : blockStat.getAllVariables()) {

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
			for (final World serverWorld : plugin.getServer().getWorlds()) {
				blockStat = player.getStatData(statsAPI.getStatExact(statType),
						serverWorld.getName(), true);

				for (final Object[] vars : blockStat.getAllVariables()) {

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

	public int getTotalMobsKilled(final String playerName,
			final String mobName, final World world) {
		if (!isEnabled())
			return 0;

		final StatsPlayer player = getStats(playerName);

		StatData blockStat;
		final EntityType mob = getEntityType(mobName);
		boolean checkEntityType = false;
		int value = 0;

		if (mob != null) {
			checkEntityType = true;
		}

		// Implement world logic

		if (world != null) {
			blockStat = player.getStatData(statsAPI.getStatExact("Kill"),
					world.getName(), true);

			for (final Object[] vars : blockStat.getAllVariables()) {

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
			for (final World serverWorld : plugin.getServer().getWorlds()) {
				blockStat = player.getStatData(statsAPI.getStatExact("Kill"),
						serverWorld.getName(), true);

				for (final Object[] vars : blockStat.getAllVariables()) {

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
	
	public int getTotalBlocksMoved(String playerName, int type, World world) {
		final StatsPlayer player = getStats(playerName);
		String statName = "Move";
		StatData stat;

		int value = 0;

		if (world != null) {
			stat = player.getStatData(statsAPI.getStat(statName),
					world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				if ((Integer) vars[0] == type) {
					value += stat.getValue(vars);
				}
			}

		} else {
			// We want global (no specific world) so we loop over every world.

			for (final World serverWorld : plugin.getServer().getWorlds()) {

				stat = player.getStatData(statsAPI.getStat(statName),
						serverWorld.getName(), true);

				for (final Object[] vars : stat.getAllVariables()) {
					if ((Integer) vars[0] == type) {
						value += stat.getValue(vars);
					}
				}
			}
		}

		return value;
	}

	public EntityType getEntityType(final String entityName) {
		try {
			return EntityType.valueOf(entityName.toUpperCase());
		} catch (final Exception e) {
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
