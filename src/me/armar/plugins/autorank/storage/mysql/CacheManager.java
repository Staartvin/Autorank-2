package me.armar.plugins.autorank.storage.mysql;

import me.armar.plugins.autorank.storage.TimeType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is responsible for caching time values for the MySQLStorageProvider. It only stores time for this
 * storage provider.
 */
public class CacheManager {

    // Store cached values in map
    private Map<ComposedKey, Integer> cachedTimeValues = new HashMap<>();

    /**
     * Register a new cached value for a player.
     *
     * @param uuid  UUID of player
     * @param value Value to cache
     */
    public void registerCachedTime(TimeType timeType, UUID uuid, int value) {
        this.cachedTimeValues.put(new ComposedKey(uuid, timeType), value);
    }

    /**
     * Get the cached time of a player
     *
     * @param uuid UUID of the player
     * @return cached time value of a player
     */
    public int getCachedTime(TimeType timeType, UUID uuid) {
        return this.cachedTimeValues.get(new ComposedKey(uuid, timeType));
    }

    /**
     * Check whether a player has cached time.
     *
     * @param uuid UUID of the player
     * @return true if the player has a cached time value, false otherwise.
     */
    public boolean hasCachedTime(TimeType timeType, UUID uuid) {
        ComposedKey composedKey = new ComposedKey(uuid, timeType);

        return this.cachedTimeValues.containsKey(composedKey) && this.cachedTimeValues.get(composedKey) != null;
    }
}

/**
 * Since we want to keep track of a player's time for each time type, we can't have a map with only a singleton key.
 * Hence, I create a temporary ComposedKey object that stores both the UUID and the TimeType. Hence, it acts as a
 * single key, while being composed of two objects.
 */
class ComposedKey {
    private UUID uuid;
    private TimeType timeType;

    ComposedKey(UUID uuid, TimeType timeType) {
        this.uuid = uuid;
        this.timeType = timeType;
    }

    @Override
    public boolean equals(Object comparedKey) {

        if (!(comparedKey instanceof ComposedKey)) {
            return false;
        }

        return ((ComposedKey) comparedKey).uuid.equals(uuid) && ((ComposedKey) comparedKey).timeType.equals(timeType);
    }

    @Override
    public int hashCode() {
        return (uuid.toString() + timeType.toString()).hashCode();
    }

    @Override
    public String toString() {
        return "ComposedKey(" + uuid + ", " + timeType + ")";
    }
}
