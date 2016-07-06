package me.armar.plugins.autorank.playtimes;

/*
 * PlaytimesSave saves to disk in case the server crashes. It stores
 * all of that in a SimpleYamlConfiguration (like the regular bukkit
 * YAML but with things like automatic config generation added). And
 * then there is the SQLDataStorage class that connects to the database
 * but thats far from finished.
 * 
 */

public class PlaytimesSave implements Runnable {

	private final Playtimes playtimes;

	public PlaytimesSave(final Playtimes playtimes) {
		this.playtimes = playtimes;
	}

	@Override
	public void run() {

		playtimes.getAutorank().debugMessage("Saving all data files...");

		playtimes.save();
	}

}
