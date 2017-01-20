package me.armar.plugins.autorank.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when a player is going to be promoted to a new group
 * 
 * @author Staartvin
 * 
 */
public class PlayerPromoteEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean isCancelled;
    private final Player player;

    private final String worldName, groupFrom, groupTo;

    /**
     * @param player
     *            Player
     * @param worldName
     *            World
     * @param groupFrom
     *            GroupFrom
     * @param groupTo
     *            GroupTo
     */
    public PlayerPromoteEvent(final Player player, final String worldName, final String groupFrom,
            final String groupTo) {
        this.player = player;
        this.worldName = worldName;
        this.groupFrom = groupFrom;
        this.groupTo = groupTo;
    }

    /**
     * Gets the group a player is grouped from
     * 
     * @return group a player was in
     */
    public String getGroupFrom() {
        return groupFrom;
    }

    /**
     * Gets the group a player is promoted to
     * 
     * @return group where player will be promoted to
     */
    public String getGroupTo() {
        return groupTo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the world a player is ranked upon. If world is null, player will be
     * ranked globally.
     * 
     * @return worldName or null if ranked globally
     */
    public String getWorld() {
        return worldName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.event.Cancellable#isCancelled()
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.event.Cancellable#setCancelled(boolean)
     */
    @Override
    public void setCancelled(final boolean cancel) {
        isCancelled = cancel;
    }
}
