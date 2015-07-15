package me.armar.plugins.autorank.hooks.statsapi.customstats;

import java.util.HashMap;
import java.util.Map;

import nl.lolmewn.stats.api.stat.Stat;
import nl.lolmewn.stats.api.stat.StatEntry;
import nl.lolmewn.stats.api.storage.DataType;

/**
 * Stat that tracks special mobs killed, such as 'charged creeper' or 'elder
 * guardian'
 * <p>
 * Date created: 15:59:38 27 jun. 2015
 * 
 * @author Staartvin
 * 
 */
public class MobKilledStat implements Stat {

	private HashMap<String, DataType> data = new HashMap<String, DataType>();

	public static String statName = "AR special mobs killed";

	public MobKilledStat() {
		// init
		data.put("entityType", DataType.STRING);
		data.put("extraType", DataType.STRING);
		data.put("world", DataType.STRING);
	}

	/* (non-Javadoc)
	 * @see nl.lolmewn.stats.api.stat.Stat#format(nl.lolmewn.stats.api.stat.StatEntry)
	 */
	@Override
	public String format(StatEntry arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nl.lolmewn.stats.api.stat.Stat#getDataTypes()
	 */
	@Override
	public Map<String, DataType> getDataTypes() {
		// TODO Auto-generated method stub
		return data;
	}

	/* (non-Javadoc)
	 * @see nl.lolmewn.stats.api.stat.Stat#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nl.lolmewn.stats.api.stat.Stat#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return statName;
	}

	/* (non-Javadoc)
	 * @see nl.lolmewn.stats.api.stat.Stat#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see nl.lolmewn.stats.api.stat.Stat#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean arg0) {
		// TODO Auto-generated method stub

	}

}
