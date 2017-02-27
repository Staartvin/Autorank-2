package me.armar.plugins.autorank.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.activity.ActivityTracker.ActionType;

/**
 * This listener will listen to players leaving the server.
 * 
 * @author Staartvin
 * 
 */
public class PlayerQuitListener implements Listener {

    private final Autorank plugin;

    public PlayerQuitListener(final Autorank instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        // Store log-out activity
        plugin.getActivityTracker().addAction(player.getUniqueId(), ActionType.LOGGED_OUT);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKick(final PlayerKickEvent event) {
        final Player player = event.getPlayer();

        // Store log-out activity
        plugin.getActivityTracker().addAction(player.getUniqueId(), ActionType.LOGGED_OUT);
    }
}
