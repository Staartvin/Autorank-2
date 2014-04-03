package me.armar.plugins.autorank.util.uuid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages everything related to UUIDs
 * <p>
 * Date created: 17:13:57 2 apr. 2014
 * 
 * @author Staartvin
 * 
 */
public class UUIDManager {

	private Map<String, UUID> foundUUIDs = new HashMap<String, UUID>();
	private Map<UUID, String> foundPlayers = new HashMap<UUID, String>();

	public Map<String, UUID> getUUIDs(final List<String> names) {

		long startTime = System.currentTimeMillis();
		
		// Clear maps first
		foundUUIDs.clear();

		Thread fetcherThread = new Thread(new Runnable() {

			public void run() {
				UUIDFetcher fetcher = new UUIDFetcher(names);

				Map<String, UUID> response = null;

				try {
					response = fetcher.call();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (response != null) {
					foundUUIDs = response;
				}
			}
		});

		fetcherThread.start();

		if (fetcherThread.isAlive()) {
			try {
				fetcherThread.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.print("Time for UUIDs: " + (System.currentTimeMillis() - startTime));

		// Thread stopped now, collect results
		return foundUUIDs;
	}

	public Map<UUID, String> getPlayers(final List<UUID> uuids) {
		
		long startTime = System.currentTimeMillis();
		// Clear names first
		foundPlayers.clear();

		Thread fetcherThread = new Thread(new Runnable() {

			public void run() {
				NameFetcher fetcher = new NameFetcher(uuids);

				Map<UUID, String> response = null;

				try {
					response = fetcher.call();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (response != null) {
					foundPlayers = response;
				}
			}
		});

		fetcherThread.start();

		if (fetcherThread.isAlive()) {
			try {
				fetcherThread.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.print("Time for Players: " + (System.currentTimeMillis() - startTime));

		// Thread stopped now, collect results
		return foundPlayers;
	}
	
	/**
	 * Get the Minecraft name of the player that is hooked to this Mojang account UUID.
	 * @param uuid the UUID of the Mojang account
	 * @return the name of player or null if not found.
	 */
	public String getPlayerFromUUID(UUID uuid) {
		Map<UUID, String> players = getPlayers(Arrays.asList(uuid));
		
		if (players == null) return null;
		
		if (players.isEmpty()) return null;
		
		return players.get(uuid);
	}
	
	/**
	 * Get the UUID of the Mojang account associated with this player name
	 * @param playerName Name of the player
	 * @return UUID of the associated Mojang account or null if not found.
	 */
	public UUID getUUIDFromPlayer(String playerName) {
		Map<String, UUID> uuids = getUUIDs(Arrays.asList(playerName));
		
		if (uuids == null) return null;
			
		if (uuids.isEmpty()) return null;
		
		return uuids.get(playerName);
	}
}
