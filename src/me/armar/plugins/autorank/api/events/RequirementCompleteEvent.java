package me.armar.plugins.autorank.api.events;

import me.armar.plugins.autorank.playerchecker.requirement.Requirement;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when a player meets the requirement for a group and
 * certain results will be performed.
 * 
 * @author Staartvin
 * 
 */
public class RequirementCompleteEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private Requirement requirement;
	private boolean isCancelled;

	/**
	 * @param player Player
	 * @param worldName World
	 * @param groupFrom GroupFrom
	 * @param groupTo GroupTo
	 */
	public RequirementCompleteEvent(Player player, Requirement req) {
		this.player = player;
		this.requirement = req;
	}

	/**
	 * Gets the player involved in this event.
	 * @return player object
	 */
	public Player getPlayer() {
		return player;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.Cancellable#isCancelled()
	 */
	public boolean isCancelled() {
		return isCancelled;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.Cancellable#setCancelled(boolean)
	 */
	@Override
	public void setCancelled(boolean cancel) {
		isCancelled = cancel;
	}

	/**
	 * Get the requirement that has been completed.
	 * @return Requirement that has been completed.
	 */
	public Requirement getRequirement() {
		return requirement;
	}
}
