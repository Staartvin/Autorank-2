package me.armar.plugins.autorank.statsmanager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.handlers.StatzHandler;
import me.armar.plugins.autorank.statsmanager.handlers.vanilla.VanillaHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class decides which Stats plugin will be used for getting stat storage.
 *
 * @author Staartvin
 */
public class StatisticsManager {

    private final Autorank plugin;
    private final List<StatsPlugin> availableStatsPlugins = new ArrayList<>();

    public StatisticsManager(final Autorank instance) {
        plugin = instance;
    }

    private boolean isPluginAvailable(String pluginName) {
        final Plugin x = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        return x != null;
    }

    public void loadAvailableStatsPlugins() {
        // Clear all current stats plugins.
        this.availableStatsPlugins.clear();

        // Look for Statz
        if (isPluginAvailable("Statz")) {

            plugin.getLogger().info("Found Statz plugin: Statz (by Staartvin)");

            StatsPlugin statsPlugin = new StatzHandler(plugin,
                    (StatzAPIHandler) plugin.getDependencyManager().getDependency(AutorankDependency.STATZ));

            if (!statsPlugin.isEnabled()) {
                plugin.getLogger().info("Couldn't hook into Statz! Make sure the version is correct!");
                return;
            }

            plugin.getLogger().info("Hooked into Statz (by Staartvin)");

            availableStatsPlugins.add(statsPlugin);
        }

        // Look for vanilla statistics
        StatsPlugin statsPlugin = new VanillaHandler(plugin);

        plugin.getLogger().info("Registering statistics of vanilla Minecraft!");

        availableStatsPlugins.add(statsPlugin);
    }

    /**
     * Get the number of blocks that are broken of a specific type. If no type is given, the total number of broken
     * blocks is returned.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the blocks should be broken. Null means on all worlds.
     * @param block     Block material of the broken blocks or null if the total number of blocks is requested.
     * @return number of blocks broken of a specific type or total number of blocks broken if no block type is
     * specified.
     */
    public int getBlocksBroken(UUID uuid, String worldName, Material block) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getBlocksBroken(uuid, worldName, block);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of blocks a player has moved.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the player should have moved. Null means on all worlds.
     * @return Number of blocks a player has moved.
     */
    public int getBlocksMoved(UUID uuid, String worldName) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getBlocksMoved(uuid, worldName);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of blocks that are placed of a specific type. If no type is given, the total number of placed
     * blocks is returned.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the blocks should be placed. Null means on all worlds.
     * @param block     Block material of the placed blocks or null if the total number of blocks is requested.
     * @return number of blocks placed of a specific type or total number of blocks placed if no block type is
     * specified.
     */
    public int getBlocksPlaced(UUID uuid, String worldName, Material block) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getBlocksPlaced(uuid, worldName, block);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the total damage taken (in points) by a player.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the damage must be taken. Null means on all worlds.
     * @return total damage taken by a player.
     */
    public int getDamageTaken(UUID uuid, String worldName) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getDamageTaken(uuid, worldName);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of fish caught (on a specific world)
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the fish should be caught. Null means all worlds
     * @return number of fish caught by the player.
     */
    public int getFishCaught(UUID uuid, String worldName) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getFishCaught(uuid, worldName);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of consumed food items for a specific item, or the total number of consumed items if no type is
     * specified.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the items should be consumed. Null means all worlds.
     * @param food      Type of food to check or null if interested in the total number of items consumed.
     * @return number of food items consumed of a specific type, or the total number of food items consumed.
     */
    public int getFoodEaten(UUID uuid, String worldName, Material food) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getFoodEaten(uuid, worldName, food);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of items crafted of a specific type, or the total number of crafted items.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the items should be crafted. Null means all worlds.
     * @param item      Type of the item that should be crafted, or null for the total number of crafted items.
     * @return the number of items crafted of a specific type or the total number of items crafted.
     */
    public int getItemsCrafted(UUID uuid, String worldName, Material item) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getItemsCrafted(uuid, worldName, item);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of mobs a player has killed of a specific type, or the total number of mobs killed.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world the mobs should be killed on. Null means all worlds.
     * @param mob       Type of mob to check or null for the total number of mobs killed.
     * @return the number of mobs killed of a specific type or the total number of mobs killed.
     */
    public int getMobsKilled(UUID uuid, String worldName, EntityType mob) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getMobsKilled(uuid, worldName, mob);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the total number of players killed by a player.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the players should be killed. Null means all worlds.
     * @return the total number of players killed by a player.
     */
    public int getPlayersKilled(UUID uuid, String worldName) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getPlayersKilled(uuid, worldName);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the total play time (in minutes) of a player.
     *
     * @param uuid      UUID of the player.
     * @param worldName Name of the world of which the play time should be obtained. Null means all worlds.
     * @return total play time of a player.
     */
    public int getTimePlayed(UUID uuid, String worldName) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getTimePlayed(uuid, worldName);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get total number of sheep shorn by a player.
     *
     * @param uuid      UUID of the player
     * @param worldName Name of the world on which the sheep should be shorn
     * @return number of sheeps shorn.
     */
    public int getSheepShorn(UUID uuid, String worldName) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getSheepShorn(uuid, worldName);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get total number of votes of a player
     *
     * @param uuid UUID of the player
     * @return number of votes of a player.
     */
    public int getTimesVoted(UUID uuid) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getTimesVoted(uuid);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of animals a player has bred.
     *
     * @param uuid UUID of the player
     * @return number of animals bred
     */
    public int getAnimalsBred(UUID uuid) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getAnimalsBred(uuid);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of cake slices a player has eaten
     *
     * @param uuid UUID of the player
     * @return number of cake slices added
     */
    public int getCakeSlicesEaten(UUID uuid) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getCakeSlicesEaten(uuid);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of items that a player has enchanted
     *
     * @param uuid UUID of the player
     * @return number of enchanted items
     * @throws UnsupportedOperationException When this statistic is not supported by this statistics plugin.
     */
    public int getItemsEnchanted(UUID uuid) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getItemsEnchanted(uuid);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of deaths a player has had.
     *
     * @param uuid UUID of the player
     * @return how often the player has died
     */
    public int getTimesDied(UUID uuid) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getTimesDied(uuid);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of plants a player has potted.
     *
     * @param uuid UUID of the player
     * @return number of plants potted
     */
    public int getPlantsPotted(UUID uuid) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getPlantsPotted(uuid);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }

    /**
     * Get the number of trades a player has made with a villager
     *
     * @param uuid UUID of the player
     * @return number of trades with a villager
     */
    public int getTimesTradedWithVillagers(UUID uuid) {
        for (StatsPlugin availableStatsPlugin : this.availableStatsPlugins) {
            try {
                return availableStatsPlugin.getTimesTradedWithVillagers(uuid);
            } catch (UnsupportedOperationException e) {
                // Continue to the next available plugin.
            }
        }

        // Return zero if no plugin available
        return 0;
    }
}
