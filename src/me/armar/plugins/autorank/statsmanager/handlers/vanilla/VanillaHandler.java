package me.armar.plugins.autorank.statsmanager.handlers.vanilla;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.query.StatisticQuery;
import me.armar.plugins.autorank.statsmanager.query.parameter.ParameterType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Statistics handler that gets data from vanilla minecraft.
 */
public class VanillaHandler extends StatsPlugin {

    private final Autorank plugin;
    private final VanillaDataLoader dataLoader;

    public VanillaHandler(final Autorank instance) {
        this.plugin = instance;
        this.dataLoader = new VanillaDataLoader();
    }

    @Override
    public int getNormalStat(StatType statType, final UUID uuid, StatisticQuery query) {
        double value = 0;

        switch (statType) {
            case PLAYERS_KILLED:
                return this.dataLoader.getTotalPlayersKilled(uuid);
            case DAMAGE_TAKEN:
                return this.dataLoader.getDamageTaken(uuid);
            case TOTAL_BLOCKS_PLACED:
                return this.dataLoader.getTotalBlocksPlaced(uuid);
            case TOTAL_BLOCKS_BROKEN:
                return this.dataLoader.getTotalBlocksBroken(uuid);
            case TIME_PLAYED:
                return this.dataLoader.getTimePlayed(uuid);
            case FISH_CAUGHT:
                return this.dataLoader.getFishCaught(uuid);
            case ITEMS_CRAFTED:
                return this.dataLoader.getTotalItemsCrafted(uuid);
            case TIMES_SHEARED:
                return this.dataLoader.getTimesShearsUsed(uuid);
            case MOBS_KILLED:
                if (!query.hasParameter(ParameterType.MOB_TYPE)) {
                    value = this.dataLoader.getTotalMobsKilled(uuid);
                } else {

                    EntityType entityType =
                            EntityType.valueOf(query.getParameterValue(ParameterType.MOB_TYPE).orElse(""));

                    value = this.dataLoader.getMobsKilled(uuid, entityType);
                }
                break;
            case BLOCKS_PLACED:
            case BLOCKS_BROKEN:
                if (!query.hasParameter(ParameterType.BLOCK_TYPE)) {
                    value = statType == StatType.BLOCKS_PLACED ? this.dataLoader.getTotalBlocksPlaced(uuid) :
                            this.dataLoader.getTotalBlocksBroken(uuid);
                } else {

                    Material material =
                            Material.getMaterial(query.getParameterValue(ParameterType.BLOCK_TYPE).orElse(""));

                    if (material != null) {
                        value = statType == StatType.BLOCKS_PLACED ? this.dataLoader.getBlocksPlaced(uuid, material)
                                : this.dataLoader.getBlocksBroken(uuid, material);
                    } else {
                        value = 0;
                    }
                }

                break;
            case BLOCKS_MOVED:
                value = this.dataLoader.getDistanceWalked(uuid);
                break;
            case FOOD_EATEN:
                if (!query.hasParameter(ParameterType.FOOD_TYPE)) {
                    value = this.dataLoader.getTotalFoodEaten(uuid);
                } else {

                    Material food = Material.getMaterial(query.getParameterValue(ParameterType.FOOD_TYPE).orElse(""));

                    if (food != null) {
                        value = this.dataLoader.getFoodEaten(uuid, food);
                    } else {
                        value = 0;
                    }
                }
                break;
            default:
                break;
        }

        return (int) value;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
