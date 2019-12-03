package me.armar.plugins.autorank.leaderboard;

import me.armar.plugins.autorank.storage.TimeType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class represent a leaderboard that is kept by Autorank.
 */
public class AutorankLeaderboard {

    private TimeType timeType;
    private Map<String, Integer> leaderboard = new LinkedHashMap<>();
    private boolean isSorted = false; // Keep track whether we are sorted

    public AutorankLeaderboard(TimeType timeType) {
        this.setTimeType(timeType);
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }

    public void addAll(Map<String, Integer> map) {
        leaderboard.putAll(map);
        isSorted = false;
    }

    public void add(String playerName, int value) {
        leaderboard.put(playerName, value);
        isSorted = false;
    }

    public void sortLeaderboard() {
        // Don't sort if it's already sorted.
        if (isSorted()) {
            return;
        }

        // Sort the map by value and return it to the leaderboard
        leaderboard =
                leaderboard.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        isSorted = true;
    }

    public Map<String, Integer> getLeaderboard() {
        return Collections.unmodifiableMap(leaderboard);
    }

    public boolean isSorted() {
        return isSorted;
    }

    public void setSorted(boolean sorted) {
        isSorted = sorted;
    }

    public int size() {
        return this.leaderboard.size();
    }
}
