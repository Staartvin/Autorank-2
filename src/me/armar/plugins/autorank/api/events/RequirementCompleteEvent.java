package me.armar.plugins.autorank.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.armar.plugins.autorank.playerchecker.requirement.Requirement;

/**
 * This event is called when a player meets the requirement for a group and
 * certain results will be performed.
 * 
 * @author Staartvin
 * 
 */
public class RequirementCompleteEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean isCancelled;
	private final Player player;

	private final Requirement requirement;

	/**
	 * Create a new RequirementCompleteEvent.
	 * This event represents a requirement that is completed by a player.
	 * 
	 * @param player Player that completed the requirement
	 * @param req Requirement that was completed
	 */
	public RequirementCompleteEvent(final Player player, final Requirement req) {
		this.player = player;
		this.requirement = req;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Gets the player involved in this event.
	 * 
	 * @return player object
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the requirement that has been completed.
	 * 
	 * @return Requirement that has been completed.
	 */
	public Requirement getRequirement() {
		return requirement;
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
