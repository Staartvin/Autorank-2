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

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Leaderboard stores how when the last update was and if someone wants to<br>
 * display it and it it outdated (set to 10 minutes)
 * it will generate a new leaderboard.<br>
 * <p>
 * Date created: 21:03:23 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class Leaderboard {

	private static Map<UUID, Integer> sortByComparator(
			final Map<UUID, Integer> unsortMap, final boolean order) {

		final List<Entry<UUID, Integer>> list = new LinkedList<Entry<UUID, Integer>>(
				unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<UUID, Integer>>() {
			@Override
			public int compare(final Entry<UUID, Integer> o1,
					final Entry<UUID, Integer> o2) {
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

	private long lastUpdatedTime;
	private String layout = "&6&r | &b&p - &7&d day(s), &h hour(s) and &m minute(s).";
	private int leaderboardLength = 10;
	private String[] messages;
	private final Autorank plugin;

	private final double validTime = 10D; // In minutes

	public Leaderboard(final Autorank plugin) {
		this.plugin = plugin;

		leaderboardLength = plugin.getConfigHandler().getLeaderboardLength();
		layout = plugin.getConfigHandler().getLeaderboardLayout();

		// Run async because it uses UUID lookup
		plugin.getServer().getScheduler()
				.runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						updateLeaderboard();
					}
				});
	}

	private Map<UUID, Integer> getSortedPlaytimes() {

		final List<UUID> uuids = plugin.getPlaytimes().getUUIDKeys();

		final HashMap<UUID, Integer> times = new HashMap<UUID, Integer>();

		//String firstWorld = plugin.getServer().getWorlds().get(0).getName();

		// Fill unsorted lists
		for (int i = 0; i < uuids.size(); i++) {

			// If player is exempted
			if (plugin.getRequirementHandler().hasLeaderboardExemption(
					uuids.get(i))) {
				continue;
			}
			
			String playerName = plugin.getUUIDStorage().getPlayerName(uuids.get(i));
			
			if (playerName == null) {
				plugin.getLogger().warning("Could not get player name of uuid '" + uuids.get(i) + "'!");
				continue;
			}

			// We should use getTimeOfPlayer(), but that requires a lot of rewrites, so I'll leave it at the moment.
			times.put(uuids.get(i),
					(plugin.getPlaytimes().getTimeOfPlayer(playerName)/60));
		}

		// Sort all values
		final Map<UUID, Integer> sortedMap = sortByComparator(times, false);

		return sortedMap;
	}

	public void sendLeaderboard(final CommandSender sender) {
		if (shouldUpdateLeaderboard()) {
			// Update leaderboard because it is not valid anymore.
			// Run async because it uses UUID lookup
			plugin.getServer().getScheduler()
					.runTaskAsynchronously(plugin, new Runnable() {
						@Override
						public void run() {
							updateLeaderboard();

							// Send them afterwards, not at the same time.
							for (final String msg : messages) {
								AutorankTools.sendColoredMessage(sender, msg);
							}
						}
					});
		} else {
			// send them instantly
			for (final String msg : messages) {
				AutorankTools.sendColoredMessage(sender, msg);
			}
		}
	}

	public void broadcastLeaderboard() {
		if (shouldUpdateLeaderboard()) {
			// Update leaderboard because it is not valid anymore.
			// Run async because it uses UUID lookup
			plugin.getServer().getScheduler()
					.runTaskAsynchronously(plugin, new Runnable() {
						@Override
						public void run() {
							updateLeaderboard();

							// Send them afterwards, not at the same time.
							for (final String msg : messages) {
								plugin.getServer().broadcastMessage(
										ChatColor.translateAlternateColorCodes(
												'&', msg));
							}
						}
					});
		} else {
			// send them instantly
			for (final String msg : messages) {
				plugin.getServer().broadcastMessage(
						ChatColor.translateAlternateColorCodes('&', msg));
			}
		}
	}

	private boolean shouldUpdateLeaderboard() {
		if (System.currentTimeMillis() - lastUpdatedTime > (60000 * validTime)
				|| messages == null)
			return true;
		else
			return false;
	}

	public void updateLeaderboard() {
		plugin.debugMessage("Updating leaderboard...");

		lastUpdatedTime = System.currentTimeMillis();

		final Map<UUID, Integer> sortedPlaytimes = getSortedPlaytimes();
		final Iterator<Entry<UUID, Integer>> itr = sortedPlaytimes.entrySet()
				.iterator();

		plugin.debugMessage("Size leaderboard: " + sortedPlaytimes.size());

		final List<String> stringList = new ArrayList<String>();
		stringList.add("&a-------- Autorank Leaderboard --------");

		for (int i = 0; i < leaderboardLength && itr.hasNext(); i++) {
			final Entry<UUID, Integer> entry = itr.next();

			final UUID uuid = entry.getKey();

			// Grab playername from here so it doesn't load all player names ever.
			final String name = UUIDManager.getPlayerFromUUID(uuid);

			if (name == null)
				continue;

			Integer time = entry.getValue().intValue();

			String message = layout.replaceAll("&p", name);

			message = message.replaceAll("&r", Integer.toString(i + 1));
			message = message.replaceAll("&tm", Integer.toString(time));
			message = message.replaceAll("&th", Integer.toString(time / 60));
			message = message.replaceAll("&d", Integer.toString(time / 1440));
			time = time - ((time / 1440) * 1440);
			message = message.replaceAll("&h", Integer.toString(time / 60));
			time = time - ((time / 60) * 60);
			message = message.replaceAll("&m", Integer.toString(time));
			message = ChatColor.translateAlternateColorCodes('&', message);

			stringList.add(message);

		}

		stringList.add("&a------------------------------------");

		messages = stringList.toArray(new String[stringList.size()]);

		//System.out.print("Took: " + (System.currentTimeMillis() - lastUpdatedTime)  + " ms."); 
	}

}
