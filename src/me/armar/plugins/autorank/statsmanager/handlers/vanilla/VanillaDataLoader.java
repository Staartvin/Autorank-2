package me.armar.plugins.autorank.statsmanager.handlers.vanilla;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This class loads vanilla statistics data of a player.
 */
public class VanillaDataLoader {


//    /**
//     * Get (fresh) vanilla data of a player for a given world. This may return an empty optional if there is no data
//     * for the given player or the given world doesn't exist.
//     * @param uuid UUID of the player
//     * @param worldName Name of the world
//     * @return vanilla data of the player for the given world.
//     */
//    public Optional<VanillaData> getVanillaData(UUID uuid, String worldName) {
//        Bukkit.getServer().getPlayer('test').getStatistic()
//    }

//    /**
//     * Get (fresh) vanilla data of a player for all worlds. Data of all world is aggregated.
//     * @param uuid UUID of the player
//     * @return aggregated vanilla data of the given player.
//     */
//    public Optional<VanillaData> getVanillaData(UUID uuid) {
//
//    }

    public int getTotalBlocksBroken(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        int count = 0;

        if (player == null) return count;

        for (Material mat : Material.values()) {
            count += player.getStatistic(Statistic.MINE_BLOCK, mat);
        }

        return count;
    }

    public int getBlocksBroken(UUID uuid, Material material) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.MINE_BLOCK, material);
    }

    public int getTotalBlocksPlaced(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        int count = 0;

        if (player == null) return count;

        for (Material mat : Material.values()) {
            if (!mat.isBlock()) continue;
            count += player.getStatistic(Statistic.USE_ITEM, mat);
        }

        return count;
    }

    public int getBlocksPlaced(UUID uuid, Material material) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.USE_ITEM, material);
    }

    public double getDistanceWalked(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.WALK_ONE_CM) / 100.0d; // Return number of meters (or blocks) walked.
    }

    public int getDamageTaken(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.DAMAGE_TAKEN);
    }

    public int getFishCaught(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.FISH_CAUGHT);
    }

    public int getTotalFoodEaten(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        int count = 0;

        if (player == null) return count;

        for (Material mat : Material.values()) {
            if (!mat.isEdible()) continue;
            count += player.getStatistic(Statistic.USE_ITEM, mat);
        }

        return count;
    }

    public int getFoodEaten(UUID uuid, Material material) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.USE_ITEM, material);
    }

    public int getTotalItemsCrafted(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        int count = 0;

        if (player == null) return count;

        for (Material mat : Material.values()) {
            count += player.getStatistic(Statistic.CRAFT_ITEM, mat);
        }

        return count;
    }

    public int getItemsCrafted(UUID uuid, Material material) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.CRAFT_ITEM, material);
    }

    public int getTotalMobsKilled(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.MOB_KILLS);
    }

    public int getMobsKilled(UUID uuid, EntityType entityType) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.KILL_ENTITY, entityType);
    }

    public int getTotalPlayersKilled(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.PLAYER_KILLS);
    }

    public int getTimePlayed(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        // Despite it being called 'minute', it's actually in ticks.
        return (int) (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20.0d / 60);
    }

    public int getTimesShearsUsed(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.USE_ITEM, Material.SHEARS);
    }

    public int getAnimalsBred(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.ANIMALS_BRED);
    }

    public int getCakeSlicesEaten(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.ANIMALS_BRED);
    }

    public int getItemsEnchanted(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.ITEM_ENCHANTED);
    }

    public int getTimesDied(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.DEATHS);
    }

    public int getPlantsPotted(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.FLOWER_POTTED);
    }

    public int getTimesTradedWithVillages(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) return 0;

        return player.getStatistic(Statistic.TRADED_WITH_VILLAGER);
    }


}
