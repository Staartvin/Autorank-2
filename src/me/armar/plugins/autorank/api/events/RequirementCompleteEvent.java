package me.armar.plugins.autorank.api.events;

import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * This event is called when a player meets the requirement for a path and
 * certain results will be performed.
 *
 * @author Staartvin
 */
public class RequirementCompleteEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean isCancelled;
    private final UUID uuid;

    private final CompositeRequirement reqHolder;

    /**
     * Create a new RequirementCompleteEvent. This event represents a
     * requirement that is completed by a player.
     *
     * @param uuid      UUID of the player that completed the requirement
     * @param reqHolder {@linkplain CompositeRequirement} that was completed
     */
    public RequirementCompleteEvent(final UUID uuid, final CompositeRequirement reqHolder) {
        this.uuid = uuid;
        this.reqHolder = reqHolder;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the player involved in this event. Note that the player does not have to be online for a requirement to
     * be completed.
     *
     * @return player object
     */
    public UUID getPlayer() {
        return uuid;
    }

    /**
     * Get the {@linkplain CompositeRequirement} that has been completed.
     *
     * @return {@linkplain CompositeRequirement} that has been completed.
     */
    public CompositeRequirement getRequirement() {
        return reqHolder;
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
