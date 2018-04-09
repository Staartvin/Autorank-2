package me.armar.plugins.autorank.listeners;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
        final Player player = event.getPlayer();

        // Stop task that updates the play time of a player
        plugin.getTaskManager().stopUpdatePlayTimeTask(player.getUniqueId());
    }
}
