package me.armar.plugins.autorank.playtimes;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.TimeType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * This class is responsible for updating a player's play time every x minutes.
 */
public class UpdateTimePlayedTask extends BukkitRunnable {

    private Autorank plugin;

    // UUID of player to keep track of.
    private UUID uuid;

    public UpdateTimePlayedTask(Autorank instance, UUID uuid) {
        this.plugin = instance;
        this.uuid = uuid;
    }

    @Override
    public void run() {

        Player player = plugin.getServer().getPlayer(uuid);

        // Cancel task as player is not online anymore.
        if (player == null || !player.isOnline()) {
            plugin.debugMessage("Cancelling update play time of " + uuid + " as he's not online.");

            if (!this.isCancelled()) {
                this.cancel();
            }

            return;
        }

        // Do calendar check to see if storage provider still has up-to-date info.
        plugin.getStorageManager().doCalendarCheck();

        // Provide debug message so we know what's going on
        plugin.debugMessage("Updating play time of " + player.getName());

        // Check for leaderboard exempt permission -> updates value of
        // leaderboard exemption
        plugin.getPlayerChecker().doLeaderboardExemptCheck(player);

        // Don't add extra time to the player when he is excluded from time updates.
        if (player.hasPermission(AutorankPermission.EXCLUDE_FROM_TIME_UPDATES)) {
            plugin.debugMessage("Player " + player.getName() + " is excluded from time updates by given permissions.");
            return;
        }

        // Check to see if player is afk
        if (plugin.getDependencyManager().isAFK(player)) {
            plugin.debugMessage("Player " + player.getName() + " is AFK and so we don't add time.");
            return;
        }

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

        // Add time to a player's current time for all storage providers.
        for (final TimeType type : TimeType.values()) {
            plugin.getStorageManager().addPlayerTime(type, uuid, PlayTimeManager.INTERVAL_MINUTES);
        }
//
//        // Modify global time
//        if (plugin.getMySQLManager().isMySQLEnabled()) {
//            plugin.getMySQLManager().addGlobalTime(uuid, PlayTimeManager.INTERVAL_MINUTES);
//        }

        // Auto assign path (if possible)
        plugin.getPathManager().autoAssignPath(player);

        // Only check a player if it is not disabled in the Settings.yml
        if (!plugin.getConfigHandler().isAutomaticPathDisabled()) {
            // Check if player meets requirements
            plugin.getPlayerChecker().checkPlayer(player);
        }
    }
}
