package me.armar.plugins.autorank.hooks.statsapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import nl.lolmewn.stats.Main;
import nl.lolmewn.stats.api.Stat;
import nl.lolmewn.stats.api.StatsAPI;
import nl.lolmewn.stats.player.StatData;
import nl.lolmewn.stats.player.StatsPlayer;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

/**
 * Handles all connections with Stats
 * <p>
 * Date created:  21:02:34
 * 15 mrt. 2014
 * @author Staartvin
 *
 */
public class StatsAPIHandler implements DependencyHandler {

	private final Autorank plugin;
	private StatsAPI api;

	private final String[] compatibleVersions = { "1.37" };

	public StatsAPIHandler(final Autorank instance) {
		plugin = instance;
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
		if (!isAvailable())
			return 0;
		
		String statName = "Block break";

		final StatsPlayer player = getStats(playerName);
		StatData stat;

		int value = 0;

		if (world != null) {
			// Do check for one world
			stat = player.getStatData(api.getStat(statName), world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				value += stat.getValue(vars);
			}
		} else {
			// Do global check
			for (final World w : plugin.getServer().getWorlds()) {
				stat = player.getStatData(api.getStat(statName),
						w.getName(), true);

				for (final Object[] vars : stat.getAllVariables()) {
					value += stat.getValue(vars);
				}
			}
		}

		return value;
	}

	public int getTotalBlocksPlaced(final String playerName, final World world) {
		if (!isAvailable())
			return 0;
		
		String statName = "Block place";

		final StatsPlayer player = getStats(playerName);
		StatData stat;

		int value = 0;

		if (world != null) {
			// Do check for one world
			stat = player.getStatData(api.getStat(statName), world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				value += stat.getValue(vars);
			}
		} else {
			// Do global check
			for (final World w : plugin.getServer().getWorlds()) {
				stat = player.getStatData(api.getStat(statName),
						w.getName(), true);

				for (final Object[] vars : stat.getAllVariables()) {
					value += stat.getValue(vars);
				}
			}
		}

		return value;
	}

	public int getTotalPlayTime(final String playerName, final World world) {
		if (!isAvailable())
			return 0;
		
		String statName = "Playtime";
		
		final StatsPlayer player = getStats(playerName);
		StatData stat;

		int value = 0;

		if (world != null) {
			// Do check for one world
			stat = player.getStatData(api.getStat(statName), world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				value += stat.getValue(vars);
			}
		} else {
			// Do global check
			for (final World w : plugin.getServer().getWorlds()) {
				stat = player.getStatData(api.getStat(statName),
						w.getName(), true);

				for (final Object[] vars : stat.getAllVariables()) {
					value += stat.getValue(vars);
				}
			}
		}

		return value;
	}

	public StatsPlayer getStats(final String player) {
		return api.getStatsPlayer(player);
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

	public int getNormalStat(final String playerName, final String statName,
			final World world) {
		if (!isAvailable())
			return 0;
		
		final StatsPlayer player = getStats(playerName);
		StatData stat;

		int value = 0;

		if (world != null) {
			stat = player.getStatData(api.getStat(statName),
					world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				value += stat.getValue(vars);
			}

		} else {
			// We want global (no specific world) so we loop over every world.

			for (final World serverWorld : plugin.getServer().getWorlds()) {

				stat = player.getStatData(api.getStat(statName),
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
		if (!isAvailable())
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
			blockStat = player.getStatData(api.getStatExact(statType),
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
				blockStat = player.getStatData(api.getStatExact(statType),
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
		if (!isAvailable())
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
			blockStat = player.getStatData(api.getStatExact("Kill"),
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
				blockStat = player.getStatData(api.getStatExact("Kill"),
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
		if (!isAvailable())
			return 0;
		
		final StatsPlayer player = getStats(playerName);
		String statName = "Move";
		StatData stat;

		int value = 0;

		if (world != null) {
			stat = player.getStatData(api.getStat(statName),
					world.getName(), true);

			for (final Object[] vars : stat.getAllVariables()) {
				if ((Integer) vars[0] == type) {
					value += stat.getValue(vars);
				}
			}

		} else {
			// We want global (no specific world) so we loop over every world.

			for (final World serverWorld : plugin.getServer().getWorlds()) {

				stat = player.getStatData(api.getStat(statName),
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
		if (api != null) {
			return api.isUsingBetaFunctions();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
	 */
	@Override
	public Plugin get() {
		Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin("Stats");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof Main)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup() {
		if (!isInstalled()) {
			plugin.getLogger().info("Stats has not been found!");
			return false;
		} else {
			Main stats = (Main) get();
			
			api = stats.getAPI();

			if (api != null) {
				plugin.getLogger().info(
						"Stats has been found and can be used!");
				return true;
			} else {
				plugin.getLogger().info(
						"Stats has been found but cannot be used!");
				return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		Plugin plugin = get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return api != null;
	}
}
