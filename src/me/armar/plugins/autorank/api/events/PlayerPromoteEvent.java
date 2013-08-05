package me.armar.plugins.autorank.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
/**
 * This event is called when a player is going to be promoted to a new group
 * @author Staartvin
 *
 */
public class PlayerPromoteEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private String worldName, groupFrom, groupTo;
    private boolean isCancelled;
 
    /**
     * @param player Player
     * @param worldName World
     * @param groupFrom GroupFrom
     * @param groupTo GroupTo
     */
    public PlayerPromoteEvent(Player player, String worldName, String groupFrom, String groupTo) {
        this.player = player;
        this.worldName = worldName;
        this.groupFrom = groupFrom;
        this.groupTo = groupTo;
    }
 
    public Player getPlayer() {
        return player;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     * Gets the world a player is ranked upon. 
     * If world is null, player will be ranked globally.
     * @return worldName or null if ranked globally
     */
    public String getWorld() {
    	return worldName;
    }
    
    /**
     * Gets the group a player is grouped from
     * @return group a player was in
     */
    public String getGroupFrom() {
    	return groupFrom;
    }
    
    /**
     * Gets the group a player is promoted to
     * @return group where player will be promoted to
     */
    public String getGroupTo() {
    	return groupTo;
    }
    
	public boolean isCancelled() {
		return isCancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		isCancelled = cancel;	
	}
}
