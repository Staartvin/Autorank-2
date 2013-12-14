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

	private Essentials ess;
	private final Playtimes playtimes;

	public PlaytimesUpdate(final Playtimes playtimes, final Autorank plugin) {
		this.playtimes = playtimes;

		if (plugin.getAdvancedConfig().getBoolean("use advanced config")
				&& plugin.getAdvancedConfig().getBoolean("afk integration")) {
			setupEssentials();
		}

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
			if (ess != null) {
				if (ess.getUser(player).isAfk()
						|| ess.getUser(player).isJailed())
					return;
			}
			final String playerName = player.getName().toLowerCase();
			if (!playtimes.getKeys().contains(playerName)) {
				playtimes.setLocalTime(playerName, 0);
			}
			// Modify local time
			playtimes.modifyLocalTime(playerName, Playtimes.INTERVAL_MINUTES);

			// Modify global time
			if (playtimes.isMySQLEnabled()) {
				playtimes.modifyGlobalTime(playerName,
						Playtimes.INTERVAL_MINUTES);
			}
		}
	}

	private void setupEssentials() {
		final Plugin x = Bukkit.getServer().getPluginManager()
				.getPlugin("Essentials");
		if (x != null & x instanceof Essentials) {
			ess = (Essentials) x;
			Autorank.logMessage("Essentials was found! AFK integration can be used.");
		} else {
			Autorank.logMessage("Essentials was NOT found! Disabling AFK integration.");
		}
	}

}
