package me.armar.plugins.autorank.playtimes;

import java.util.Calendar;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.playtimes.Playtimes.dataType;

/*
 * PlaytimesUpdate does an update on all online players 
 * every 5 minutes (set lower atm for debugging).
 * 
 */
public class PlaytimesUpdate implements Runnable {

	private final Playtimes playtimes;
	private final Autorank plugin;

	public PlaytimesUpdate(final Playtimes playtimes, final Autorank plugin) {
		this.playtimes = playtimes;

		this.plugin = plugin;

	}

	@Override
	public void run() {
		updateMinutesPlayed();
	}

	private void updateMinutesPlayed(final Player player) {
		// Changed this so it is readable ;)
		// OP's should also get time added. 
		// When a player has a wildcard permission ('*') it should still update.

		// Check for leaderboard exempt permission -> updates value of leaderboard exemption
		plugin.getPlayerChecker().doLeaderboardExemptCheck(player);

		if (player.hasPermission("autorank.rsefrxsgtse")
				|| !player.hasPermission("autorank.timeexclude")) {

			final DependencyManager depManager = plugin.getDependencyManager();

			// Check to see if player is afk
			if (depManager.isAFK(player)) {
				return;
			}

			final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

			// Modify local time
			for (dataType type: dataType.values()) {
				playtimes.modifyTime(uuid, Playtimes.INTERVAL_MINUTES, type);
			}
			

			// Modify global time
			if (playtimes.isMySQLEnabled()) {
				playtimes.modifyGlobalTime(uuid, Playtimes.INTERVAL_MINUTES);
			}

			// Check if player meets requirements
			plugin.getPlayerChecker().checkPlayer(player);

		}
	}

	private void updateMinutesPlayed() {
		plugin.debugMessage("Checking players for automatic ranking");
		
		// Check if daily, weekly or monthly files should be reset.
		
		Calendar cal = Calendar.getInstance();
		
		for (dataType type : Playtimes.dataType.values()) {
			if (plugin.getPlaytimes().shouldResetDatafile(type)) {
				// We should reset it now, it has expired.
				plugin.getPlaytimes().resetDatafile(type);
				
				int value = 0;
				if (type == dataType.DAILY_TIME) {
					value = cal.get(Calendar.DAY_OF_WEEK);
				} else if (type == dataType.WEEKLY_TIME) {
					value = cal.get(Calendar.WEEK_OF_YEAR);
				} else if (type == dataType.MONTHLY_TIME) {
					value = cal.get(Calendar.MONTH);
				}
 				
				// Update tracked data type
				plugin.getInternalProps().setTrackedDataType(type, value);
			}
		}

		for (final Player player : plugin.getServer().getOnlinePlayers()) {

			if (player.getPlayer() == null) {
				plugin.debugMessage("Could not update play time of "
						+ player.getName() + " as (s)he is not online!");
				continue;
			}

			updateMinutesPlayed(player);
		}
	}

}
