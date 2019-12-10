package me.armar.plugins.autorank.listeners;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.ExecutionException;

/**
 * This listener will listen to players joining and perform actions based on the player.
 *
 * @author Staartvin
 */
public class PlayerJoinListener implements Listener {

    private final Autorank plugin;

    public PlayerJoinListener(final Autorank instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // Refresh uuid of the player if it is outdated
        if (plugin.getUUIDStorage().isOutdated(player.getName())) {
            try {
                plugin.getUUIDStorage().storeUUID(player.getName(), player.getUniqueId()).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Save whether this player is exempted from the leaderboard.
        plugin.getPlayerChecker().doOfflineExemptionChecks(player);

        // Try to automatically assign a path to a player.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            // Try to auto assign path to a player
            plugin.getPathManager().autoAssignPaths(player.getUniqueId());

            // Perform check for player on login
            plugin.getPlayerChecker().checkPlayer(player.getUniqueId());
        });

        // Only display 'update available' message to users with correct permissions.
        if (player.hasPermission(AutorankPermission.NOTICE_ON_UPDATE_AVAILABLE)) {

            // Run check async so server doesn't lag.
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                if (plugin.getUpdateHandler().isUpdateAvailable()) {

                    // Schedule it later so it will appear at the bottom
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        player.sendMessage(
                                ChatColor.GREEN + plugin.getName() + " " + plugin.getUpdateHandler()
                                        .getUpdater().getLatestVersion() + ChatColor.GOLD + " is now " +
                                        "available for download!");

                        player.sendMessage(ChatColor.GREEN + "Available at: " + ChatColor.GOLD
                                + plugin.getUpdateHandler().getUpdater().getResourceURL());
                    }, 10L);
                }
            });

        }

        // Only show warnings to users with correct permission.
        if (player.hasPermission(AutorankPermission.NOTICE_ON_WARNINGS)) {

            if (plugin.getWarningManager().getHighestWarning() != null) {
                // Schedule it later so it will appear at the bottom
                plugin.getServer().getScheduler().runTaskLater(plugin,
                        () -> plugin.getWarningManager().sendWarnings(player), 10L);
            }
        }

        // Register new task that updates the play time of a player
        plugin.getTaskManager().startUpdatePlayTimeTask(player.getUniqueId());
    }
}
