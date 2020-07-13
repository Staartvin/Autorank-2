package me.armar.plugins.autorank.statsmanager.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.staartvin.statz.database.datatype.RowRequirement;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class StatzHandler extends StatsPlugin {

    private final Autorank plugin;

    private final StatzAPIHandler statzApi;

    public StatzHandler(final Autorank instance, final StatzAPIHandler statzAPI) {
        this.plugin = instance;

        statzApi = statzAPI;
    }

//    @Override
//    public int getNormalStat(StatType statType, final UUID uuid, StatisticQuery query) {
//        // First argument is the world (or null)
//
//        String worldName = query.getParameterValue(ParameterType.WORLD).orElse(null);
//
//        double value = 0;
//
//        switch (statType) {
//            case VOTES:
//            case PLAYERS_KILLED:
//            case DAMAGE_TAKEN:
//            case TOTAL_BLOCKS_PLACED:
//            case TOTAL_BLOCKS_BROKEN:
//            case TIME_PLAYED:
//            case FISH_CAUGHT:
//            case ITEMS_CRAFTED:
//            case TIMES_SHEARED:
//                value = statzApi.getTotalOf(uuid, statType, worldName);
//                break;
//            case MOBS_KILLED:
//                if (!query.hasParameter(ParameterType.MOB_TYPE)) {
//                    value = statzApi.getSpecificData(uuid, statType);
//                } else {
//                    value = statzApi.getSpecificData(uuid, statType,
//                            new RowRequirement("mob", query.getParameterValue(ParameterType.MOB_TYPE).orElse(null)));
//                }
//
//                break;
//            case BLOCKS_PLACED:
//            case BLOCKS_BROKEN:
//                if (!query.hasParameter(ParameterType.BLOCK_TYPE)) {
//                    value = statzApi.getSpecificData(uuid, statType);
//                } else {
//                    value = statzApi.getSpecificData(uuid,
//                            statType, new RowRequirement("block",
//                                    query.getParameterValue(ParameterType.BLOCK_TYPE).orElse(null)));
//                }
//
//                break;
//            case BLOCKS_MOVED:
//                String moveTypeString =
//                        query.getParameterValue(ParameterType.MOVEMENT_TYPE).orElse(MovementTypeParameter
//                        .MovementType.WALK.toString());
//
//                MovementTypeParameter.MovementType movementType =
//                        MovementTypeParameter.MovementType.valueOf(moveTypeString);
//
//                if (movementType == MovementTypeParameter.MovementType.FOOT) {
//                    moveTypeString = "WALK";
//                } else {
//                    moveTypeString = moveTypeString.replace("_", " ");
//                }
//
//                value = statzApi.getSpecificData(uuid, statType, new RowRequirement("moveType", moveTypeString));
//                break;
//            case FOOD_EATEN:
//                if (!query.hasParameter(ParameterType.FOOD_TYPE)) {
//                    value = statzApi.getSpecificData(uuid, statType);
//                } else {
//                    value = statzApi.getSpecificData(uuid, statType,
//                            new RowRequirement("foodEaten",
//                                    query.getParameterValue(ParameterType.FOOD_TYPE).orElse(null)));
//                }
//                break;
//            default:
//                break;
//        }
//
//        return (int) value;
//    }

    @Override
    public int getBlocksBroken(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        if (block == null) {
            return (int) statzApi.getSpecificData(uuid, StatType.BLOCKS_BROKEN);
        } else {
            return (int) statzApi.getSpecificData(uuid, StatType.BLOCKS_BROKEN,
                    new RowRequirement("block", block.name()));
        }
    }

    @Override
    public int getBlocksMoved(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int) statzApi.getTotalOf(uuid, StatType.BLOCKS_MOVED, worldName);
    }

    @Override
    public int getBlocksPlaced(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        if (block == null) {
            return (int) statzApi.getSpecificData(uuid, StatType.BLOCKS_PLACED);
        } else {
            return (int) statzApi.getSpecificData(uuid, StatType.BLOCKS_PLACED,
                    new RowRequirement("block", block.name()));
        }
    }

    @Override
    public int getDamageTaken(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int) statzApi.getTotalOf(uuid, StatType.DAMAGE_TAKEN, worldName);
    }

    @Override
    public int getFishCaught(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int) statzApi.getTotalOf(uuid, StatType.FISH_CAUGHT, worldName);
    }

    @Override
    public int getFoodEaten(UUID uuid, String worldName, Material food) throws UnsupportedOperationException {
        if (food == null) {
            return (int) statzApi.getSpecificData(uuid, StatType.FOOD_EATEN);
        } else {
            return (int) statzApi.getSpecificData(uuid, StatType.FOOD_EATEN,
                    new RowRequirement("foodEaten", food.name()));
        }
    }

    @Override
    public int getItemsCrafted(UUID uuid, String worldName, Material item) throws UnsupportedOperationException {
        if (item == null) {
            return (int) statzApi.getSpecificData(uuid, StatType.ITEMS_CRAFTED);
        } else {
            return (int) statzApi.getSpecificData(uuid, StatType.ITEMS_CRAFTED,
                    new RowRequirement("item", item.name()));
        }
    }

    @Override
    public int getMobsKilled(UUID uuid, String worldName, EntityType mob) throws UnsupportedOperationException {
        if (mob == null) {
            return (int) statzApi.getSpecificData(uuid, StatType.MOBS_KILLED);
        } else {
            return (int) statzApi.getSpecificData(uuid, StatType.MOBS_KILLED,
                    new RowRequirement("mob", mob.name()));
        }
    }

    @Override
    public int getPlayersKilled(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int) statzApi.getTotalOf(uuid, StatType.PLAYERS_KILLED, worldName);
    }

    @Override
    public int getTimePlayed(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int) statzApi.getTotalOf(uuid, StatType.TIME_PLAYED, worldName);
    }

    @Override
    public int getSheepShorn(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int) statzApi.getTotalOf(uuid, StatType.TIMES_SHEARED, worldName);
    }

    @Override
    public int getTimesVoted(UUID uuid) throws UnsupportedOperationException {
        return (int) statzApi.getTotalOf(uuid, StatType.VOTES, null);
    }

    @Override
    public int getAnimalsBred(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getCakeSlicesEaten(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getItemsEnchanted(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTimesDied(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPlantsPotted(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTimesTradedWithVillagers(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
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
