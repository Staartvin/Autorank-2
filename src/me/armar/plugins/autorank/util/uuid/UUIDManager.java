package me.armar.plugins.autorank.util.uuid;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.Bukkit;

/**
 * This class allows developers to easily get UUIDs from names <br>
 * or to do the reverse. <br>
 * It has an implemented cache system, which makes sure it doesn't do a lookup <br>
 * when not needed.
 * <p>
 * Date created: 17:13:57 2 apr. 2014
 * 
 * @author Staartvin
 * 
 */
public class UUIDManager {

	private static Autorank plugin;

	static {
		plugin = (Autorank) Bukkit.getPluginManager().getPlugin("Autorank");
	}

	private static Map<UUID, String> foundPlayers = new HashMap<UUID, String>();

	private static Map<String, UUID> foundUUIDs = new HashMap<String, UUID>();

	// Whether to use cache or not
	private static final boolean useCache = true;

	public static void addCachedPlayer(final String playerName, final UUID uuid) {
		if (!useCache)
			return;

		plugin.getUUIDStorage().storeUUID(playerName, uuid);

		//System.out.print("Cached " + uuid + " of " + playerName);
		/*
		cachedUUIDs.put(playerName, uuid);
		lastCached.put(playerName, System.currentTimeMillis());*/
	}

	private static UUID getCachedUUID(final String playerName) {

		return plugin.getUUIDStorage().getStoredUUID(playerName);

		/*
		
		// Already found
		if (cachedUUIDs.containsKey(playerName))
			return cachedUUIDs.get(playerName);

		// Search for lowercase matches
		for (final String loggedName : cachedUUIDs.keySet()) {
			if (loggedName.equalsIgnoreCase(playerName)) {
				playerName = loggedName;
				break;
			}
		}

		if (!cachedUUIDs.containsKey(playerName))
			return null;

		// Grab UUID
		return cachedUUIDs.get(playerName);(*/
	}

	/**
	 * Get the Minecraft name of the player that is hooked to this Mojang
	 * account UUID. <br>
	 * It uses {@link #getPlayers(List)} to get the player's name.
	 * 
	 * @param uuid the UUID of the Mojang account
	 * @return the name of player or null if not found.
	 */
	public static String getPlayerFromUUID(final UUID uuid) {
		if (uuid == null)
			return null;

		final Map<UUID, String> players = getPlayers(Arrays.asList(uuid));

		if (players == null)
			return null;

		if (players.isEmpty())
			return null;

		if (players.get(uuid) == null) {
			throw new NullPointerException("Could not get player from UUID "
					+ uuid + "!");
		}

		return players.get(uuid);
	}

	/**
	 * Get the player names associated with this UUID. <br>
	 * This method has to run async, because it will use the lookup from the
	 * Mojang API. <br>
	 * It also takes care of already cached values. It doesn't lookup new
	 * players when it still has old, valid ones stored.
	 * 
	 * @param uuids A list of uuids to get the player names of.
	 * @return A map containing every player name per UUID.
	 */
	public static Map<UUID, String> getPlayers(final List<UUID> uuids) {
		// Clear names first
		foundPlayers.clear();

		// A new map to store cached values
		final HashMap<UUID, String> players = new HashMap<UUID, String>();

		// This is used to check if we need to use the lookup from the mojang website.
		boolean useInternetLookup = true;

		if (useCache) {
			// Check if we have cached values
			for (final UUID uuid : uuids) {

				final String playerName = plugin.getUUIDStorage()
						.getPlayerName(uuid);

				if (playerName != null) {
					// If cached value is still valid, use it.
					if (!plugin.getUUIDStorage().isOutdated(playerName)) {
						players.put(uuid, playerName);
					}
				}
			}

			// All names were retrieved from cached values
			// So we don't need to do a lookup to the Mojang website.
			if (players.entrySet().size() == uuids.size()) {
				useInternetLookup = false;
			}

			// No internet lookup needed.
			if (!useInternetLookup) {
				// Return all cached values.
				return players;
			}

			// From here on we know that didn't have all uuids as cached values.
			// So we need to do a lookup.
			// We have to make sure we only lookup the players that we haven't got cached values of yet.

			// Remove uuids that don't need to be looked up anymore. 
			// Just for performance sake.
			for (final UUID entry : players.keySet()) {
				uuids.remove(entry);
			}

		}

		// Now we need to lookup the other players

		final Thread fetcherThread = new Thread(new Runnable() {

			@Override
			public void run() {
				final NameFetcher fetcher = new NameFetcher(uuids);

				Map<UUID, String> response = null;

				try {
					response = fetcher.call();
				} catch (final Exception e) {
					if (e instanceof IOException) {
						Bukkit.getLogger()
								.warning(
										"Tried to contact Mojang page for UUID lookup but failed.");
						return;
					}
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

		// Update cached entries
		for (final Entry<UUID, String> entry : foundPlayers.entrySet()) {
			final String playerName = entry.getValue();
			final UUID uuid = entry.getKey();

			// Add found players to the list of players to return
			players.put(uuid, playerName);

			if (plugin.getUUIDStorage().isOutdated(playerName)) {
				// Update cached values
				addCachedPlayer(playerName, uuid);
			} else {
				// Do not update if it is not needed.
				continue;
			}
		}

		// Thread stopped now, collect results
		return players;
	}

	/**
	 * Get the UUID of the Mojang account associated with this player name <br>
	 * It uses {@link #getUUIDs(List)} to get the UUID.
	 * 
	 * @param playerName Name of the player
	 * @return UUID of the associated Mojang account or null if not found.
	 */
	public static UUID getUUIDFromPlayer(final String playerName) {
		if (playerName == null) {
			return null;
		}

		final Map<String, UUID> uuids = getUUIDs(Arrays.asList(playerName));

		if (uuids == null) {
			return null;
		}

		if (uuids.isEmpty()) {
			return null;
		}

		// Search case insensitive
		for (final Entry<String, UUID> entry : uuids.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(playerName)) {
				return entry.getValue();
			}
		}

		throw new NullPointerException("Could not UUID from player "
				+ playerName + "!");
	}

	/**
	 * Get the UUIDs of a list of players. <br>
	 * This method has to run async, because it will use the lookup from the
	 * Mojang API. <br>
	 * It also takes care of already cached values. It doesn't lookup new
	 * players when it still has old, valid ones stored.
	 * 
	 * @param names A list of playernames that you want the UUIDs of.
	 * @return A map containing every UUID per player name.
	 */
	public static Map<String, UUID> getUUIDs(final List<String> names) {

		// Clear maps first
		foundUUIDs.clear();

		// A new map to store cached values
		final HashMap<String, UUID> uuids = new HashMap<String, UUID>();

		// This is used to check if we need to use the lookup from the mojang website.
		boolean useInternetLookup = true;

		if (useCache) {
			// Check if we have cached values
			for (final String playerName : names) {

				// If cached value is still valid, use it.
				if (!plugin.getUUIDStorage().isOutdated(playerName)) {
					uuids.put(playerName, getCachedUUID(playerName));
				}
			}

			// All names were retrieved from cached values
			// So we don't need to do a lookup to the Mojang website.
			if (uuids.entrySet().size() == names.size()) {
				useInternetLookup = false;
			}

			// No internet lookup needed.
			if (!useInternetLookup) {
				// Return all cached values.
				return uuids;
			}

			// From here on we know that didn't have all uuids as cached values.
			// So we need to do a lookup.
			// We have to make sure we only lookup the players that we haven't got cached values of yet.

			// Remove players that don't need to be looked up anymore. 
			// Just for performance sake.
			for (final Entry<String, UUID> entry : uuids.entrySet()) {
				names.remove(entry.getKey());
			}

		}

		// Now we need to lookup the other players

		final Thread fetcherThread = new Thread(new Runnable() {

			@Override
			public void run() {
				final UUIDFetcher fetcher = new UUIDFetcher(names);

				Map<String, UUID> response = null;

				try {
					response = fetcher.call();
				} catch (final Exception e) {
					if (e instanceof IOException) {
						Bukkit.getLogger()
								.warning(
										"Tried to contact Mojang page for UUID lookup but failed.");
						return;
					}
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

		// Update cached entries
		for (final Entry<String, UUID> entry : foundUUIDs.entrySet()) {
			final String playerName = entry.getKey();
			final UUID uuid = entry.getValue();

			// Add found uuids to the list of uuids to return
			uuids.put(playerName, uuid);

			if (plugin.getUUIDStorage().isOutdated(playerName)) {
				// Update cached values
				addCachedPlayer(playerName, uuid);
			} else {
				// Do not update if it is not needed.
				continue;
			}
		}

		// Thread stopped now, collect results
		return uuids;
	}
}
