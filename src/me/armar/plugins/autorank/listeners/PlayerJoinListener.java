package me.armar.plugins.autorank.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.activity.ActivityTracker.ActionType;
import me.armar.plugins.autorank.permissions.AutorankPermission;

/**
 * This listener will listen to players joining and send them a message when an
 * update is available or an error has been found
 * 
 * @author Staartvin
 * 
 */
public class PlayerJoinListener implements Listener {

    private final Autorank plugin;

    public PlayerJoinListener(final Autorank instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();


        // Store log-in activity
        plugin.getActivityTracker().addAction(player.getUniqueId(), ActionType.LOGGED_IN);

        // Refresh uuid of the player if it is outdated
        if (plugin.getUUIDStorage().isOutdated(player.getName())) {
            plugin.getUUIDStorage().storeUUID(player.getName(), player.getUniqueId(), player.getName());
        }

        // Cannot check player at this moment. -> try at next automatic task
        if (plugin.getPlayerChecker() == null) {
            plugin.getLogger()
                    .severe("Autorank lost its player checker, this is bad! Please report this to the developers!");
            return;
        }
        
        // Do leaderboard exemption check
        plugin.getPlayerChecker().doLeaderboardExemptCheck(player);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                
                // Try to auto assign path to a player
                plugin.getPathManager().autoAssignPath(player);
                
                // Perform check for player on login
                plugin.getPlayerChecker().checkPlayer(player);
            }
        });

        // Player isn't allowed to see messages.
        if (player.hasPermission(AutorankPermission.NOTICE_ON_UPDATE_AVAILABLE)) {

            // Run check async so server doesn't lag.
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    if (plugin.getUpdateHandler().isUpdateAvailable()) {

                        // Schedule it later so it will appear at the bottom
                        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

                            @Override
                            public void run() {
                                player.sendMessage(
                                        ChatColor.GREEN + plugin.getUpdateHandler().getUpdater().getLatestName()
                                                + ChatColor.GOLD + " is now available for download!");
                                player.sendMessage(ChatColor.GREEN + "Available at: " + ChatColor.GOLD
                                        + plugin.getUpdateHandler().getUpdater().getLatestFileLink());
                            }

                        }, 10L);
                    }
                }
            });

        }

        // If player has notice on warning permission
        if (player.hasPermission(AutorankPermission.NOTICE_ON_WARNINGS)) {

            if (plugin.getWarningManager().getHighestWarning() != null) {

                // Schedule it later so it will appear at the bottom
                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

                    @Override
                    public void run() {
                         plugin.getWarningManager().sendWarnings(player);
                    }

                }, 10L);
            }
        }
    }
}
