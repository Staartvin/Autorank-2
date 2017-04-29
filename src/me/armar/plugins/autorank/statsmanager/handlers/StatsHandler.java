package me.armar.plugins.autorank.statsmanager.handlers;

import java.util.HashMap;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.staartvin.statz.hooks.handlers.StatsAPIHandler;

public class StatsHandler extends StatsPlugin {

    private final Autorank plugin;

    private final StatsAPIHandler statsApi;;

    public StatsHandler(final Autorank instance, final StatsAPIHandler statsAPI) {
        this.plugin = instance;

        statsApi = statsAPI;
    }

    @Override
    public int getNormalStat(StatType statType, final UUID uuid, final HashMap<String, Object> arguments) {
        // First argument is the world (or null)

        String worldName = null;

        if (arguments != null) {
            if (arguments.containsKey("world")) {
                worldName = (arguments.get("world") != null ? arguments.get("world").toString() : null);
            }
        }

        int value = -1;

        if (statType.equals(StatType.VOTES)) {
            // Handle voting
            value = statsApi.getNormalStat(uuid, "Votes", worldName);
        } else if (statType.equals(StatType.PLAYERS_KILLED)) {
            // Handle players killed
            value = statsApi.getTotalMobsKilled(uuid, "player", worldName);
        } else if (statType.equals(StatType.MOBS_KILLED)) {
            // Handle mobs killed
            // arg[2] == mobType
            value = statsApi.getTotalMobsKilled(uuid, arguments.get("mobType").toString(), worldName);
        } else if (statType.equals(StatType.DAMAGE_TAKEN)) {
            // Handle damage taken
            value = statsApi.getNormalStat(uuid, "Damage taken", worldName);
        } else if (statType.equals(StatType.BLOCKS_PLACED)) {
            // Handle blocks placed
            value = statsApi.getBlocksStat(uuid, Integer.parseInt(arguments.get("typeID").toString()),
                    Integer.parseInt(arguments.get("dataValue").toString()), worldName, "Blocks placed");
        } else if (statType.equals(StatType.BLOCKS_BROKEN)) {
            // Handle blocks broken
            value = statsApi.getBlocksStat(uuid, Integer.parseInt(arguments.get("typeID").toString()),
                    Integer.parseInt(arguments.get("dataValue").toString()), worldName, "Blocks broken");
        } else if (statType.equals(StatType.TOTAL_BLOCKS_PLACED)) {
            // Handle total blocks placed
            value = statsApi.getTotalBlocksPlaced(uuid, worldName);
        } else if (statType.equals(StatType.TOTAL_BLOCKS_BROKEN)) {
            // Handle total blocks placed
            value = statsApi.getTotalBlocksBroken(uuid, worldName);
        } else if (statType.equals(StatType.TIME_PLAYED)) {
            // Handle time played
            value = statsApi.getTotalPlayTime(uuid, worldName);
        } else if (statType.equals(StatType.BLOCKS_MOVED)) {
            // Handle time played
            value = statsApi.getTotalBlocksMoved(uuid, (Integer) arguments.get("moveType"), worldName);
        } else if (statType.equals(StatType.FISH_CAUGHT)) {
            // Handle time played
            value = statsApi.getNormalStat(uuid, "Fish caught", worldName);
        } else if (statType.equals(StatType.ITEMS_CRAFTED)) {
            // Handle time played
            value = statsApi.getNormalStat(uuid, "Items crafted", worldName);
        } else if (statType.equals(StatType.TIMES_SHEARED)) {
            // Handle time played
            value = statsApi.getNormalStat(uuid, "Shears", worldName);
        } else if (statType.equals(StatType.FOOD_EATEN)) {
            // TODO: Not supported by Stats. Fix so only checks amount and not
            // what kind of food.
        }

        return value;
    }

    @Override
    public boolean isEnabled() {
        if (statsApi == null) {
            plugin.getLogger().info("Stats (by Lolmewn) api library was not found!");
            return false;
        }

        if (!statsApi.isAvailable()) {
            plugin.getLogger().info("Stats (by Lolmewn) is not enabled!");
            return false;
        }

        return true;
    }

}
