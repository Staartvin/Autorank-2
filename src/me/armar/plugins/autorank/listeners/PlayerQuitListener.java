package me.armar.plugins.autorank.listeners;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * This listener will listen to players leaving the server
 *
 * @author Staartvin
 */
public class PlayerQuitListener implements Listener {

    private final Autorank plugin;

    public PlayerQuitListener(final Autorank instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        // Stop task that updates the play time of a player
        plugin.getTaskManager().stopUpdatePlayTimeTask(uuid);

        // Check to see when the last time was that we updated the time.
        long lastPlayTimeUpdate = plugin.getTaskManager().getLastPlayTimeUpdate(uuid);

        // Let's check how long it's been since we updated the time of the player.
        if (lastPlayTimeUpdate > 0) {

            double difference = (System.currentTimeMillis() - lastPlayTimeUpdate) / 1000.0 / 60;

            if (difference > 1.0) {

                // Round to the nearest integer as we store time as an integer.
                int roundedDiff = (int) Math.round(difference);

                // Add the 'lost' time to the player's current time.
                plugin.getStorageManager().addPlayerTime(uuid, roundedDiff);

                // Remove the old time.
                plugin.getTaskManager().setLastPlayTimeUpdate(uuid, -1);
            }
        }
    }
}
