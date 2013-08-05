package me.armar.plugins.autorank.listeners;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.PlayerPromoteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * This listener will listen if {@link me.armar.plugins.autorank.api.events.PlayerPromoteEvent} is cancelled
 * 
 * @author Staartvin
 *
 */
public class PlayerPromoteListener implements Listener {

	private Autorank plugin;
	
	public PlayerPromoteListener(Autorank instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPromote(PlayerPromoteEvent event) {
		plugin.getEventHandler().doNotPromote(event.isCancelled());
	}
}
