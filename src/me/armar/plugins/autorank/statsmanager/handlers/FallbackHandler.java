package me.armar.plugins.autorank.statsmanager.handlers;

import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * This class is used when no stats plugin is found. This does not do anything,
 * but it allows Autorank to run without erroring all over the place.
 *
 * @author Staartvin
 */
public class FallbackHandler extends StatsPlugin {

    @Override
    public int getBlocksBroken(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getBlocksMoved(UUID uuid, String worldName) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getBlocksPlaced(UUID uuid, String worldName, Material block) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getDamageTaken(UUID uuid, String worldName) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getFishCaught(UUID uuid, String worldName) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getFoodEaten(UUID uuid, String worldName, Material food) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getItemsCrafted(UUID uuid, String worldName, Material item) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getMobsKilled(UUID uuid, String worldName, EntityType mob) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getPlayersKilled(UUID uuid, String worldName) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getTimePlayed(UUID uuid, String worldName) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getSheepShorn(UUID uuid, String worldName) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public int getTimesVoted(UUID uuid) throws UnsupportedOperationException {
        return 0;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
