package me.armar.plugins.autorank.hooks.statzapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.hooks.StatzDependency;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * Handles all connections with Statz
 * <p>
 * Date created: 11:47:01 16 jun. 2016
 *
 * @author Staartvin
 */
public class StatzAPIHandler extends DependencyHandler {

    private final Autorank plugin;
    private Statz statz;

    public StatzAPIHandler(final Autorank instance) {
        plugin = instance;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#get()
     */
    @Override
    public Plugin get() {
        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("Statz");

        try {
            // WorldGuard may not be loaded
            if (plugin == null || !(plugin instanceof Statz)) {
                return null; // Maybe you want throw an exception instead
            }

        } catch (final NoClassDefFoundError exception) {
            this.plugin.getLogger()
                    .info("Could not find Statz because it's probably disabled! Does Statz properly enable?");
            return null;
        }

        return plugin;
    }

    public me.staartvin.statz.hooks.DependencyHandler getDependencyHandler(StatzDependency dep) {
        if (!this.isAvailable())
            return null;

        return statz.getStatzAPI().getDependencyHandler(dep);
    }

    public double getSpecificData(UUID uuid, StatsPlugin.StatType statType, RowRequirement... conditions) {
        if (!this.isAvailable())
            return -1;

        Object value;

        switch (statType) {
            case VOTES:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.VOTES, uuid, conditions);
                break;
            case DAMAGE_TAKEN:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.DAMAGE_TAKEN, uuid, conditions);
                break;
            case MOBS_KILLED:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.KILLS_MOBS, uuid, conditions);
                break;
            case PLAYERS_KILLED:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.KILLS_PLAYERS, uuid, conditions);
                break;
            case BLOCKS_MOVED:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.DISTANCE_TRAVELLED, uuid, conditions);
                break;
            case BLOCKS_PLACED:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_PLACED, uuid, conditions);
                break;
            case BLOCKS_BROKEN:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_BROKEN, uuid, conditions);
                break;
            case TIME_PLAYED:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.TIME_PLAYED, uuid, conditions);
                break;
            case ITEMS_CRAFTED:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.ITEMS_CRAFTED, uuid, conditions);
                break;
            case FISH_CAUGHT:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.ITEMS_CAUGHT, uuid, conditions);
                break;
            case TIMES_SHEARED:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.TIMES_SHORN, uuid, conditions);
                break;
            case TOTAL_BLOCKS_BROKEN:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_BROKEN, uuid, conditions);
                break;
            case TOTAL_BLOCKS_PLACED:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_PLACED, uuid, conditions);
                break;
            case FOOD_EATEN:
                value = statz.getStatzAPI().getSpecificData(PlayerStat.FOOD_EATEN, uuid, conditions);
                break;
            default:
                // Unknown statType
                value = 0;
                break;
        }

        if (value == null)
            return 0;

        return (double) value;
    }

    public double getTotalOf(UUID uuid, StatsPlugin.StatType statType, String worldName) {
        if (!this.isAvailable())
            return -1;

        double value;

        if (worldName == null) {
            value = getSpecificData(uuid, statType);
        } else {
            value = getSpecificData(uuid, statType, new RowRequirement("world", worldName));
        }

        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailablePluginLibrary()
     */
    @Override
    public boolean isAvailable() {
        return statz != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
     */
    @Override
    public boolean isInstalled() {
        final Plugin plugin = get();

        return plugin != null && plugin.isEnabled();
    }

    /*
     * (non-Javadoc)
     *
     * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
     */
    @Override
    public boolean setup(final boolean verbose) {
        if (!isInstalled()) {
            if (verbose) {
                plugin.getLogger().info("Statz has not been found!");
            }
            return false;
        } else {
            statz = (Statz) get();

            if (statz != null) {
                if (verbose) {
                    plugin.getLogger().info("Statz has been found and can be used!");
                }
                return true;
            } else {
                if (verbose) {
                    plugin.getLogger().info("Statz has been found but cannot be used!");
                }
                return false;
            }
        }
    }

}
