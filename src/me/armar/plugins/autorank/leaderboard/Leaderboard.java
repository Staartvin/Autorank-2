package me.armar.plugins.autorank.leaderboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playtimes.Playtimes.dataType;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * This class is used to handle all leaderboard things.
 * <br>
 * When a player calls /ar leaderboard, it will show the currently cached
 * leaderboard.
 * <br>
 * <i>/ar leaderboard force</i> can be used to forcefully update the current
 * leaderboard.
 * <br>
 * <i>/ar leaderboard broadcast</i> can be used to broadcast the leaderboard
 * over the entire server.
 * <p>
 * Date created: 21:03:23 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class Leaderboard {

	private static Map<UUID, Integer> sortByComparator(final Map<UUID, Integer> unsortMap, final boolean order) {

		final List<Entry<UUID, Integer>> list = new LinkedList<Entry<UUID, Integer>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<UUID, Integer>>() {
			@Override
			public int compare(final Entry<UUID, Integer> o1, final Entry<UUID, Integer> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		final Map<UUID, Integer> sortedMap = new LinkedHashMap<UUID, Integer>();
		for (final Entry<UUID, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	private String layout = "&6&r | &b&p - &7&d %day%, &h %hour% and &m %minute%.";
	private int leaderboardLength = 10;
	// private String[] messages;
	private final Autorank plugin;

	private final double validTime = 30; // Leaderboard is valid for 30 minutes.

	public Leaderboard(final Autorank plugin) {
		this.plugin = plugin;

		leaderboardLength = plugin.getConfigHandler().getLeaderboardLength();
		layout = plugin.getConfigHandler().getLeaderboardLayout();
	}

	/**
	 * Gets a map, the key is the UUID of a player and the value is time that
	 * player has played.
	 * <br>
	 * This map is sorted on player time.
	 * 
	 * @param type dataType to get the sort for.
	 * @return a sorted map.
	 */
	private Map<UUID, Integer> getSortedPlaytimes(final dataType type) {

		final List<UUID> uuids = plugin.getPlaytimes().getUUIDKeys(type);

		final HashMap<UUID, Integer> times = new HashMap<UUID, Integer>();

		// String firstWorld = plugin.getServer().getWorlds().get(0).getName();

		// Fill unsorted lists
		for (int i = 0; i < uuids.size(); i++) {

			// If player is exempted
			if (plugin.getPlayerDataHandler().hasLeaderboardExemption(uuids.get(i))) {
				continue;
			}

			// Get the cached value of this uuid
			final String playerName = plugin.getUUIDStorage().getCachedPlayerName(uuids.get(i));

			if (playerName == null) {
				plugin.debugMessage("Could not get cached player name of uuid '" + uuids.get(i) + "'!");
				continue;
			}

			// Use cache on .getTimeOfPlayer() so that we don't refresh all
			// uuids in existence.
			if (type == dataType.TOTAL_TIME) {

				times.put(uuids.get(i), (plugin.getPlaytimes().getTimeOfPlayer(playerName, true) / 60));
			} else {
				times.put(uuids.get(i), plugin.getPlaytimes().getTime(type, uuids.get(i)));
			}

		}

		// Sort all values
		final Map<UUID, Integer> sortedMap = sortByComparator(times, false);

		return sortedMap;
	}

	/**
	 * Sends the leaderboard to a {@linkplain CommandSender}.
	 * 
	 * @param sender Sender to send it to.
	 * @param type Type of leaderboard to send.
	 */
	public void sendLeaderboard(final CommandSender sender, final dataType type) {
		if (shouldUpdateLeaderboard()) {
			// Update leaderboard because it is not valid anymore.
			// Run async because it uses UUID lookup
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					updateLeaderboard(type);

					// Send them afterwards, not at the same time.
					sendMessages(sender, type);
				}
			});
		} else {
			// send them instantly
			sendMessages(sender, type);
		}
	}

	public void broadcastLeaderboard(final dataType type) {
		if (shouldUpdateLeaderboard()) {
			// Update leaderboard because it is not valid anymore.
			// Run async because it uses UUID lookup
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					updateLeaderboard(type);

					// Send them afterwards, not at the same time.
					for (final String msg : plugin.getInternalProps().getCachedLeaderboard(type)) {
						plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
					}
				}
			});
		} else {
			// send them instantly
			for (final String msg : plugin.getInternalProps().getCachedLeaderboard(type)) {
				plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
			}
		}
	}

	private boolean shouldUpdateLeaderboard() {
		if (System.currentTimeMillis() - plugin.getInternalProps().getLeaderboardLastUpdateTime() > (60000 * validTime))
			return true;
		else
			return false;
	}

	public void sendMessages(final CommandSender sender, final dataType type) {
		for (final String msg : plugin.getInternalProps().getCachedLeaderboard(type)) {
			AutorankTools.sendColoredMessage(sender, msg);
		}
	}

	/**
	 * Forcefully update a leaderboard.
	 * 
	 * @param type Type of leaderboard to update.
	 */
	public void updateLeaderboard(final dataType type) {
		plugin.debugMessage("Updating leaderboard '" + type.toString() + "'!");

		final Map<UUID, Integer> sortedPlaytimes = getSortedPlaytimes(type);
		final Iterator<Entry<UUID, Integer>> itr = sortedPlaytimes.entrySet().iterator();

		plugin.debugMessage("Size leaderboard: " + sortedPlaytimes.size());

		final List<String> stringList = new ArrayList<String>();

		if (type == dataType.TOTAL_TIME) {
			stringList.add(Lang.LEADERBOARD_HEADER_ALL_TIME.getConfigValue());
		} else if (type == dataType.DAILY_TIME) {
			stringList.add(Lang.LEADERBOARD_HEADER_DAILY.getConfigValue());
		} else if (type == dataType.WEEKLY_TIME) {
			stringList.add(Lang.LEADERBOARD_HEADER_WEEKLY.getConfigValue());
		} else if (type == dataType.MONTHLY_TIME) {
			stringList.add(Lang.LEADERBOARD_HEADER_MONTHLY.getConfigValue());
		}

		for (int i = 0; i < leaderboardLength && itr.hasNext(); i++) {
			final Entry<UUID, Integer> entry = itr.next();

			final UUID uuid = entry.getKey();

			// Grab playername from here so it doesn't load all player names
			// ever.
			// Get the cached value of this uuid to improve performance
			String name = plugin.getUUIDStorage().getRealName(uuid);

			// UUIDManager.getPlayerFromUUID(uuid);

			// There was no real name found, use cached player name
			if (name == null) {
				name = plugin.getUUIDStorage().getCachedPlayerName(uuid);
			}

			// No cached name found, don't use this name.
			if (name == null)
				continue;

			Integer time = entry.getValue().intValue();

			String message = layout.replaceAll("&p", name);

			// divided by 1440
			final int days = (time / 1440);

			// (time - days) / 60
			final int hours = (time - (days * 1440)) / 60;

			// (time - days - hours)
			final int minutes = time - (days * 1440) - (hours * 60);

			message = message.replaceAll("&r", Integer.toString(i + 1));
			message = message.replaceAll("&tm", Integer.toString(time));
			message = message.replaceAll("&th", Integer.toString(time / 60));
			message = message.replaceAll("&d", Integer.toString(days));
			time = time - ((time / 1440) * 1440);
			message = message.replaceAll("&h", Integer.toString(hours));
			time = time - ((time / 60) * 60);
			message = message.replaceAll("&m", Integer.toString(minutes));
			message = ChatColor.translateAlternateColorCodes('&', message);

			// Correctly show plural or singular format.
			if (days > 1 || days == 0) {
				message = message.replace("%day%", Lang.DAY_PLURAL.getConfigValue());
			} else {
				message = message.replace("%day%", Lang.DAY_SINGULAR.getConfigValue());
			}

			if (hours > 1 || hours == 0) {
				message = message.replace("%hour%", Lang.HOUR_PLURAL.getConfigValue());
			} else {
				message = message.replace("%hour%", Lang.HOUR_SINGULAR.getConfigValue());
			}

			if (minutes > 1 || minutes == 0) {
				message = message.replace("%minute%", Lang.MINUTE_PLURAL.getConfigValue());
			} else {
				message = message.replace("%minute%", Lang.MINUTE_SINGULAR.getConfigValue());
			}

			stringList.add(message);

		}

		stringList.add(Lang.LEADERBOARD_FOOTER.getConfigValue());

		// Cache this leaderboard
		plugin.getInternalProps().setCachedLeaderboard(type, stringList);

		// Update latest update-time
		plugin.getInternalProps().setLeaderboardLastUpdateTime(System.currentTimeMillis());

		// messages = stringList.toArray(new String[stringList.size()]);
	}

	public void updateAllLeaderboards() {
		if (!shouldUpdateLeaderboard())
			return;

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				for (final dataType type : dataType.values()) {
					updateLeaderboard(type);
				}
			}
		});
	}

}
