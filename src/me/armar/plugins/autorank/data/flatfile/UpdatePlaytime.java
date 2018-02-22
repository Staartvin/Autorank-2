package me.armar.plugins.autorank.data.flatfile;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.playtimes.PlaytimeManager;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
 * UpdatePlaytime does an update on all online players
 * every 5 minutes
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

    /**
     * Called every x minutes
     */
    private void updateMinutesPlayed() {
        plugin.debugMessage("Checking players for automatic ranking");

        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            public void run() {
                // Check whether the files are still up to date - Do this
                // synchronously
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

    /**
     * Check whether a user is now allowed to complete the path
     *
     * @param player Player to check
     */
    private void updateMinutesPlayed(final Player player) {
        // Changed this so it is readable ;)
        // OP's should also get time added.
        // When a player has a wildcard permission ('*') it should still update.

        // Check for leaderboard exempt permission -> updates value of
        // leaderboard exemption
        plugin.getPlayerChecker().doLeaderboardExemptCheck(player);

        // Don't add extra time to the player when he is excluded from time updates.
        if (player.hasPermission(AutorankPermission.EXCLUDE_FROM_TIME_UPDATES)) {
            return;
        }

        final DependencyManager depManager = plugin.getDependencyManager();

        // Check to see if player is afk
        if (depManager.isAFK(player)) {
            return;
        }

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

        // Modify local time
        for (final TimeType type : TimeType.values()) {
            flatFileManager.addLocalTime(uuid, PlaytimeManager.INTERVAL_MINUTES, type);
        }

        // Modify global time
        if (plugin.getMySQLManager().isMySQLEnabled()) {
            plugin.getMySQLManager().addGlobalTime(uuid, PlaytimeManager.INTERVAL_MINUTES);
        }

        // Auto assign path (if possible)
        plugin.getPathManager().autoAssignPath(player);

        // Only check a player if it is not disabled in the Settings.yml
        if (!plugin.getConfigHandler().isAutomaticPathDisabled()) {
            // Check if player meets requirements
            plugin.getPlayerChecker().checkPlayer(player);
        }

    }
}
