package me.armar.plugins.autorank;

import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.api.API;
import me.armar.plugins.autorank.backup.BackupManager;
import me.armar.plugins.autorank.commands.manager.CommandsManager;
import me.armar.plugins.autorank.config.DefaultBehaviorConfig;
import me.armar.plugins.autorank.config.InternalPropertiesConfig;
import me.armar.plugins.autorank.config.PathsConfig;
import me.armar.plugins.autorank.config.SettingsConfig;
import me.armar.plugins.autorank.converter.DataConverter;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.leaderboard.LeaderboardHandler;
import me.armar.plugins.autorank.listeners.PlayerJoinListener;
import me.armar.plugins.autorank.listeners.PlayerQuitListener;
import me.armar.plugins.autorank.pathbuilder.PathManager;
import me.armar.plugins.autorank.pathbuilder.builders.RequirementBuilder;
import me.armar.plugins.autorank.pathbuilder.builders.ResultBuilder;
import me.armar.plugins.autorank.pathbuilder.requirement.*;
import me.armar.plugins.autorank.pathbuilder.result.*;
import me.armar.plugins.autorank.permissions.PermissionsPluginManager;
import me.armar.plugins.autorank.playerchecker.PlayerChecker;
import me.armar.plugins.autorank.playtimes.PlayTimeManager;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.handlers.FallbackHandler;
import me.armar.plugins.autorank.storage.StorageManager;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.flatfile.FlatFileStorageProvider;
import me.armar.plugins.autorank.storage.mysql.MySQLStorageProvider;
import me.armar.plugins.autorank.tasks.TaskManager;
import me.armar.plugins.autorank.updater.UpdateHandler;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.storage.UUIDStorage;
import me.armar.plugins.autorank.validations.ValidateHandler;
import me.armar.plugins.autorank.warningmanager.WarningManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Main class of Autorank
 * <p>
 * Date created: 18:34:00 13 jan. 2014
 *
 * @author Staartvin
 */
public class Autorank extends JavaPlugin {

    private static Autorank autorank;

    // ---------- INITIALIZING VARIABLES ---------- \\
    //
    //
    //

    // Managers
    private PathManager pathManager;

    // ---------- INITIALIZING VARIABLES ---------- \\
    //
    //
    //
    private AddOnManager addonManager;
    private BackupManager backupManager;
    private CommandsManager commandsManager;
    private DependencyManager dependencyManager;
    private LeaderboardHandler leaderboardManager;

    // Handlers
    private LanguageHandler languageHandler;
    private PermissionsPluginManager permPlugHandler;
    private UpdateHandler updateHandler;

    // Miscalleaneous
    private PlayerChecker playerChecker;
    private PlayTimeManager playTimeManager;
    private DataConverter dataConverter;
    // UUID storage
    private UUIDStorage uuidStorage;
    private StorageManager storageManager;
    // Managing periodic tasks
    private TaskManager taskManager;
    // Validation & Warning
    private ValidateHandler validateHandler;
    private WarningManager warningManager;
    private Debugger debugger;
    // Configs
    private SettingsConfig settingsConfig;
    private InternalPropertiesConfig internalPropertiesConfig;
    private PathsConfig pathsConfig;
    private DefaultBehaviorConfig defaultBehaviorConfig;

    public static Autorank getInstance() {
        return autorank;
    }

    // ---------- onEnable() & onDisable() ---------- \\
    //
    //
    //

    /*
     * (non-Javadoc)
     *
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {

        // Make sure all tasks are cancelled after shutdown. This seems obvious,
        // but when a player /reloads, the server creates an instance of the
        // plugin which causes duplicate tasks to run.
        getServer().getScheduler().cancelTasks(this);

        // ------------- Save files and databases -------------

        this.getStorageManager().saveAllStorageProviders();

        getUUIDStorage().saveAllFiles();

        // ------------- Say bye-bye -------------

        getLogger().info(String.format("Autorank %s has been disabled!", getDescription().getVersion()));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        // ------------- Init static variables -------------
        autorank = this;

        // ------------- Create files & folders -------------

        // Register configs
        setPathsConfig(new PathsConfig(this));
        setSettingsConfig(new SettingsConfig(this));
        setInternalPropertiesConfig(new InternalPropertiesConfig(this));
        setDefaultBehaviorConfig(new DefaultBehaviorConfig((this)));

        // Create new configs
        this.getDefaultBehaviorConfig().loadConfig();
        this.getPathsConfig().loadConfig();
        this.getSettingsConfig().loadConfig();
        this.getInternalPropertiesConfig().loadConfig();

        // ------------- Initialize managers -------------

        // Create backup manager
        setBackupManager(new BackupManager(this));

        // Create warning manager
        setWarningManager(new WarningManager(this));

        // Create Storage Manager
        setStorageManager(new StorageManager(this));

        // Load AutorankDependency manager
        setDependencyManager(new DependencyManager(this));

        // Create commands manager
        setCommandsManager(new CommandsManager(this));

        // Create Addon Manager
        setAddonManager(new AddOnManager(this));

        // Create Path Manager
        setPathManager(new PathManager(this));

        // Create Task Manager so we can schedule tasks.
        setTaskManager(new TaskManager(this));

        // ------------- Initialize handlers -------------

        // Create update handler
        setUpdateHandler(new UpdateHandler(this));

        // Create language classes
        setLanguageHandler(new LanguageHandler(this));

        // Create permission plugin handler class
        setPermPlugHandler(new PermissionsPluginManager(this));

        // Create validate handler
        setValidateHandler(new ValidateHandler(this));

        // Create leaderboard class
        setLeaderboardManager(new LeaderboardHandler(this));

        // ------------- Initialize storage -------------

        // Create uuid storage
        setUUIDStorage(new UUIDStorage(this));

        // ------------- Initialize others -------------

        // Create playtime class
        setPlayTimeManager(new PlayTimeManager(this));

        // Create player check class
        setPlayerChecker(new PlayerChecker(this));

        // Set debugger
        setDebugger(new Debugger(this));

        // Run this async, as it can take a bit of time.
        this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                // Load UUID files
                getUUIDStorage().createNewFiles();
            }
        });

        // Load storage converter
        setDataConverter(new DataConverter(this));

        // ------------- Create files & folders -------------

        // Setup language file
        languageHandler.createNewFile();

        // ------------- Register storage providers -------------

        // Register FlatFile storage provider
        getStorageManager().registerStorageProvider(new FlatFileStorageProvider(this));


        if (this.getSettingsConfig().useMySQL()) {
            StorageProvider mysqlStorageProvider = new MySQLStorageProvider(this);

            // Register MySQL storage provider
            getStorageManager().registerStorageProvider(mysqlStorageProvider);

            // Set mysql as primary storage provider.
            if (this.getSettingsConfig().getPrimaryStorageProvider().equalsIgnoreCase("mysql")) {
                getStorageManager().setPrimaryStorageProvider(mysqlStorageProvider);
            }
        }

        this.getLogger().info("Primary storage provider of Autorank: " + this.getStorageManager()
                .getPrimaryStorageProvider().getName());

        // ------------- Initialize requirements and results -------------
        this.initializeReqsAndRes();

        // Start warning task if a warning has been found
        getWarningManager().startWarningTask();

        // ------------- Register listeners -------------

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        // ------------- Schedule tasks -------------

        // Load all third party dependencies
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                try {
                    // Load dependencies
                    dependencyManager.loadDependencies();

                } catch (final Throwable t) {

                    // When an error occurred!

                    getLogger().severe("Could not hook into a dependency: \nCause: ");
                    t.printStackTrace();
                }

                // After dependencies, load paths
                // Initialize paths
                getPathManager().initialiseFromConfigs();

                // Validate paths                
                if (!getValidateHandler().startValidation()) {
                    getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "Detected errors in " +
                            "your Paths.yml file. Log in to your server to see the problems!");
                }

                // Show warnings (if there are any)

                HashMap<String, Integer> warnings = getWarningManager().getWarnings();

                if (warnings.size() > 0) {
                    getLogger().warning("Autorank has some warnings for you: ");
                }

                for (Entry<String, Integer> entry : warnings.entrySet()) {
                    getLogger().warning("(Priority " + entry.getValue() + ") '" + entry.getKey() + "'");
                }

            }
        }, 1L);

        // Run task that updates storage providers if something changed.
        getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                if (!getSettingsConfig().shouldRemoveOldEntries()) return;

                if (!getInternalPropertiesConfig().isConvertedToNewFormat()) return;

                // Remove old entries
                int removed = getStorageManager().getPrimaryStorageProvider().purgeOldEntries();

                getLogger().info("Removed " + removed + " old storage entries from database!");
            }
        }, 0, (long) AutorankTools.TICKS_PER_MINUTE * 60 * 24);

        // ------------- Register commands -------------

        // Register command
        getCommand("autorank").setExecutor(getCommandsManager());

        // ------------- Log messages -------------

        // Debug message telling what plugin is used for timing.
        getLogger().info("Using timings of: " + getSettingsConfig().useTimeOf().toString().toLowerCase());

        debugMessage("Autorank debug is turned on!");

        // ------------- Check version -------------

        // Extra warning for dev users
        if (isDevVersion()) {
            this.getLogger().warning("You're running a DEV version, be sure to backup your Autorank folder!");
            this.getLogger().warning(
                    "DEV versions are not guaranteed to be stable and generally shouldn't be used on big production " +
                            "servers with lots of players.");
        }

        // ------------- Do miscellaneous tasks -------------

        // Start automatic backup
        this.getBackupManager().startBackupSystem();

        // Try to update all leaderboards if needed.
        this.getLeaderboardManager().updateAllLeaderboards();

        // Convert all UUIDS to lowercase.
        this.getUUIDStorage().transferUUIDs();

        // TODO: implement that all storage providers do calendar checks themselves periodically.
        // Check whether the storage files are still up to date.
        this.getStorageManager().doCalendarCheck();

        // ------------- Say Welcome! -------------
        getLogger().info(String.format("Autorank %s has been enabled!", getDescription().getVersion()));

        // Run converter to Autorank 4.0
        getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            @Override
            public void run() {

                // Convert to new format (Autorank 4.0) if needed
                if (!getInternalPropertiesConfig().isConvertedToNewFormat()) {
                    getDataConverter().convertData();
                }
            }
        }, AutorankTools.TICKS_PER_SECOND * 5);
    }

    // ---------- CONVENIENCE METHODS ---------- \\
    //
    //
    //

    private void initializeReqsAndRes() {
        // Register 'main' requirements

        // Autorank related
        RequirementBuilder.registerRequirement("exp", ExpRequirement.class);
        RequirementBuilder.registerRequirement("gamemode", GamemodeRequirement.class);
        RequirementBuilder.registerRequirement("has item", HasItemRequirement.class);
        RequirementBuilder.registerRequirement("location", LocationRequirement.class);
        RequirementBuilder.registerRequirement("global time", GlobalTimeRequirement.class);
        RequirementBuilder.registerRequirement("total time", TotalTimeRequirement.class);
        RequirementBuilder.registerRequirement("world", WorldRequirement.class);
        RequirementBuilder.registerRequirement("permission", PermissionRequirement.class);
        RequirementBuilder.registerRequirement("time", TimeRequirement.class);
        RequirementBuilder.registerRequirement("in biome", InBiomeRequirement.class);
        RequirementBuilder.registerRequirement("has advancement", AdvancementRequirement.class);
        RequirementBuilder.registerRequirement("in group", InGroupRequirement.class);
        RequirementBuilder.registerRequirement("javascript", JavaScriptRequirement.class);
        RequirementBuilder.registerRequirement("active paths", AutorankActivePathsRequirement.class);
        RequirementBuilder.registerRequirement("completed paths", AutorankCompletedPathsRequirement.class);

        // Vault related
        RequirementBuilder.registerRequirement("money", MoneyRequirement.class);

        // Statz related
        RequirementBuilder.registerRequirement("blocks broken", BlocksBrokenRequirement.class);
        RequirementBuilder.registerRequirement("blocks placed", BlocksPlacedRequirement.class);
        RequirementBuilder.registerRequirement("blocks moved", BlocksMovedRequirement.class);
        RequirementBuilder.registerRequirement("votes", TotalVotesRequirement.class);
        RequirementBuilder.registerRequirement("damage taken", DamageTakenRequirement.class);
        RequirementBuilder.registerRequirement("mobs killed", MobKillsRequirement.class);
        RequirementBuilder.registerRequirement("players killed", PlayerKillsRequirement.class);
        RequirementBuilder.registerRequirement("fish caught", FishCaughtRequirement.class);
        RequirementBuilder.registerRequirement("items crafted", ItemsCraftedRequirement.class);
        RequirementBuilder.registerRequirement("times sheared", TimesShearedRequirement.class);
        RequirementBuilder.registerRequirement("food eaten", FoodEatenRequirement.class);

        // Faction related
        RequirementBuilder.registerRequirement("faction power", FactionPowerRequirement.class);

        // WorldGuard related
        RequirementBuilder.registerRequirement("worldguard region", WorldGuardRegionRequirement.class);

        // mcMMO
        RequirementBuilder.registerRequirement("mcmmo skill level", McMMOSkillLevelRequirement.class);
        RequirementBuilder.registerRequirement("mcmmo power level", McMMOPowerLevelRequirement.class);

        // EssentialsX
        RequirementBuilder.registerRequirement("essentials geoip location", EssentialsGeoIPRequirement.class);

        //  AcidIsland
        RequirementBuilder.registerRequirement("acidisland level", AcidIslandLevelRequirement.class);

        // ASkyBlock
        RequirementBuilder.registerRequirement("askyblock level", ASkyBlockLevelRequirement.class);

        // Jobs
        RequirementBuilder.registerRequirement("jobs current points", JobsCurrentPointsRequirement.class);
        RequirementBuilder.registerRequirement("jobs total points", JobsTotalPointsRequirement.class);
        RequirementBuilder.registerRequirement("jobs level", JobsLevelRequirement.class);
        RequirementBuilder.registerRequirement("jobs experience", JobsExperienceRequirement.class);

        // GriefPrevention
        RequirementBuilder.registerRequirement("grief prevention claims",
                GriefPreventionClaimsCountRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention claimed blocks",
                GriefPreventionClaimedBlocksRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention remaining blocks",
                GriefPreventionRemainingBlocksRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention bonus blocks",
                GriefPreventionBonusBlocksRequirement.class);

        // RPGme
        RequirementBuilder.registerRequirement("rpgme skill level", RPGMeSkillLevelRequirement.class);
        RequirementBuilder.registerRequirement("rpgme combat level", RPGMeCombatLevelRequirement.class);

        // BattleLevels
        RequirementBuilder.registerRequirement("battlelevels kdr", BattleLevelsKDRRequirement.class);
        RequirementBuilder.registerRequirement("battlelevels kills", BattleLevelsKillsRequirement.class);
        RequirementBuilder.registerRequirement("battlelevels killstreak", BattleLevelsKillStreakRequirement
                .class);
        RequirementBuilder.registerRequirement("battlelevels top killstreak",
                BattleLevelsTopKillStreakRequirement
                        .class);
        RequirementBuilder.registerRequirement("battlelevels level", BattleLevelsLevelRequirement.class);
        RequirementBuilder.registerRequirement("battlelevels score", BattleLevelsScoreRequirement.class);

        // Quests (HappyPikachu)
        RequirementBuilder.registerRequirement("quests quest points", QuestsQuestPointsRequirement.class);
        RequirementBuilder.registerRequirement("quests complete quest",
                QuestsCompleteSpecificQuestRequirement.class);
        RequirementBuilder.registerRequirement("quests active quests", QuestsActiveQuestsRequirement.class);
        RequirementBuilder.registerRequirement("quests completed quests", QuestsCompletedQuestsRequirement
                .class);

        // Quests (fatpigsarefat)
        RequirementBuilder.registerRequirement("quests fatpigsarefat complete quest",
                QuestsFPAFCompleteSpecificQuestRequirement.class);
        RequirementBuilder.registerRequirement("quests fatpigsarefat completed quests",
                QuestsFPAFCompletedQuestsRequirement.class);
        RequirementBuilder.registerRequirement("quests fatpigsarefat active quests", QuestsFPAFActiveQuestsRequirement
                .class);

        // SavageFactions
        RequirementBuilder.registerRequirement("savagefactions faction power",
                SavageFactionsPowerRequirement.class);

        // PlayerPoints
        RequirementBuilder.registerRequirement("playerpoints points", PlayerPointsPointsRequirement.class);


        // Register 'main' results
        ResultBuilder.registerResult("command", CommandResult.class);
        ResultBuilder.registerResult("effect", EffectResult.class);
        ResultBuilder.registerResult("message", MessageResult.class);
        ResultBuilder.registerResult("tp", TeleportResult.class);
        ResultBuilder.registerResult("firework", SpawnFireworkResult.class);
        ResultBuilder.registerResult("money", MoneyResult.class);
    }

    /**
     * Sends a message via the debug channel of Autorank. <br>
     * It will only show up in console if the debug option in the Settings.yml
     * is turned on.
     *
     * @param message Message to send.
     */
    public void debugMessage(final String message) {
        // Don't put out debug message when it is not needed.

        // Settings file not loaded yet.
        if (this.getSettingsConfig().getConfig() == null || !this.getSettingsConfig().useDebugOutput()) {
            return;
        }

        this.getServer().getConsoleSender()
                .sendMessage("[Autorank DEBUG] " + ChatColor.translateAlternateColorCodes('&', message));
    }

    /**
     * Get the current {@linkplain StatsPlugin} that is hooked.
     *
     * @return current {@linkplain StatsPlugin} that is hooked or
     * {@linkplain FallbackHandler} if no stats plugin is found.
     */
    public StatsPlugin getHookedStatsPlugin() {
        return getDependencyManager().getStatsPlugin();
    }

    /**
     * Checks whether the current version of Autorank is a DEV version.
     *
     * @return true if is, false otherwise.
     */
    public boolean isDevVersion() {
        return this.getDescription().getVersion().toLowerCase().contains("dev")
                || this.getDescription().getVersion().toLowerCase().contains("project");
    }

    /**
     * @see me.armar.plugins.autorank.api.API#registerRequirement(String, Class)
     * registerRequirement()
     */
    public void registerRequirement(final String name, final Class<? extends AbstractRequirement> requirement) {
        RequirementBuilder.registerRequirement(name, requirement);
    }

    /**
     * @see me.armar.plugins.autorank.api.API#registerResult(String, Class)
     * registerResult()
     */
    public void registerResult(final String name, final Class<? extends AbstractResult> result) {
        ResultBuilder.registerResult(name, result);
    }

    /**
     * Reloads the Autorank plugin.
     */
    public void reload() {
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    // TODO: add requirement that checks for how many times you completed a path.

    // ---------- GETTERS & SETTERS ---------- \\
    //
    //
    //

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    private void setLanguageHandler(final LanguageHandler lHandler) {
        this.languageHandler = lHandler;
    }

    public PermissionsPluginManager getPermPlugHandler() {
        return permPlugHandler;
    }

    public void setPermPlugHandler(final PermissionsPluginManager permPlugHandler) {
        this.permPlugHandler = permPlugHandler;
    }

    public PlayerChecker getPlayerChecker() {
        return playerChecker;
    }

    private void setPlayerChecker(final PlayerChecker playerChecker) {
        this.playerChecker = playerChecker;
    }

    public PlayTimeManager getPlayTimeManager() {
        return playTimeManager;
    }

    private void setPlayTimeManager(final PlayTimeManager playTimeManager) {
        this.playTimeManager = playTimeManager;
    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    public void setUpdateHandler(final UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    public UUIDStorage getUUIDStorage() {
        return uuidStorage;
    }

    public void setUUIDStorage(final UUIDStorage uuidStorage) {
        this.uuidStorage = uuidStorage;
    }

    public ValidateHandler getValidateHandler() {
        return validateHandler;
    }

    public void setValidateHandler(final ValidateHandler validateHandler) {
        this.validateHandler = validateHandler;
    }

    public WarningManager getWarningManager() {
        return warningManager;
    }

    public void setWarningManager(final WarningManager warningManager) {
        this.warningManager = warningManager;
    }

    public AddOnManager getAddonManager() {
        return addonManager;
    }

    public void setAddonManager(final AddOnManager addonManager) {
        this.addonManager = addonManager;
    }

    public API getAPI() {
        return new API(this);
    }

    public BackupManager getBackupManager() {
        return backupManager;
    }

    public void setBackupManager(final BackupManager backupManager) {
        this.backupManager = backupManager;
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public void setCommandsManager(final CommandsManager commandsManager) {
        this.commandsManager = commandsManager;
    }

    public Debugger getDebugger() {
        return debugger;
    }

    public void setDebugger(final Debugger debugger) {
        this.debugger = debugger;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public void setDependencyManager(final DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }
    public InternalPropertiesConfig getInternalPropertiesConfig() {
        return internalPropertiesConfig;
    }

    public void setInternalPropertiesConfig(InternalPropertiesConfig internalPropertiesConfig) {
        this.internalPropertiesConfig = internalPropertiesConfig;
    }

    public SettingsConfig getSettingsConfig() {
        return settingsConfig;
    }

    public void setSettingsConfig(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    public PathsConfig getPathsConfig() {
        return pathsConfig;
    }

    public void setPathsConfig(PathsConfig pathsConfig) {
        this.pathsConfig = pathsConfig;
    }

    public PathManager getPathManager() {
        return pathManager;
    }

    public void setPathManager(PathManager pathManager) {
        this.pathManager = pathManager;
    }

    public LeaderboardHandler getLeaderboardManager() {
        return leaderboardManager;
    }

    public void setLeaderboardManager(LeaderboardHandler leaderboardManager) {
        this.leaderboardManager = leaderboardManager;
    }

    public DataConverter getDataConverter() {
        return dataConverter;
    }

    public void setDataConverter(DataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    public DefaultBehaviorConfig getDefaultBehaviorConfig() {
        return defaultBehaviorConfig;
    }

    public void setDefaultBehaviorConfig(DefaultBehaviorConfig defaultBehaviorConfig) {
        this.defaultBehaviorConfig = defaultBehaviorConfig;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public void setStorageManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
}
