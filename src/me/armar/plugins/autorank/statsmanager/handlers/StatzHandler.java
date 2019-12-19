package me.armar.plugins.autorank.statsmanager.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.query.StatisticQuery;
import me.armar.plugins.autorank.statsmanager.query.parameter.ParameterType;
import me.armar.plugins.autorank.statsmanager.query.parameter.implementation.MovementTypeParameter;
import me.staartvin.statz.database.datatype.RowRequirement;

import java.util.UUID;

public class StatzHandler extends StatsPlugin {

    private final Autorank plugin;

    private final StatzAPIHandler statzApi;

    public StatzHandler(final Autorank instance, final StatzAPIHandler statzAPI) {
        this.plugin = instance;

        statzApi = statzAPI;
    }

    @Override
    public int getNormalStat(StatType statType, final UUID uuid, StatisticQuery query) {
        // First argument is the world (or null)

        String worldName = query.getParameterValue(ParameterType.WORLD).orElse(null);

        double value = 0;

        switch (statType) {
            case VOTES:
            case PLAYERS_KILLED:
            case DAMAGE_TAKEN:
            case TOTAL_BLOCKS_PLACED:
            case TOTAL_BLOCKS_BROKEN:
            case TIME_PLAYED:
            case FISH_CAUGHT:
            case ITEMS_CRAFTED:
            case TIMES_SHEARED:
                value = statzApi.getTotalOf(uuid, statType, worldName);
                break;
            case MOBS_KILLED:
                if (!query.hasParameter(ParameterType.MOB_TYPE)) {
                    value = statzApi.getSpecificData(uuid, statType);
                } else {
                    value = statzApi.getSpecificData(uuid, statType,
                            new RowRequirement("mob", query.getParameterValue(ParameterType.MOB_TYPE).orElse(null)));
                }

                break;
            case BLOCKS_PLACED:
            case BLOCKS_BROKEN:
                if (!query.hasParameter(ParameterType.BLOCK_TYPE)) {
                    value = statzApi.getSpecificData(uuid, statType);
                } else {
                    value = statzApi.getSpecificData(uuid,
                            statType, new RowRequirement("block",
                                    query.getParameterValue(ParameterType.BLOCK_TYPE).orElse(null)));
                }

                break;
            case BLOCKS_MOVED:
                String moveTypeString =
                        query.getParameterValue(ParameterType.MOVEMENT_TYPE).orElse(MovementTypeParameter.MovementType.WALK.toString());

                MovementTypeParameter.MovementType movementType =
                        MovementTypeParameter.MovementType.valueOf(moveTypeString);

                if (movementType == MovementTypeParameter.MovementType.FOOT) {
                    moveTypeString = "WALK";
                } else {
                    moveTypeString = moveTypeString.replace("_", " ");
                }

                value = statzApi.getSpecificData(uuid, statType, new RowRequirement("moveType", moveTypeString));
                break;
            case FOOD_EATEN:
                if (!query.hasParameter(ParameterType.FOOD_TYPE)) {
                    value = statzApi.getSpecificData(uuid, statType);
                } else {
                    value = statzApi.getSpecificData(uuid, statType,
                            new RowRequirement("foodEaten",
                                    query.getParameterValue(ParameterType.FOOD_TYPE).orElse(null)));
                }
                break;
            default:
                break;
        }

        return (int) value;
    }

    @Override
    public boolean isEnabled() {
        if (statzApi == null) {
            plugin.getLogger().info("Statz (by Staartvin) api library was not found!");
            return false;
        }

        if (!statzApi.isAvailable()) {
            plugin.getLogger().info("Statz (by Staartvin) is not enabled!");
            return false;
        }

        return true;
    }

}
