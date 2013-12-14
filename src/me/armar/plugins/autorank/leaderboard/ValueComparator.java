package me.armar.plugins.autorank.leaderboard;

import java.util.Comparator;
import java.util.Map;

class ValueComparator implements Comparator<String> {

	Map<String, Integer> base;

	public ValueComparator(final Map<String, Integer> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.    
	@Override
	public int compare(final String a, final String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}