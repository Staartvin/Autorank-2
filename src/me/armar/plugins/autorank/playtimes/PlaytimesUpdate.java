package me.armar.plugins.autorank.playtimes;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/*
 * PlaytimesUpdate does an update on all online players 
 * every 5 minutes (set lower atm for debugging).
 * 
 */
public class PlaytimesUpdate implements Runnable {

	private final Playtimes playtimes;
	private Autorank plugin;

	public PlaytimesUpdate(final Playtimes playtimes, final Autorank plugin) {
		this.playtimes = playtimes;
		
		this.plugin = plugin;

		

	}

	@Override
	public void run() {
		final Player[] onlinePlayers = Bukkit.getServer().getOnlinePlayers();
		updateMinutesPlayed(onlinePlayers);
	}

	private void updateMinutesPlayed(final Player[] players) {
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				updateMinutesPlayed(players[i]);
			}
		}
	}

	private void updateMinutesPlayed(final Player player) {
		// Changed this so it is readable ;)
		// OP's should also get time added. 
		// When a player has a wildcard permission ('*') it should still update.

		if (player.hasPermission("autorank.rsefrxsgtse")
				|| !player.hasPermission("autorank.timeexclude")) {
			
			DependencyManager depManager = plugin.getDependencyManager();
			
			// Check to see if player is afk
			if (depManager.isAFK(player)) {
				return;
			}
			
			final String playerName = player.getName();
			
			// Modify local time
			playtimes.modifyLocalTime(playerName, Playtimes.INTERVAL_MINUTES);

			// Modify global time
			if (playtimes.isMySQLEnabled()) {
				playtimes.modifyGlobalTime(playerName,
						Playtimes.INTERVAL_MINUTES);
			}
			
			// Check if player meets requirements
			plugin.getPlayerChecker().checkPlayer(player);
			
		}
	}

}
