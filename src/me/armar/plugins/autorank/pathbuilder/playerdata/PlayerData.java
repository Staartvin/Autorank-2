package me.armar.plugins.autorank.pathbuilder.playerdata;

import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;

import java.util.*;

/**
 * This class represents a record class storing information that is relevant to Autorank for a specific player.
 * Note that this data may not be loaded the first time you want to get data from a player. It's up to the player
 * data storage provider to load player data lazily.
 */
public class PlayerData {

    private UUID uuid = null;

    // Store which paths are completed
    private Collection<Path> completedPaths = new ArrayList<>();

    private Collection<Path> activePaths = new ArrayList<>();

    // For each path, stores what requirements are completed.
    private Map<Path, Collection<CompositeRequirement>> completedRequirements = new HashMap<>();

    // For each path, store which requirements are completed but where the results have not been performed yet.
    private Map<Path, Collection<CompositeRequirement>> completedRequirementsWithoutResults = new HashMap<>();

    // For each path, store which prerequisites have been completed
    private Map<Path, Collection<CompositeRequirement>> completedPrerequisites = new HashMap<>();

    // Store which paths have been chosen, but where the results have not been performed yet.
    private Collection<Path> chosenPathsWithoutResults = new ArrayList<>();

    // Store which paths have been completed, but where the results have not been performed yet.
    private Collection<Path> completedPathsWithoutResults = new ArrayList<>();

    // Whether a player should be hidden from the leaderboard
    private boolean isExemptedFromLeaderboard = false;

    // Whether we should not automatically check the player for paths
    private boolean isAutoCheckingDisabled = false;

    // Whether a player will not get any time added.
    private boolean isExemptedFromTimeAddition = false;


}
