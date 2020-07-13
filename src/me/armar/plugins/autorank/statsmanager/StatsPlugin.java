package me.armar.plugins.autorank.statsmanager;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * An abstract class that defines methods to obtain statistical information of players. Note that none of the methods
 * have to be defined for the given statistics plugin. If a statistic is not defined for a given plugin, an
 * {@link UnsupportedOperationException} is thrown.
 */
public abstract class StatsPlugin {


    /**
     * Get the number of blocks that are broken of a specific type. If no type is given, the total number of broken
     * blocks is returned.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the blocks should be broken. Null means on all worlds.
     * @param block     Block material of the broken blocks or null if the total number of blocks is requested.
     * @return number of blocks broken of a specific type or total number of blocks broken if no block type is
     * specified.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getBlocksBroken(UUID uuid, String worldName, Material block) throws UnsupportedOperationException;

    /**
     * Get the number of blocks a player has moved.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the player should have moved. Null means on all worlds.
     * @return Number of blocks a player has moved.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getBlocksMoved(UUID uuid, String worldName) throws UnsupportedOperationException;

    /**
     * Get the number of blocks that are placed of a specific type. If no type is given, the total number of placed
     * blocks is returned.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the blocks should be placed. Null means on all worlds.
     * @param block     Block material of the placed blocks or null if the total number of blocks is requested.
     * @return number of blocks placed of a specific type or total number of blocks placed if no block type is
     * specified.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getBlocksPlaced(UUID uuid, String worldName, Material block) throws UnsupportedOperationException;

    /**
     * Get the total damage taken (in points) by a player.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the damage must be taken. Null means on all worlds.
     * @return total damage taken by a player.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getDamageTaken(UUID uuid, String worldName) throws UnsupportedOperationException;

    /**
     * Get the number of fish caught (on a specific world)
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the fish should be caught. Null means all worlds
     * @return number of fish caught by the player.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getFishCaught(UUID uuid, String worldName) throws UnsupportedOperationException;

    /**
     * Get the number of consumed food items for a specific item, or the total number of consumed items if no type is
     * specified.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the items should be consumed. Null means all worlds.
     * @param food      Type of food to check or null if interested in the total number of items consumed.
     * @return number of food items consumed of a specific type, or the total number of food items consumed.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getFoodEaten(UUID uuid, String worldName, Material food) throws UnsupportedOperationException;

    /**
     * Get the number of items crafted of a specific type, or the total number of crafted items.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the items should be crafted. Null means all worlds.
     * @param item      Type of the item that should be crafted, or null for the total number of crafted items.
     * @return the number of items crafted of a specific type or the total number of items crafted.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getItemsCrafted(UUID uuid, String worldName, Material item) throws UnsupportedOperationException;

    /**
     * Get the number of mobs a player has killed of a specific type, or the total number of mobs killed.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world the mobs should be killed on. Null means all worlds.
     * @param mob       Type of mob to check or null for the total number of mobs killed.
     * @return the number of mobs killed of a specific type or the total number of mobs killed.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getMobsKilled(UUID uuid, String worldName, EntityType mob) throws UnsupportedOperationException;

    /**
     * Get the total number of players killed by a player.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the players should be killed. Null means all worlds.
     * @return the total number of players killed by a player.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getPlayersKilled(UUID uuid, String worldName) throws UnsupportedOperationException;

    /**
     * Get the total play time (in minutes) of a player.
     *
     * @param uuid      UUID of the player.
     * @param worldName Name of the world of which the play time should be obtained. Null means all worlds.
     * @return total play time of a player.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getTimePlayed(UUID uuid, String worldName) throws UnsupportedOperationException;

    /**
     * Get total number of sheep shorn by a player.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the sheep should be shorn
     * @return number of sheeps shorn.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getSheepShorn(UUID uuid, String worldName) throws UnsupportedOperationException;

    /**
     * Get total number of votes of a player
     *
     * @param uuid UUID of the player
     * @return number of votes of a player.
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getTimesVoted(UUID uuid) throws UnsupportedOperationException;

    /**
     * Get the number of animals a player has bred.
     *
     * @param uuid UUID of the player
     * @return number of animals bred
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getAnimalsBred(UUID uuid) throws UnsupportedOperationException;

    /**
     * Get the number of cake slices a player has eaten
     *
     * @param uuid UUID of the player
     * @return number of cake slices added
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getCakeSlicesEaten(UUID uuid) throws UnsupportedOperationException;

    /**
     * Get the number of items that a player has enchanted
     *
     * @param uuid UUID of the player
     * @return number of enchanted items
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getItemsEnchanted(UUID uuid) throws UnsupportedOperationException;

    /**
     * Get the number of deaths a player has had.
     *
     * @param uuid UUID of the player
     * @return how often the player has died
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getTimesDied(UUID uuid) throws UnsupportedOperationException;

    /**
     * Get the number of plants a player has potted.
     *
     * @param uuid UUID of the player
     * @return number of plants potted
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getPlantsPotted(UUID uuid) throws UnsupportedOperationException;

    /**
     * Get the number of trades a player has made with a villager
     *
     * @param uuid UUID of the player
     * @return number of trades with a villager
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public abstract int getTimesTradedWithVillagers(UUID uuid) throws UnsupportedOperationException;

    /**
     * Check whether the current stats plugin is enabled or not.
     *
     * @return true if enabled; false otherwise
     */
    public abstract boolean isEnabled();

    public enum StatType {
        BLOCKS_BROKEN, BLOCKS_MOVED, BLOCKS_PLACED, DAMAGE_TAKEN, FISH_CAUGHT, FOOD_EATEN, ITEMS_CRAFTED,
        MOBS_KILLED, PLAYERS_KILLED, TIME_PLAYED, TIMES_SHEARED, VOTES
    }
}
