package me.armar.plugins.autorank.statsmanager;

public interface StatsPlugin {

	/**
	 * Check whether the current stats plugin is enabled or not.
	 * 
	 * @return true if enabled; false otherwise
	 */
	public boolean isEnabled();

	/**
	 * Get the value of a stat. You can only get stats that are of a certain
	 * type.
	 * <p>
	 * <b>NOTE:</b> returns -1 when the current stats plugin doesn't support
	 * this stat.
	 * 
	 * @param statType Stat you want to get
	 * @param arguments Provide arguments for the stat (playerName, worldName).
	 *            1st argument has to be the playername, 2nd argument must be
	 *            world (can be null)
	 * @return value of the stat; -1 when the current stats plugin doesn't
	 *         support this stat; -2 if stat name is invalid
	 */
	public int getNormalStat(String statType, Object... arguments);

	/**
	 * Get the correct name of this stat
	 * 
	 * @param statType stat name that can be incorrect
	 * @return official name of the stat or null if not valid
	 */
	public String getCorrectStatName(String statType);

}
