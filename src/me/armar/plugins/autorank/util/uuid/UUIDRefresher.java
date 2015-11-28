package me.armar.plugins.autorank.util.uuid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

/**
 * This class is run on startup of the server to refresh all users (if they are
 * outdated). <br>
 * It will run a task that is async that will update all users.
 * <p>
 * Date created: 20:02:53 2 sep. 2015
 * 
 * @author Staartvin
 * 
 */
public class UUIDRefresher implements Runnable {

	// Whether this task is running
	public static boolean isRunning = false;

	private final Autorank plugin;

	public UUIDRefresher(final Autorank plugin) {
		this.plugin = plugin;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		isRunning = true;

		// Get a list of all uuid keys.
		final List<UUID> uuids = plugin.getPlaytimes().getUUIDKeys();

		final List<UUID> notOutdated = new ArrayList<UUID>();

		plugin.debugMessage("Starting up refreshing uuids...");
		plugin.debugMessage("Setup will take "
				+ AutorankTools.timeToString(uuids.size() / 110, Time.SECONDS));

		for (int i = 0; i < uuids.size(); i++) {

			//
			//System.out.println(i%100);

			// Show progress every 1000
			if (i % 1000 == 0) {
				plugin.debugMessage("Setup progress: " + i + "/" + uuids.size());
			}

			final UUID uuid = uuids.get(i);

			final String playerName = plugin.getUUIDStorage().getPlayerName(
					uuid);

			//System.out.println("Count: " + i + "/" + uuids.size());

			if (playerName == null) {
				continue;
			}

			if (!plugin.getUUIDStorage().isOutdated(playerName)) {
				// Not outdated, so we don't have to check it.
				notOutdated.add(uuid);
			}
		}

		// Remove all not outdated uuids from the list to check.
		for (final UUID uuid : notOutdated) {
			uuids.remove(uuid);
		}

		plugin.debugMessage("Setup finished!");

		final int size = uuids.size();

		// This will take longer than an hour, abort the refreshing and let it only update the value requested.
		if (size > 1500) {
			plugin.debugMessage("Tried refreshing uuids, but this will take over an hour. Aborting instead.");
			plugin.debugMessage("Autorank will manually update a UUID when a player logs in.");
			return;
		}

		// Time spent in seconds to refresh all.
		int timeSpent = 0;
		double count = 0;

		if (size < 600) {
			// Rougly one second per item
			timeSpent = size;
		}

		// Can only request 600 per 10 minutes.
		if (size > 600) {
			count = size / 500.0;

			final int countFloored = (int) Math.floor(count);

			// First 10 minutes + 20 minutes for each extra loop
			timeSpent = 600 + (countFloored - 1) * 1200;

			final double dif = count - countFloored;

			// Add remaining stuff. 
			timeSpent += 600 + dif * 500;
		}

		plugin.debugMessage("Refreshing "
				+ size
				+ " uuids, assuming all uuids are outdated, this will take at max. about");
		plugin.debugMessage(AutorankTools.timeToString(timeSpent, Time.SECONDS));

		final Map<UUID, String> names = UUIDManager.getPlayers(uuids);

		for (final Entry<UUID, String> entry : names.entrySet()) {
			final String name = entry.getValue();
			final UUID uuid = entry.getKey();

			plugin.getUUIDStorage().storeUUID(name, uuid);
		}

	}

}
