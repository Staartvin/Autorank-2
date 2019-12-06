package me.armar.plugins.autorank.storage.mysql;

import me.armar.plugins.autorank.storage.TimeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This class is responsible for caching time values for the MySQLStorageProvider. It only stores time for this
 * storage provider.
 */
public class CacheManager {

    // Store cached values in map
    private Map<UUID, CachedEntry> cachedTimeValues = new HashMap<>();

    /**
     * Register a new cached value for a player.
     *
     * @param uuid  UUID of player
     * @param value Value to cache
     */
    public void registerCachedTime(TimeType timeType, UUID uuid, int value) {

        CachedEntry entry = cachedTimeValues.get(uuid);

        if (entry != null) {
            entry.setCachedTime(timeType, value);
        } else {
            entry = new CachedEntry(timeType, value);
        }

        this.cachedTimeValues.put(uuid, entry);
    }

    /**
     * Get the cached time of a player
     *
     * @param uuid UUID of the player
     * @return cached time value of a player
     */
    public int getCachedTime(TimeType timeType, UUID uuid) {

        CachedEntry entry = this.cachedTimeValues.get(uuid);

        if (entry == null) {
            return 0;
        }

        return entry.getCachedTime(timeType);
    }

    /**
     * Check whether a player has cached time.
     *
     * @param uuid UUID of the player
     * @return true if the player has a cached time value, false otherwise.
     */
    public boolean hasCachedTime(TimeType timeType, UUID uuid) {
        return this.cachedTimeValues.containsKey(uuid) && this.cachedTimeValues.get(uuid) != null;
    }

    /**
     * Check whether the current time that is cached for a player is outdated.
     *
     * @param timeType Type of time to check for
     * @param uuid     UUID of the player
     * @return true if the cached time is outdated, false otherwise.
     */
    public boolean shouldUpdateCachedEntry(TimeType timeType, UUID uuid) {
        return hasCachedTime(timeType, uuid) && this.cachedTimeValues.get(uuid).isCachedTimeOutdated(timeType);
    }

    public Set<UUID> getCachedUUIDs() {
        return this.cachedTimeValues.keySet();
    }
}

class CachedEntry {
    private Map<TimeType, Integer> timePerTimeType = new HashMap<>();
    private Map<TimeType, Long> lastUpdatedPerTimeType = new HashMap<>();

    public CachedEntry() {
    }

    public CachedEntry(TimeType timeType, int value) {
        this.setCachedTime(timeType, value);
    }

    public long getMinutesSinceLastUpdated(TimeType timeType) {

        Long lastUpdatedTime = lastUpdatedPerTimeType.get(timeType);

        if (lastUpdatedTime == null) {
            return Long.MAX_VALUE;
        }

        return (System.currentTimeMillis() - lastUpdatedTime) / 60000;
    }

    public void setCachedTime(TimeType timeType, int time) {
        timePerTimeType.put(timeType, time);
        lastUpdatedPerTimeType.put(timeType, System.currentTimeMillis());
    }

    public int getCachedTime(TimeType timeType) {
        return timePerTimeType.get(timeType);
    }

    public boolean hasCachedTime(TimeType timeType) {
        return timePerTimeType.containsKey(timeType);
    }

    public boolean isCachedTimeOutdated(TimeType timeType) {
        return hasCachedTime(timeType) && getCachedTime(timeType) >= MySQLStorageProvider.CACHE_EXPIRY_TIME;
    }

    @Override
    public int hashCode() {
        return (timePerTimeType.toString() + lastUpdatedPerTimeType.toString()).hashCode();
    }
}
