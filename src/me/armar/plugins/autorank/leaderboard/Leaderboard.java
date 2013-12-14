package me.armar.plugins.autorank.leaderboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

import org.bukkit.command.CommandSender;

/*
 * Leaderboard stores how when the last update was and if someone wants to 
 * display it and it it outdated (set to 10 minutes) 
 * it will generate a new leaderboard.
 * 
 */

public class Leaderboard {

	private String[] messages;
	private long lastUpdatedTime;
	private final Autorank plugin;
	private int leaderboardLength = 10;
	private String layout = "§6&r | §b&p - §7&d day(s), &h hour(s) and &m minute(s).";

	public Leaderboard(final Autorank plugin) {
		this.plugin = plugin;
		final SimpleYamlConfiguration advConfig = this.plugin
				.getAdvancedConfig();
		if (advConfig.getBoolean("use advanced config")) {
			leaderboardLength = advConfig.getInt("leaderboard length");
			layout = advConfig.getString("leaderboard layout");
		}

		updateLeaderboard();
	}

	public void sendLeaderboard(final CommandSender sender) {
		if (System.currentTimeMillis() - lastUpdatedTime > 600000) {
			// update leaderboard is last updated longer than 10 minutes ago
			updateLeaderboard();
		}
		for (final String msg : messages) {
			AutorankTools.sendColoredMessage(sender, msg);
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
			message = message.replaceAll("(&([a-f0-9]))", "\u00A7$2");

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

		for (final String playername : plugin.getPlaytimes().getKeys()) {
			map.put(playername, plugin.getPlaytimes().getLocalTime(playername));
		}

		sorted_map.putAll(map);
		return sorted_map;
	}

}
