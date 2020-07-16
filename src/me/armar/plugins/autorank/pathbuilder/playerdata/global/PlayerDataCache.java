package me.armar.plugins.autorank.pathbuilder.playerdata.global;

import io.reactivex.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerDataCache {

    private final Map<UUID, CachedPlayerData> cache = new HashMap<>();

    public CachedPlayerData getCachedPlayerData(@NonNull UUID uuid) {

        CachedPlayerData cachedPlayerData = this.cache.get(uuid);

        if (cachedPlayerData == null) {
            cachedPlayerData = new CachedPlayerData();
            this.setCachedPlayerData(uuid, cachedPlayerData);
        }

        return cachedPlayerData;
    }

    public void setCachedPlayerData(@NonNull UUID uuid, CachedPlayerData cachedPlayerData) {
        this.cache.put(uuid, cachedPlayerData);
    }

    public void removeCachedPlayerData(@NonNull UUID uuid) {
        this.cache.remove(uuid);
    }
}

class CachedPlayerData {

    List<CachedPlayerDataEntry> cachedEntries = new ArrayList<>();

    public void addCachedEntry(String completedPath, String serverName) {
        if (!hasCachedEntry(completedPath, serverName)) {
            cachedEntries.add(new CachedPlayerDataEntry(serverName, completedPath));
        }
    }

    public void removeCachedEntry(String completedPath, String serverName) {
        this.cachedEntries.remove(new CachedPlayerDataEntry(serverName, completedPath));
    }

    public boolean hasCachedEntry(String completedPath, String serverName) {
        return getCachedEntriesByPath(completedPath).stream().anyMatch(e -> e.getServerName().equalsIgnoreCase(serverName));
    }

    public List<CachedPlayerDataEntry> getCachedEntriesByPath(String completedPath) {
        return cachedEntries.stream().filter(entry -> entry.getCompletedPath().equalsIgnoreCase(completedPath)).collect(Collectors.toList());
    }

    public List<CachedPlayerDataEntry> getCachedEntriesByServer(String serverName) {
        return cachedEntries.stream().filter(entry -> entry.getServerName().equalsIgnoreCase(serverName)).collect(Collectors.toList());
    }

}

class CachedPlayerDataEntry {

    private String serverName;
    private String completedPath;

    public CachedPlayerDataEntry(@NonNull String serverName, @NonNull String completedPath) {
        this.serverName = serverName;
        this.completedPath = completedPath;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getCompletedPath() {
        return completedPath;
    }

    public void setCompletedPath(String completedPath) {
        this.completedPath = completedPath;
    }

    @Override
    public int hashCode() {
        return (serverName + completedPath).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof CachedPlayerDataEntry)) {
            return false;
        }

        CachedPlayerDataEntry entry = (CachedPlayerDataEntry) obj;

        return entry.getServerName().equalsIgnoreCase(this.getServerName()) && entry.getCompletedPath().equalsIgnoreCase(this.getCompletedPath());
    }
}
