package me.armar.plugins.autorank.data.flatfile;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.dataType;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.playtimes.PlaytimeManager;

/*
 * UpdatePlaytime does an update on all online players
 * every 5 minutes (set lower atm for debugging).
 * 
 */
public class UpdatePlaytime implements Runnable {

	private final Autorank plugin;
	private FlatFileManager flatFileManager;

	public UpdatePlaytime(final FlatFileManager flatFileManager, final Autorank plugin) {
		this.plugin = plugin;
		this.flatFileManager = flatFileManager;
	}

	@Override
	public void run() {
		updateMinutesPlayed();
	}

	private void updateMinutesPlayed() {
		plugin.debugMessage("Checking players for automatic ranking");

		plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
			public void run() {
				// Check whether the files are still up to date - Do this synchronously
				flatFileManager.doCalendarCheck();
			}
		});

		for (final Player player : plugin.getServer().getOnlinePlayers()) {

			if (player.getPlayer() == null) {
				plugin.debugMessage("Could not update play time of " + player.getName() + " as (s)he is not online!");
				continue;
			}

			updateMinutesPlayed(player);
		}
	}

	private void updateMinutesPlayed(final Player player) {
		// Changed this so it is readable ;)
		// OP's should also get time added.
		// When a player has a wildcard permission ('*') it should still update.

		// Check for leaderboard exempt permission -> updates value of leaderboard exemption
		plugin.getPlayerChecker().doLeaderboardExemptCheck(player);

		if (player.hasPermission("autorank.rsefrxsgtse") || !player.hasPermission("autorank.timeexclude")) {

			final DependencyManager depManager = plugin.getDependencyManager();

			// Check to see if player is afk
			if (depManager.isAFK(player)) {
				return;
			}

			final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

			// Modify local time
			for (final dataType type : dataType.values()) {
				flatFileManager.addLocalTime(uuid, PlaytimeManager.INTERVAL_MINUTES, type);
			}

			// Modify global time
			if (plugin.getMySQLManager().isMySQLEnabled()) {
				flatFileManager.addGlobalTime(uuid, PlaytimeManager.INTERVAL_MINUTES);
			}

			// Only check a player if it is not disabled in the Settings.yml
			if (!plugin.getConfigHandler().isAutomaticPathDisabled()) {
				// Check if player meets requirements
				plugin.getPlayerChecker().checkPlayer(player);
			}
		}
	}

}
