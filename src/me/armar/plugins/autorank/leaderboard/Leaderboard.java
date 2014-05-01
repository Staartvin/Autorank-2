package me.armar.plugins.autorank.leaderboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.util.AutorankTools;

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

	private String[] messages;
	private long lastUpdatedTime;
	private final Autorank plugin;
	private int leaderboardLength = 10;
	private String layout = "&6&r | &b&p - &7&d day(s), &h hour(s) and &m minute(s).";

	public Leaderboard(final Autorank plugin) {
		this.plugin = plugin;
		final SimpleYamlConfiguration advConfig = this.plugin
				.getAdvancedConfig();
		if (advConfig.getBoolean("use advanced config")) {
			leaderboardLength = advConfig.getInt("leaderboard length");
			layout = advConfig.getString("leaderboard layout");
		}
		
		// Run async because it uses UUID lookup
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				
				
				// Convert first
				plugin.getPlaytimes().convertToUUIDStorage();
				
				updateLeaderboard();
			}
		});
	}

	public void sendLeaderboard(final CommandSender sender) {
		if (System.currentTimeMillis() - lastUpdatedTime > 600000) {
			// Update leaderboard because it is not valid anymore.
			// Run async because it uses UUID lookup
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				public void run() {
					updateLeaderboard();
					
					// Send them afterwards, not at the same time.
					for (final String msg : messages) {
						AutorankTools.sendColoredMessage(sender, msg);
					}
				}
			});
		} else {
			for (final String msg : messages) {
				AutorankTools.sendColoredMessage(sender, msg);
			}
		}
		
	}

	private void updateLeaderboard() {
		lastUpdatedTime = System.currentTimeMillis();

		final TreeMap<String, Integer> sortedPlaytimes = getSortedPlaytimes();
		final Iterator<Entry<String, Integer>> itr = sortedPlaytimes.entrySet()
				.iterator();

		final List<String> stringList = new ArrayList<String>();
		stringList.add("-------- Autorank Leaderboard --------");

		for (int i = 0; i < leaderboardLength && itr.hasNext(); i++) {
			final Entry<String, Integer> entry = itr.next();
			final String name = entry.getKey();
			
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

		stringList.add("------------------------------------");

		messages = stringList.toArray(new String[stringList.size()]);
	}

	private TreeMap<String, Integer> getSortedPlaytimes() {
		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		final ValueComparator bvc = new ValueComparator(map);
		final TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(
				bvc);

		List<UUID> uuids = plugin.getPlaytimes().getUUIDKeys();
		List<String> playerNames = plugin.getPlaytimes().getPlayerKeys();

		for (int i = 0; i < playerNames.size(); i++) {
			String playerName = playerNames.get(i);
			
			map.put(playerName, plugin.getPlaytimes()
					.getLocalTime(uuids.get(i)));
		}

		sorted_map.putAll(map);
		return sorted_map;
	}

}
