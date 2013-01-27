package me.armar.plugins.autorank.playtimes;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;

/*
 * PlaytimesUpdate does an update on all online players 
 * every 5 minutes (set lower atm for debugging).
 * 
 */
public class PlaytimesUpdate implements Runnable {

	@SuppressWarnings("unused")
	private Essentials ess;
    private Playtimes playtimes;

    public PlaytimesUpdate(Playtimes playtimes) {
	this.playtimes = playtimes;

	Plugin x = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
	if (x != null & x instanceof Essentials) {
	    ess = (Essentials) x;
	    Autorank.logMessage("Essentials was found! AFK integration can be used.");
	} else {
	  Autorank.logMessage("Essentials was NOT found! Disabling AFK integration.");
	}

    }

    @Override
    public void run() {
	Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers();
	updateMinutesPlayed(onlinePlayers);
    }

    private void updateMinutesPlayed(Player[] players) {
	for (int i = 0; i < players.length; i++) {
	    if (players[i] != null) {
		updateMinutesPlayed(players[i]);
	    }
	}
	System.out.print("CHECK!!!");
    }

    private void updateMinutesPlayed(Player player) {
	if (!player.hasPermission("autorank.timeexclude")) {
	    String playerName = player.getName().toLowerCase();
	    if (!playtimes.getKeys().contains(playerName)) {
		playtimes.setTime(playerName, 0);
	    }
	    playtimes.modifyTime(playerName, Playtimes.INTERVAL_MINUTES);
	}
    }

}
