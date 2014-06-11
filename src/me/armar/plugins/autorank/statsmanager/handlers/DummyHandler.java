package me.armar.plugins.autorank.statsmanager.handlers;

import me.armar.plugins.autorank.statsmanager.StatsPlugin;

/**
 * This class is used when no stats plugin is found.
 * This does not do anything, but it allows the stats plugin to be gone so that
 * there are no errors.
 * 
 * @author Staartvin
 * 
 */
public class DummyHandler implements StatsPlugin {

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNormalStat(String statType, Object... arguments) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCorrectStatName(String statType) {
		// TODO Auto-generated method stub
		return null;
	}

}
