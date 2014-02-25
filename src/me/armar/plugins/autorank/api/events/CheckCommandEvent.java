package me.armar.plugins.autorank.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when a player wants to check its status via /ar check. <br>
 * This event is cancellable.
 * 
 * @author Staartvin
 * 
 */
public class CheckCommandEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private boolean isCancelled;

	/**
	 * @param player Player that performs the command
	 */
	public CheckCommandEvent(final Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.Cancellable#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.Cancellable#setCancelled(boolean)
	 */
	@Override
	public void setCancelled(final boolean cancel) {
		isCancelled = cancel;
	}
}
