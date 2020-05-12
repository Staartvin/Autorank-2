package me.armar.plugins.autorank.pathbuilder.playerdata.local;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.AbstractConfig;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataManager;
import me.armar.plugins.autorank.pathbuilder.playerdata.PlayerDataStorage;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * PlayerDataConfig stores all the properties of players. Autorank needs to
 * store which path a player has chosen and which requirements they already met.
 * <p>
 * PlayerDataConfig uses a file (/playerdata/Playerdata.yml) which keeps tracks
 * of these things.
 *
 * @author Staartvin
 */
public class LocalPlayerDataStorage extends AbstractConfig implements PlayerDataStorage {

    private boolean convertingData = false;

    public LocalPlayerDataStorage(final Autorank instance) {
        setPlugin(instance);
        setFileName("/playerdata/PlayerData.yml");

        // Start requirement saver task
        // Run save task every 2 minutes
        this.getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(this.getPlugin(), this::saveConfig,
                AutorankTools.TICKS_PER_SECOND * 30, AutorankTools.TICKS_PER_SECOND * 30);

        this.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(this.getPlugin(),
                this::convertFormatToSupportMultiplePathsFormat, 20 * 10);

        // Load the config full of player data.
        this.loadConfig();
    }

    // ------------ COMPLETED REQUIREMENTS ------------


    public Collection<Integer> getCompletedRequirements(final UUID uuid, String pathName) {
        ConfigurationSection section = this.getProgressOnPathSection(uuid, pathName);

        if (section == null) {
            return new ArrayList<>();
        }

        return section.getIntegerList("completed requirements");
    }


    public boolean hasCompletedRequirement(UUID uuid, String pathName, int reqId) {
        return getCompletedRequirements(uuid, pathName).contains(reqId);
    }


    public void addCompletedRequirement(UUID uuid, String pathName, int reqId) {
        // Player has already completed this requirement
        if (hasCompletedRequirement(uuid, pathName, reqId)) {
            return;
        }

        Collection<Integer> completedRequirements = this.getCompletedRequirements(uuid, pathName);

        completedRequirements.add(reqId);

        setCompletedRequirements(uuid, pathName, completedRequirements);
    }


    public void setCompletedRequirements(UUID uuid, String pathName, Collection<Integer> requirements) {
        getProgressOnPathsSection(uuid).set(pathName + ".completed requirements", requirements);
    }

    // ------------ COMPLETED PREREQUISITES ------------


    public Collection<Integer> getCompletedPrerequisites(final UUID uuid, String pathName) {
        ConfigurationSection section = this.getProgressOnPathSection(uuid, pathName);

        if (section == null) {
            return new ArrayList<>();
        }

        return section.getIntegerList("completed prerequisites");
    }


    public boolean hasCompletedPrerequisite(UUID uuid, String pathName, int preReqId) {
        return getCompletedPrerequisites(uuid, pathName).contains(preReqId);
    }

    public void addCompletedPrerequisite(UUID uuid, String pathName, int preReqId) {
        // Player has already completed this prerequisite
        if (hasCompletedPrerequisite(uuid, pathName, preReqId)) {
            return;
        }

        Collection<Integer> completedPrerequisites = this.getCompletedPrerequisites(uuid, pathName);

        completedPrerequisites.add(preReqId);

        setCompletedPrerequisites(uuid, pathName, completedPrerequisites);
    }

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

                    UUID uuid = null;
                    try {
                        uuid = UUIDManager.getUUID(name).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

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

    // ------------ CONVERT PATHS FILE TO NEW FORMAT ------------

    private void convertFormatToSupportMultiplePathsFormat() {

        this.getPlugin().debugMessage("Looking for UUIDs to convert in PlayerData.yml file!");

        int convertedUUIDCount = 0;

        // Loop over all users and convert them
        for (String uuidString : this.getConfig().getKeys(false)) {

            UUID uuid = UUID.fromString(uuidString);

            String chosenPath = this.getConfig().getString(uuidString + ".chosen path");

            // We have already converting this uuid.
            if (chosenPath == null) {
                continue;
            }

            this.getPlugin().debugMessage("Converting UUID " + uuidString + "...");

            // Convert the active path and its completed requirements
            this.addActivePath(uuid, chosenPath);

            // Set any requirement that was completed for this chosen path.
            for (int completedRequirementId : this.getConfig().getIntegerList(uuidString
                    + ".completed requirements")) {

                // Add completed requirement of the chosen path.
                this.addCompletedRequirement(uuid, chosenPath, completedRequirementId);
            }


            java.util.List<String> completedPaths = this.getConfig().getStringList(uuidString + ".completed paths");

            this.getConfig().set(uuidString + ".completed paths", null);

            // Convert the completed paths.
            for (String completedPathName : completedPaths) {
                this.addCompletedPath(uuid, completedPathName);
            }

            // Remove all traces to previous data.
            this.getConfig().set(uuidString + ".chosen path", null);
            this.getConfig().set(uuidString + ".started paths", null);
            this.getConfig().set(uuidString + ".completed requirements", null);

            convertedUUIDCount++;
        }

        this.getPlugin().debugMessage("Converted " + convertedUUIDCount + " uuids to new format.");

        this.saveConfig();
    }


    // ------------ ACTIVE PATHS ------------

    public Collection<String> getActivePaths(final UUID uuid) {

        ConfigurationSection section = this.getActivePathsSection(uuid);

        return section.getKeys(false);
    }

    @Override
    public boolean hasActivePath(UUID uuid, String pathName) {
        return getActivePaths(uuid).contains(pathName);
    }

    public void addActivePath(final UUID uuid, String pathName) {

        // This path is already active, so we don't add it again.
        if (hasActivePath(uuid, pathName)) {
            return;
        }

        ConfigurationSection activePathsSection = getActivePathsSection(uuid);

        activePathsSection.set(pathName + ".started", System.currentTimeMillis());
    }


    public void setActivePaths(final UUID uuid, Collection<String> paths) {
        ConfigurationSection activePathsSection = getActivePathsSection(uuid);

        for (String pathName : paths) {
            activePathsSection.set(pathName + ".started", System.currentTimeMillis());
        }
    }


    public void removeActivePath(final UUID uuid, String pathName) {

        // This path is not active, so we don't remove it.
        if (!hasActivePath(uuid, pathName)) {
            return;
        }

        ConfigurationSection activePathsSection = getActivePathsSection(uuid);

        activePathsSection.set(pathName, null);
    }

    // ------------ COMPLETED PATHS ------------


    public Collection<String> getCompletedPaths(final UUID uuid) {

        ConfigurationSection section = getCompletedPathsSection(uuid);

        return section.getKeys(false);
    }


    public boolean hasCompletedPath(final UUID uuid, final String pathName) {
        return getCompletedPaths(uuid).contains(pathName);
    }


    public void addCompletedPath(final UUID uuid, String pathName) {
        ConfigurationSection completedPathSection = getCompletedPathSection(uuid, pathName);

        // Player has not completed the path before.
        if (completedPathSection == null) {
            this.getCompletedPathsSection(uuid).set(pathName + ".completed", 1);
        } else {
            completedPathSection.set("completed", completedPathSection.getInt("completed", 0) + 1);
        }

        // Store when the player completed path.
        getCompletedPathSection(uuid, pathName).set("completed at", System.currentTimeMillis());
    }

    public void removeCompletedPath(final UUID uuid, String pathName) {

        // Don't remove anything when it is not present.
        if (!this.hasCompletedPath(uuid, pathName)) {
            return;
        }

        ConfigurationSection section = getCompletedPathsSection(uuid);

        section.set(pathName, null);
    }

    @Override
    public void setCompletedPaths(UUID uuid, Collection<String> paths) {
        ConfigurationSection section = getCompletedPathsSection(uuid);

        // First remove the paths that were already there.
        getCompletedPaths(uuid).forEach(completedPath -> section.set(completedPath, null));

        // Add all paths that are on the given list.
        paths.forEach(completedPath -> this.addCompletedPath(uuid, completedPath));
    }


    public int getTimesCompletedPath(final UUID uuid, String pathName) {
        ConfigurationSection completedPathSection = getCompletedPathSection(uuid, pathName);

        if (completedPathSection != null) {
            return completedPathSection.getInt("completed", 0);
        } else {
            return 0;
        }
    }

    @Override
    public Optional<Long> getTimeSinceCompletionOfPath(UUID uuid, String pathName) {
        ConfigurationSection completedPathSection = getCompletedPathSection(uuid, pathName);

        // The player has not completed the path yet
        if (completedPathSection == null) {
            return Optional.empty();
        }

        // Get the time that the path was completed
        long completionTime = completedPathSection.getLong("completed at");

        // Find the difference and divide by 60000 to convert to minutes.
        return Optional.of((System.currentTimeMillis() - completionTime) / 60000);
    }

    // ------------ COMPLETED PATHS WHERE RESULTS ARE NOT PERFORMED YET ------------


    public Collection<String> getCompletedPathsWithMissingResults(@NonNull UUID uuid) {
        ConfigurationSection section = this.getResultsNotPerformedSection(uuid);

        return section.getStringList("completed paths");
    }


    public void addCompletedPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        Collection<String> completedPaths = getCompletedPathsWithMissingResults(uuid);

        completedPaths.add(pathName);

        this.getResultsNotPerformedSection(uuid).set("completed paths", completedPaths);
    }


    public void removeCompletedPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        Collection<String> completedPaths = getCompletedPathsWithMissingResults(uuid);

        completedPaths.remove(pathName);

        this.getResultsNotPerformedSection(uuid).set("completed paths", completedPaths);
    }


    public boolean hasCompletedPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        return getCompletedPathsWithMissingResults(uuid).contains(pathName);
    }

    // ------------ COMPLETED REQUIREMENTS WHERE RESULTS ARE NOT PERFORMED YET ------------


    public List<Integer> getCompletedRequirementsWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        ConfigurationSection section = this.getCompletedRequirementsMissingResultsSection(uuid);

        return section.getIntegerList(pathName);
    }


    public void addCompletedRequirementWithMissingResults(@NonNull UUID uuid, @NonNull String pathName,
                                                          @NonNull int requirementId) {
        List<Integer> completedRequirements = getCompletedRequirementsWithMissingResults(uuid, pathName);

        completedRequirements.add(requirementId);

        this.getCompletedRequirementsMissingResultsSection(uuid).set(pathName, completedRequirements);
    }


    public void removeCompletedRequirementWithMissingResults(@NonNull UUID uuid, @NonNull String pathName,
                                                             @NonNull int requirementId) {
        List<Integer> completedRequirements = getCompletedRequirementsWithMissingResults(uuid, pathName);

        completedRequirements.remove((Integer) requirementId);

        this.getCompletedRequirementsMissingResultsSection(uuid).set(pathName, completedRequirements);
    }


    public boolean hasCompletedRequirementWithMissingResults(@NonNull UUID uuid, @NonNull String pathName,
                                                             @NonNull int requirementId) {
        return getCompletedRequirementsWithMissingResults(uuid, pathName).contains(requirementId);
    }

    // ------------ CHOSEN PATHS WHERE RESULTS (UPON CHOOSING) ARE NOT PERFORMED YET ------------


    public Collection<String> getChosenPathsWithMissingResults(@NonNull UUID uuid) {
        ConfigurationSection section = this.getResultsNotPerformedSection(uuid);

        return section.getStringList("chosen paths");
    }


    public void addChosenPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        Collection<String> chosenPaths = getChosenPathsWithMissingResults(uuid);

        chosenPaths.add(pathName);

        this.getResultsNotPerformedSection(uuid).set("chosen paths", chosenPaths);
    }


    public void removeChosenPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        Collection<String> chosenPaths = getChosenPathsWithMissingResults(uuid);

        chosenPaths.remove(pathName);

        this.getResultsNotPerformedSection(uuid).set("chosen paths", chosenPaths);
    }


    public boolean hasChosenPathWithMissingResults(@NonNull UUID uuid, @NonNull String pathName) {
        return getChosenPathsWithMissingResults(uuid).contains(pathName);
    }

    // ------------PERMISSION EXEMPTIONS ------------


    public boolean hasLeaderboardExemption(final UUID uuid) {
        return getPlayerSection(uuid).getBoolean("exempt leaderboard", false);
    }


    public void setLeaderboardExemption(final UUID uuid, final boolean value) {
        getPlayerSection(uuid).set("exempt leaderboard", value);
    }


    public boolean hasAutoCheckingExemption(final UUID uuid) {
        return getPlayerSection(uuid).getBoolean("exempted from checking", false);
    }


    public void setAutoCheckingExemption(final UUID uuid, final boolean value) {
        getPlayerSection(uuid).set("exempted from checking", value);
    }


    public boolean hasTimeAdditionExemption(final UUID uuid) {
        return getPlayerSection(uuid).getBoolean("exempted from time addition", false);
    }


    public void setTimeAdditionExemption(final UUID uuid, final boolean value) {
        getPlayerSection(uuid).set("exempted from time addition", value);
    }

    @Override
    public PlayerDataManager.PlayerDataStorageType getDataStorageType() {
        return PlayerDataManager.PlayerDataStorageType.LOCAL;
    }

    // ------------ CONFIGURATION SECTIONS ------------

    @NotNull
    private ConfigurationSection getPlayerSection(@NotNull UUID uuid) {

        ConfigurationSection playerSection = this.getConfig().getConfigurationSection(uuid.toString());

        if (playerSection == null) {
            playerSection = this.getConfig().createSection(uuid.toString());
        }

        return playerSection;
    }

    @NotNull
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

    @NotNull
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

    @NotNull
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

    @NotNull
    private ConfigurationSection getResultsNotPerformedSection(UUID uuid) {
        ConfigurationSection section = getPlayerSection(uuid);

        ConfigurationSection returnValue = section.getConfigurationSection("results not performed");

        if (returnValue == null) {
            returnValue = section.createSection("results not performed");
        }

        return returnValue;
    }

    @NotNull
    private ConfigurationSection getCompletedRequirementsMissingResultsSection(UUID uuid) {
        ConfigurationSection section = getResultsNotPerformedSection(uuid);

        ConfigurationSection returnValue = section.getConfigurationSection("completed requirements");

        if (returnValue == null) {
            returnValue = section.createSection("completed requirements");
        }

        return returnValue;
    }
}
