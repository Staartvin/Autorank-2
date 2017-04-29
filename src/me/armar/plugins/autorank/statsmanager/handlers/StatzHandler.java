package me.armar.plugins.autorank.statsmanager.handlers;

import java.util.HashMap;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.statzapi.StatzAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.staartvin.statz.database.datatype.RowRequirement;

public class StatzHandler extends StatsPlugin {

    private final Autorank plugin;

    private final StatzAPIHandler statzApi;

    public StatzHandler(final Autorank instance, final StatzAPIHandler statzAPI) {
        this.plugin = instance;

        statzApi = statzAPI;
    }

    @Override
    public int getNormalStat(StatType statType, final UUID uuid, final HashMap<String, Object> arguments) {
        // First argument is the world (or null)

        String worldName = null;

        if (arguments.containsKey("world")) {
            worldName = (arguments.get("world") != null ? arguments.get("world").toString() : null);
        }

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
                if (!arguments.containsKey("mobType")) {
                    value = statzApi.getSpecificData(uuid, statType);
                } else {
                    value = statzApi.getSpecificData(uuid, statType,
                            new RowRequirement("mob", arguments.get("mobType").toString()));
                }

                break;
            case BLOCKS_PLACED:
            case BLOCKS_BROKEN:
                if (!arguments.containsKey("dataValue")) {
                    value = statzApi.getSpecificData(uuid, statType,
                            new RowRequirement("typeid", arguments.get("typeID").toString()));
                } else {
                    value = statzApi.getSpecificData(uuid, statType,
                            new RowRequirement("typeid", arguments.get("typeID").toString()),
                            new RowRequirement("datavalue", arguments.get("dataValue").toString()));
                }

                break;
            case BLOCKS_MOVED:
                String moveType = "";

                int moveTypeInt = (Integer) arguments.get("moveType");

                if (moveTypeInt == 1) {
                    moveType = "BOAT";
                } else if (moveTypeInt == 2) {
                    moveType = "MINECART";
                } else if (moveTypeInt == 3) {
                    moveType = "PIG";
                } else if (moveTypeInt == 4) {
                    moveType = "PIG IN MINECART";
                } else if (moveTypeInt == 5) {
                    moveType = "HORSE";
                } else {
                    moveType = "WALK";
                }

                value = statzApi.getSpecificData(uuid, statType, new RowRequirement("moveType", moveType));
                break;
            case FOOD_EATEN:
                if (!arguments.containsKey("foodType")) {
                    value = statzApi.getSpecificData(uuid, statType);
                } else {
                    value = statzApi.getSpecificData(uuid, statType,
                            new RowRequirement("foodEaten", arguments.get("foodType").toString()));
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
