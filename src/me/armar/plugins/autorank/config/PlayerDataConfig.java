package me.armar.plugins.autorank.config;

import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * PlayerDataConfig stores all the properties of players. Autorank needs to
 * store which path a player has chosen and which requirements they already met.
 * 
 * PlayerDataConfig uses a file (/playerdata/Playerdata.yml) which keeps tracks
 * of these things.
 * 
 * @author Staartvin
 * 
 */
public class PlayerDataConfig {

    private final Autorank plugin;

    private SimpleYamlConfiguration config;

    private String fileName = "PlayerData.yml";

    private boolean convertingData = false;

    public PlayerDataConfig(final Autorank instance) {
        this.plugin = instance;

        // Start requirement saver task
        // Run save task every 2 minutes
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                saveConfig();
            }
        }, AutorankTools.TICKS_PER_MINUTE, AutorankTools.TICKS_PER_MINUTE * 2);
    }

    /**
     * Create a new PlayerData.yml file.
     */
    public void createNewFile() {
        config = new SimpleYamlConfiguration(plugin, "/playerdata/" + fileName, fileName);

        plugin.getLogger().info("PlayerData file loaded (" + fileName + ")");
    }

    /**
     * Get the PlayerData.yml file.
     * 
     * @return the PlayerData.yml
     */
    public FileConfiguration getConfig() {
        if (config != null) {
            return config;
        }

        return null;
    }

    /**
     * Reload the PlayerData.yml file.
     */
    public void reloadConfig() {
        if (config != null) {
            config.reloadFile();
        }
    }

    /**
     * Save the PlayerData.yml file.
     */
    public void saveConfig() {
        if (config == null) {
            return;
        }

        config.saveFile();
    }

    /**
     * Add a requirement that is completed by a player.
     * 
     * @param uuid
     *            UUID of the player
     * @param reqID
     *            ID of the requirement
     */
    public void addCompletedRequirement(final UUID uuid, final int reqID) {
        final List<Integer> progress = getCompletedRequirements(uuid);

        if (hasCompletedRequirement(reqID, uuid))
            return;

        progress.add(reqID);

        setCompletedRequirements(uuid, progress);
    }

    /**
     * Set the completed requirements a player has.
     * 
     * @param uuid
     *            UUID of the player
     * @param requirements
     *            Requirements that the player completed.
     */
    public void setCompletedRequirements(final UUID uuid, final List<Integer> requirements) {
        config.set(uuid.toString() + ".completed requirements", requirements);
    }

    /**
     * Get a list of completed requirements of a player. <br>
     * This list is reset when a player chooses a new path or completes a path.
     * 
     * @param uuid
     *            UUID of the player
     * @return a list of requirements a player completed.
     */
    public List<Integer> getCompletedRequirements(final UUID uuid) {
        return config.getIntegerList(uuid.toString() + ".completed requirements");
    }

    /**
     * Check whether a player completed a specific requirement.
     * 
     * @param reqID
     *            ID of requirement
     * @param uuid
     *            UUID of the player
     * @return true if the player completed the given requirement. False
     *         otherwise.
     */
    public boolean hasCompletedRequirement(final int reqID, final UUID uuid) {
        final List<Integer> completedRequirement = getCompletedRequirements(uuid);

        return completedRequirement.contains(reqID);
    }

    /**
     * Add a prerequisite that is completed by a player.
     * 
     * @param uuid
     *            UUID of the player
     * @param preReqID
     *            ID of the prerequisite
     */
    public void addCompletedPrerequisite(final UUID uuid, final int preReqID) {
        final List<Integer> progress = getCompletedPrerequisites(uuid);

        if (hasCompletedPrerequisite(preReqID, uuid))
            return;

        progress.add(preReqID);

        setCompletedPrerequisites(uuid, progress);
    }

    /**
     * Set the completed prerequisites a player has.
     * 
     * @param uuid
     *            UUID of the player
     * @param prerequisites
     *            Prerequisites that the player completed.
     */
    public void setCompletedPrerequisites(final UUID uuid, final List<Integer> prerequisites) {
        config.set(uuid.toString() + ".completed prerequisites", prerequisites);
    }

    /**
     * Get a list of completed prerequisites of a player. <br>
     * This list is reset when a player chooses a new path or completes a path.
     * 
     * @param uuid
     *            UUID of the player
     * @return a list of prerequisites a player completed.
     */
    public List<Integer> getCompletedPrerequisites(final UUID uuid) {
        return config.getIntegerList(uuid.toString() + ".completed prerequisites");
    }

    /**
     * Check whether a player completed a specific prerequisite.
     * 
     * @param preReqId
     *            ID of prerequisite
     * @param uuid
     *            UUID of the player
     * @return true if the player completed the given prerequisite. False
     *         otherwise.
     */
    public boolean hasCompletedPrerequisite(final int preReqId, final UUID uuid) {
        final List<Integer> completedPrerequisites = getCompletedPrerequisites(uuid);

        return completedPrerequisites.contains(preReqId);
    }

    /**
     * Add a path that is completed by a player.
     * 
     * @param uuid
     *            UUID of the player
     * @param pathName
     *            Name (internal name) of the path
     */
    public void addCompletedPath(final UUID uuid, final String pathName) {
        final List<String> completed = getCompletedPaths(uuid);

        if (completed.contains(pathName)) {
            return;
        }

        completed.add(pathName);

        setCompletedPaths(uuid, completed);
    }

    /**
     * In an earlier version of Autorank, the PlayerData.yml file stored users
     * as usernames. This function can be used to change the usernames to UUIDs.
     * This will probably be removed in the future.
     */
    public void convertNamesToUUIDs() {

        if (convertingData)
            return;

        convertingData = true;

        plugin.getLogger().info("Starting to convert playerdata.yml");

        // Run async to prevent problems.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                // Backup beforehand
                plugin.getBackupManager().backupFile("/playerdata/playerdata.yml", null);

                for (final String name : getConfig().getKeys(false)) {

                    // Probably UUID because names don't have dashes.
                    if (name.contains("-"))
                        continue;

                    final UUID uuid = plugin.getUUIDStorage().getStoredUUID(name);

                    if (uuid == null)
                        continue;

                    final List<Integer> progress = config.getIntegerList(name + ".progress");
                    final String lastKnownGroup = config.getString(name + ".last group");

                    // Remove name
                    config.set(name, null);

                    // Replace name with UUID
                    config.set(uuid.toString() + ".progress", progress);
                    config.set(uuid.toString() + ".last group", lastKnownGroup);
                }

                plugin.getLogger().info("Converted playerdata.yml to UUID format");
            }
        });
    }

    /**
     * Get the path a player has chosen.
     * 
     * @param uuid
     *            UUID of the player
     * @return name of the path a player has chosen, or 'unknown' if (s)he did
     *         not choose a path (yet).
     */
    public String getChosenPath(final UUID uuid) {
        return config.getString(uuid.toString() + ".chosen path", "unknown");
    }

    /**
     * Get a list of paths that a player completed.
     * 
     * @param uuid
     *            UUID of the player
     * @return a list of path names that the given player completed.
     */
    public List<String> getCompletedPaths(final UUID uuid) {
        final List<String> completed = config.getStringList(uuid.toString() + ".completed paths");

        return completed;
    }

    /**
     * Check whether a player has completed a specific path.
     * 
     * @param uuid
     *            UUID of the player
     * @param pathName
     *            Name of path
     * @return true if the given player has completed the given path. False
     *         otherwise.
     */
    public boolean hasCompletedPath(final UUID uuid, final String pathName) {
        // If player can rank up forever on the same rank, we will always return
        // false.
        if (plugin.getPathsConfig().allowInfinitePathing(pathName)) {
            return false;
        }

        return getCompletedPaths(uuid).contains(pathName);
    }

    /**
     * Check whether a player is exempted from appearing on any leaderboard.
     * 
     * @param uuid
     *            UUID of the player
     * @return true if the given player is not allowed to be shown on any
     *         leaderboard. False otherwise.
     */
    public boolean hasLeaderboardExemption(final UUID uuid) {
        return config.getBoolean(uuid.toString() + ".exempt leaderboard", false);
    }

    /**
     * Set whether a player is exempted from appearing on any leaderboard.
     * 
     * @param uuid
     *            UUID of the player
     * @param value
     *            Value to set the exemption status to.
     */
    public void hasLeaderboardExemption(final UUID uuid, final boolean value) {
        config.set(uuid.toString() + ".exempt leaderboard", value);
    }
    
    /**
     * Add a path to the started path list.
     * @param uuid UUID of the player
     * @param pathName Name of the path
     */
    public void addStartedPath(UUID uuid, String pathName) {
        // Don't add a path if it's already in there.
        if (this.getStartedPaths(uuid).contains(pathName)) return;
        
        List<String> startedPaths = this.getStartedPaths(uuid);
        
        startedPaths.add(pathName);
        
        this.setStartedPaths(uuid, startedPaths);
    }
    
    /**
     * Remove a path from the started path list.
     * @param uuid UUID of the player
     * @param pathName Name of the path
     */
    public void removeStartedPath(UUID uuid, String pathName) {
        // Don't remove a path if it's not in there.
        if (!this.getStartedPaths(uuid).contains(pathName)) return;
        
        List<String> startedPaths = this.getStartedPaths(uuid);
        
        startedPaths.remove(pathName);
        
        this.setStartedPaths(uuid, startedPaths);
    }
    
    /**
     * Get a list of paths that the given player started but did not complete yet.
     * When a path is in this list it does necessarily mean that this path is still his active path.
     * Players can choose a new path at any point in time they like.
     * 
     * @param uuid UUID of the player
     * @return a list of path names that the player started
     */
    public List<String> getStartedPaths(UUID uuid) {
        return config.getStringList(uuid + ".started paths");
    }
    
    /**
     * Set the paths that the given player started.
     * @param uuid UUID of the player
     * @param pathNames The paths the player started
     */
    public void setStartedPaths(UUID uuid, List<String> pathNames) {      
        config.set(uuid + ".started paths", pathNames);
    }
    
    /**
     * Check whether a path has been started by a player.
     * @param uuid UUID of the player
     * @param pathName Name of the path
     * @return true if the player has started this path, false otherwise (if this path was completed or never started)
     */
    public boolean hasStartedPath(UUID uuid, String pathName) {       
        return this.getStartedPaths(uuid).contains(pathName);
    }

    /**
     * Set the path that a player has chosen.
     * 
     * @param uuid
     *            UUID of the player
     * @param path
     *            Name of path
     */
    public void setChosenPath(final UUID uuid, final String path) {
        config.set(uuid.toString() + ".chosen path", path);
    }

    /**
     * Set the paths that a player has completed.
     * 
     * @param uuid
     *            UUID of the player
     * @param completedPaths
     *            Paths that the player has completed
     */
    public void setCompletedPaths(final UUID uuid, final List<String> completedPaths) {
        config.set(uuid.toString() + ".completed paths", completedPaths);
    }
}
