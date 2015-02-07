package me.armar.plugins.autorank.statsmanager;

import java.util.UUID;

public interface StatsPlugin {

	/**
	 * Get the correct name of this stat
	 * 
	 * @param statType stat name that can be incorrect
	 * @return official name of the stat or null if not valid
	 */
	public String getCorrectStatName(String statType);

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
	 *         support this stat; -2 if stat name is invalid
	 */
	public int getNormalStat(String statType, UUID uuid, Object... arguments);

	/**
	 * Check whether the current stats plugin is enabled or not.
	 * 
	 * @return true if enabled; false otherwise
	 */
	public boolean isEnabled();

}
