package me.armar.plugins.autorank.statsmanager;

import java.util.HashMap;
import java.util.UUID;

public abstract class StatsPlugin {

	public static enum statTypes {
		BLOCKS_BROKEN, BLOCKS_MOVED, BLOCKS_PLACED, DAMAGE_TAKEN, FISH_CAUGHT, FOOD_EATEN, ITEMS_CRAFTED, MOBS_KILLED, PLAYERS_KILLED, TIME_PLAYED, TIMES_SHEARED, TOTAL_BLOCKS_BROKEN, TOTAL_BLOCKS_PLACED, VOTES
	}

	/**
	 * Get the value of a stat. You can only get stats that are of a certain
	 * type.
	 * <p>
	 * <b>NOTE:</b> returns -1 when the current stats plugin doesn't support
	 * this stat.
	 * 
	 * @param statType Stat you want to get
	 * @param uuid UUID of the player you want information of
	 * @param arguments Provide arguments for the stat (worldName).
	 *            1st argument has to be the world (can be null)
	 * @return value of the stat; -1 when the current stats plugin doesn't
	 *         support this stat
	 */
	public abstract int getNormalStat(statTypes statType, UUID uuid, HashMap<String, Object> arguments);

	/**
	 * Check whether the current stats plugin is enabled or not.
	 * 
	 * @return true if enabled; false otherwise
	 */
	public abstract boolean isEnabled();

}
