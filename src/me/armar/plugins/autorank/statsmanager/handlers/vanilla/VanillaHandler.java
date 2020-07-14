package me.armar.plugins.autorank.statsmanager.handlers.vanilla;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
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
    public int getBlocksBroken(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        if (block == null) {
            return this.dataLoader.getTotalBlocksBroken(uuid);
        } else {
            return this.dataLoader.getBlocksBroken(uuid, block);
        }
    }

    @Override
    public int getBlocksMoved(UUID uuid, String worldName) throws UnsupportedOperationException {
        return (int) this.dataLoader.getDistanceWalked(uuid);
    }

    @Override
    public int getBlocksPlaced(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        if (block == null) {
            return this.dataLoader.getTotalBlocksPlaced(uuid);
        } else {
            return this.dataLoader.getBlocksPlaced(uuid, block);
        }
    }

    @Override
    public int getDamageTaken(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getDamageTaken(uuid);
    }

    @Override
    public int getFishCaught(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getFishCaught(uuid);
    }

    @Override
    public int getFoodEaten(UUID uuid, String worldName, Material food) throws UnsupportedOperationException {

        if (food == null) {
            return this.dataLoader.getTotalFoodEaten(uuid);
        } else {
            return this.dataLoader.getFoodEaten(uuid, food);
        }
    }

    @Override
    public int getItemsCrafted(UUID uuid, String worldName, Material item) throws UnsupportedOperationException {
        if (item == null) {
            return this.dataLoader.getTotalItemsCrafted(uuid);
        } else {
            return this.dataLoader.getItemsCrafted(uuid, item);
        }
    }

    @Override
    public int getMobsKilled(UUID uuid, String worldName, EntityType mob) throws UnsupportedOperationException {
        if (mob == null) {
            return this.dataLoader.getTotalMobsKilled(uuid);
        } else {
            return this.dataLoader.getMobsKilled(uuid, mob);
        }
    }

    @Override
    public int getPlayersKilled(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getTotalPlayersKilled(uuid);
    }

    @Override
    public int getTimePlayed(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getTimePlayed(uuid); // It's already in minutes, so we don't need to convert.
    }

    @Override
    public int getSheepShorn(UUID uuid, String worldName) throws UnsupportedOperationException {
        return this.dataLoader.getTimesShearsUsed(uuid);
    }

    @Override
    public int getTimesVoted(UUID uuid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getAnimalsBred(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getAnimalsBred(uuid);
    }

    @Override
    public int getCakeSlicesEaten(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getCakeSlicesEaten(uuid);
    }

    @Override
    public int getItemsEnchanted(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getItemsEnchanted(uuid);
    }

    @Override
    public int getTimesDied(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getTimesDied(uuid);
    }

    @Override
    public int getPlantsPotted(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getPlantsPotted(uuid);
    }

    @Override
    public int getTimesTradedWithVillagers(UUID uuid) throws UnsupportedOperationException {
        return this.dataLoader.getTimesTradedWithVillages(uuid);
    }

    @Override
    public int getItemThrown(UUID uuid, Material material) throws UnsupportedOperationException {
        return this.dataLoader.getItemThrown(uuid, material);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
