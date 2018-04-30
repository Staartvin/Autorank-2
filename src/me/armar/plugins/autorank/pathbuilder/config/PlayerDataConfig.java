package me.armar.plugins.autorank.pathbuilder.config;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.AbstractConfig;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * PlayerDataConfig stores all the properties of players. Autorank needs to
 * store which path a player has chosen and which requirements they already met.
 * <p>
 * PlayerDataConfig uses a file (/playerdata/Playerdata.yml) which keeps tracks
 * of these things.
 *
 * @author Staartvin
 */
public class PlayerDataConfig extends AbstractConfig {

    private String fileName = "/playerdata/PlayerData.yml";

    private boolean convertingData = false;

    public PlayerDataConfig(final Autorank instance) {
        setPlugin(instance);
        setFileName(fileName);

        // Start requirement saver task
        // Run save task every 2 minutes
        this.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(this.getPlugin(), () -> saveConfig(),
                AutorankTools.TICKS_PER_SECOND * 30, AutorankTools.TICKS_PER_SECOND * 30);
    }

    // ------------ COMPLETED REQUIREMENTS ------------

    /**
     * Get a list of completed requirements of a player for a given path. <br>
     * This list is reset when a player completes the path.
     *
     * @param uuid UUID of the player
     * @return a list of requirements a player has completed for a given path.
     */
    public Collection<Integer> getCompletedRequirements(final UUID uuid, String pathName) {
        ConfigurationSection section = this.getProgressOnPathSection(uuid, pathName);

        if (section == null) {
            return new ArrayList<>();
        }

        return section.getIntegerList("completed requirements");
    }

    /**
     * Check whether a player completed a specific requirement of a given path.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path.
     * @param reqId ID of requirement
     * @return true if the player completed the given requirement. False
     * otherwise.
     */
    public boolean hasCompletedRequirement(UUID uuid, String pathName, int reqId) {
        return getCompletedRequirements(uuid, pathName).contains(reqId);
    }

    /**
     * Add a completed requirement of a path for a given player.
     * @param uuid UUID of the player
     * @param pathName Name of the path
     * @param reqId Id of the requirement that has been completed.
     */
    public void addCompletedRequirement(UUID uuid, String pathName, int reqId) {
        // Player has already completed this requirement
        if (hasCompletedRequirement(uuid, pathName, reqId)) {
            return;
        }

        Collection<Integer> completedRequirements = this.getCompletedRequirements(uuid, pathName);

        completedRequirements.add(reqId);

        setCompletedRequirements(uuid, pathName, completedRequirements);
    }

    /**
     * Set the completed requirements of a player for a path.
     * @param uuid UUID of the player
     * @param pathName Name of the path
     * @param requirements Requirements ids to set as completed.
     */
    public void setCompletedRequirements(UUID uuid, String pathName, Collection<Integer> requirements) {
        getProgressOnPathsSection(uuid).set(pathName + ".completed requirements", requirements);
    }

    // ------------ COMPLETED PREREQUISITES ------------

    /**
     * Get a list of completed prerequisites of a player for a given path. <br>
     * This list is reset when a player chooses the path.
     *
     * @param uuid UUID of the player
     * @return a list of prerequisites a player has completed for a given path.
     */
    public Collection<Integer> getCompletedPrerequisites(final UUID uuid, String pathName) {
        ConfigurationSection section = this.getProgressOnPathSection(uuid, pathName);

        if (section == null) {
            return new ArrayList<>();
        }

        return section.getIntegerList("completed prerequisites");
    }

    /**
     * Check whether a player completed a specific prerequisite of a given path..
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path.
     * @param preReqId ID of prerequisite
     * @return true if the player completed the given prerequisite. False
     * otherwise.
     */
    public boolean hasCompletedPrerequisite(UUID uuid, String pathName, int preReqId) {
        return getCompletedPrerequisites(uuid, pathName).contains(preReqId);
    }

    /**
     * Add a completed prerequisite of a path for a given player.
     * @param uuid UUID of the player
     * @param pathName Name of the path
     * @param preReqId Id of the prerequisite that has been completed.
     */
    public void addCompletedPrerequisite(UUID uuid, String pathName, int preReqId) {
        // Player has already completed this prerequisite
        if (hasCompletedPrerequisite(uuid, pathName, preReqId)) {
            return;
        }

        Collection<Integer> completedPrerequisites = this.getCompletedPrerequisites(uuid, pathName);

        completedPrerequisites.add(preReqId);

        setCompletedPrerequisites(uuid, pathName, completedPrerequisites);
    }

    /**
     * Set the completed prerequisites of a player for a path.
     *
     * @param uuid          UUID of the player
     * @param pathName      Name of the path
     * @param prerequisites Prerequisites ids to set as completed.
     */
    public void setCompletedPrerequisites(UUID uuid, String pathName, Collection<Integer> prerequisites) {
        getProgressOnPathsSection(uuid).set(pathName + ".completed prerequisites", prerequisites);
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

        this.getPlugin().getLogger().info("Starting to convert playerdata.yml");

        // Run async to prevent problems.
        this.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.getPlugin(), new Runnable() {

            @Override
            public void run() {
                // Backup beforehand
                getPlugin().getBackupManager().backupFile("/playerdata/playerdata.yml", null);

                for (final String name : getConfig().getKeys(false)) {

                    // Probably UUID because names don't have dashes.
                    if (name.contains("-"))
                        continue;

                    final UUID uuid = getPlugin().getUUIDStorage().getStoredUUID(name);

                    if (uuid == null)
                        continue;

                    final List<Integer> progress = getConfig().getIntegerList(name + ".progress");
                    final String lastKnownGroup = getConfig().getString(name + ".last group");

                    // Remove name
                    getConfig().set(name, null);

                    // Replace name with UUID
                    getConfig().set(uuid.toString() + ".progress", progress);
                    getConfig().set(uuid.toString() + ".last group", lastKnownGroup);
                }

                getPlugin().getLogger().info("Converted playerdata.yml to UUID format");
            }
        });
    }

    // ------------ ACTIVE PATHS ------------

    /**
     * Get active paths for a player
     *
     * @param uuid UUID of the player
     * @return collection of paths that are active for the given player.
     */
    public Collection<String> getActivePaths(final UUID uuid) {

        ConfigurationSection section = this.getActivePathsSection(uuid);

        if (section == null) {
            return new HashSet<>();
        }

        return section.getKeys(false);
    }

    /**
     * Check whether a path is active for a player.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of path
     * @return true if the given path is active for the given player.
     */
    public boolean isActivePath(final UUID uuid, final String pathName) {
        return getActivePaths(uuid).contains(pathName);
    }

    /**
     * Add a path that a player is active.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path that is active.
     */
    public void addActivePath(final UUID uuid, String pathName) {

        // This path is already active, so we don't add it again.
        if (isActivePath(uuid, pathName)) {
            return;
        }

        ConfigurationSection activePathsSection = getActivePathsSection(uuid);

        activePathsSection.set(pathName + ".completed requirements", new ArrayList<>());
    }

    /**
     * Set the paths that are active for a player.
     *
     * @param uuid  UUID of the player
     * @param paths Paths that are set to active.
     */
    public void setActivePaths(final UUID uuid, Collection<String> paths) {
        ConfigurationSection activePathsSection = getActivePathsSection(uuid);

        for (String pathName : paths) {
            activePathsSection.set(pathName + ".completed requirements", new ArrayList<>());
        }
    }

    /**
     * Remove an active path from a player.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path that is active.
     */
    public void removeActivePath(final UUID uuid, String pathName) {

        // This path is not active, so we don't remove it.
        if (!isActivePath(uuid, pathName)) {
            return;
        }

        ConfigurationSection activePathsSection = getActivePathsSection(uuid);

        activePathsSection.set(pathName, null);
    }

    // ------------ COMPLETED PATHS ------------

    /**
     * Get a list of paths that a player completed.
     *
     * @param uuid UUID of the player
     * @return a list of path names that the given player completed.
     */
    public Collection<String> getCompletedPaths(final UUID uuid) {

        ConfigurationSection section = getCompletedPathsSection(uuid);

        if (section == null) {
            return new ArrayList<>();
        }

        return section.getKeys(false);
    }

    /**
     * Check whether a player has completed a specific path.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of path
     * @return true if the given player has completed the given path. False
     * otherwise.
     */
    public boolean hasCompletedPath(final UUID uuid, final String pathName) {
        return getCompletedPaths(uuid).contains(pathName);
    }

    /**
     * Add a path that a player has completed.
     *
     * @param uuid     UUID of the player
     * @param pathName Name of the path that was completed.
     */
    public void addCompletedPath(final UUID uuid, String pathName) {
        ConfigurationSection completedPathSection = getCompletedPathSection(uuid, pathName);

        // Player has not completed the path before.
        if (completedPathSection == null) {
            this.getCompletedPathsSection(uuid).set(pathName + ".completed", 1);
        } else {
            completedPathSection.set("completed", completedPathSection.getInt("completed", 0) + 1);
        }
    }

    /**
     * Remove a completed path from the list of completed paths.
     *
     * @param uuid UUID of the player
     */
    public void removeCompletedPath(final UUID uuid, String pathName) {

        // Don't remove anything when it is not present.
        if (!this.hasCompletedPath(uuid, pathName)) {
            return;
        }

        ConfigurationSection section = getCompletedPathsSection(uuid);

        if (section == null) {
            return;
        }

        section.set(pathName, null);
    }

    // ------------LEADERBOARD EXEMPTION ------------

    /**
     * Check whether a player is exempted from appearing on any leaderboard.
     *
     * @param uuid UUID of the player
     * @return true if the given player is not allowed to be shown on any
     * leaderboard. False otherwise.
     */
    public boolean hasLeaderboardExemption(final UUID uuid) {
        return getPlayerSection(uuid).getBoolean("exempt leaderboard", false);
    }

    /**
     * Set whether a player is exempted from appearing on any leaderboard.
     *
     * @param uuid  UUID of the player
     * @param value Value to set the exemption status to.
     */
    public void hasLeaderboardExemption(final UUID uuid, final boolean value) {
        getPlayerSection(uuid).set("exempt leaderboard", value);
    }

    // ------------ CONFIGURATION SECTIONS ------------

    private ConfigurationSection getPlayerSection(UUID uuid) {

        ConfigurationSection playerSection = this.getConfig().getConfigurationSection(uuid.toString());

        if (playerSection == null) {
            playerSection = this.getConfig().createSection(uuid.toString());
        }

        return playerSection;
    }

    private ConfigurationSection getActivePathsSection(UUID uuid) {
        ConfigurationSection playerSection = getPlayerSection(uuid);

        ConfigurationSection activePathsSection = playerSection.getConfigurationSection("active paths");

        if (activePathsSection == null) {
            activePathsSection = playerSection.createSection("active paths");
        }

        return activePathsSection;
    }

    private ConfigurationSection getActivePathSection(UUID uuid, String pathName) {
        ConfigurationSection section = getActivePathsSection(uuid);

        return section.getConfigurationSection(pathName);
    }

    private ConfigurationSection getCompletedPathsSection(UUID uuid) {
        ConfigurationSection playerSection = getPlayerSection(uuid);

        ConfigurationSection completedPathsSection = playerSection.getConfigurationSection("completed paths");

        if (completedPathsSection == null) {
            completedPathsSection = playerSection.createSection("completed paths");
        }

        return completedPathsSection;
    }

    private ConfigurationSection getCompletedPathSection(UUID uuid, String pathName) {
        ConfigurationSection section = this.getCompletedPathsSection(uuid);

        return section.getConfigurationSection(pathName);
    }

    private ConfigurationSection getProgressOnPathsSection(UUID uuid) {
        ConfigurationSection playerSection = getPlayerSection(uuid);

        ConfigurationSection progressSection = playerSection.getConfigurationSection("progress on paths");

        if (progressSection == null) {
            progressSection = playerSection.createSection("progress on paths");
        }

        return progressSection;
    }

    private ConfigurationSection getProgressOnPathSection(UUID uuid, String pathName) {
        ConfigurationSection section = getProgressOnPathsSection(uuid);

        return section.getConfigurationSection(pathName);
    }
}
