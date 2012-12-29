package me.armar.plugins.autorank.leaderboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

import org.bukkit.command.CommandSender;

public class Leaderboard {

    private String[] messages;
    private long lastUpdatedTime;
    private Date date = new Date();
    private Autorank plugin;
    private int leaderboardLength = 10;
    private String layout = "&r | &p - &d day(s), &h hour(s) and &m minute(s).";

    public Leaderboard(Autorank plugin) {
	this.plugin = plugin;
	SimpleYamlConfiguration advConfig = this.plugin.getAdvancedConfig();
	if(advConfig.getBoolean("use advanced config")){
	    leaderboardLength = advConfig.getInt("leaderboard length");
	    layout = advConfig.getString("leaderboard layout");
	}
	
	updateLeaderboard();
    }

    public void sendLeaderboard(CommandSender sender) {
	// TODO if (date.getTime() - lastUpdatedTime > 600000) {
	if (date.getTime() - lastUpdatedTime > 30000) {
	    // update leaderboard is last updated longer than 10 minutes ago
	    updateLeaderboard();
	}

	for (String msg : messages) {
	    AutorankTools.sendColoredMessage(sender, msg);
	}
    }

    private void updateLeaderboard() {
	lastUpdatedTime = date.getTime();
		
	TreeMap<String, Integer> sortedPlaytimes = getSortedPlaytimes();
	Iterator<Entry<String, Integer>> itr = sortedPlaytimes.entrySet().iterator();

	List<String> stringList = new ArrayList<String>();
	stringList.add("-------- Autorank Leaderboard --------");

	for (int i = leaderboardLength; i > 0 && itr.hasNext(); i--) {
	    Entry<String, Integer> entry = (Entry<String, Integer>) itr.next();
	    String name = entry.getKey();
	    Integer time = entry.getValue().intValue();
	    
		String message = layout.replaceAll("&p", name);

		message = message.replaceAll("&r", Integer.toString(11-i));
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
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	ValueComparator bvc = new ValueComparator(map);
	TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

	for (String playername : plugin.getPlaytimes().getKeys()) {
	    map.put(playername, plugin.getPlaytimes().getTime(playername));
	}

	sorted_map.putAll(map);
	return sorted_map;
    }

}
