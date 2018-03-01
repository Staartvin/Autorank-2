package me.armar.plugins.autorank;

import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.api.API;
import me.armar.plugins.autorank.backup.BackupManager;
import me.armar.plugins.autorank.commands.manager.CommandsManager;
import me.armar.plugins.autorank.config.*;
import me.armar.plugins.autorank.converter.DataConverter;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager;
import me.armar.plugins.autorank.data.mysql.MySQLManager;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.leaderboard.LeaderboardHandler;
import me.armar.plugins.autorank.listeners.PlayerJoinListener;
import me.armar.plugins.autorank.pathbuilder.PathManager;
import me.armar.plugins.autorank.pathbuilder.builders.RequirementBuilder;
import me.armar.plugins.autorank.pathbuilder.builders.ResultBuilder;
import me.armar.plugins.autorank.pathbuilder.requirement.*;
import me.armar.plugins.autorank.pathbuilder.result.*;
import me.armar.plugins.autorank.permissions.PermissionsPluginManager;
import me.armar.plugins.autorank.playerchecker.PlayerChecker;
import me.armar.plugins.autorank.playtimes.PlaytimeManager;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.handlers.FallbackHandler;
import me.armar.plugins.autorank.updater.UpdateHandler;
import me.armar.plugins.autorank.updater.Updater;
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

    public static Autorank getInstance() {
        return autorank;
    }

    // ---------- INITIALIZING VARIABLES ---------- \\
    //
    //
    //

    // Managers
    private PathManager pathManager;
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
    private PlaytimeManager playtimes;
    private DataConverter dataConverter;

    // Data connection
    private MySQLManager mysqlManager;
    private FlatFileManager flatFileManager;

    // UUID storage
    private UUIDStorage uuidStorage;

    // Validation & Warning
    private ValidateHandler validateHandler;
    private WarningManager warningManager;
    private Debugger debugger;

    // Configs
    private SettingsConfig settingsConfig;
    private InternalPropertiesConfig internalPropertiesConfig;
    private PathsConfig pathsConfig;
    private PlayerDataConfig playerDataConfig;
    private DefaultBehaviorConfig defaultBehaviorConfig;

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

        this.getFlatFileManager().saveFiles();

        getUUIDStorage().saveAllFiles();

        // Close database connection
        this.getMySQLManager().disconnectDatabase();

        // Save playerdata.yml
        this.getPlayerDataConfig().saveConfig();

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
        setPlayerDataConfig(new PlayerDataConfig(this));
        setDefaultBehaviorConfig(new DefaultBehaviorConfig((this)));

        // Create new configs
        this.getDefaultBehaviorConfig().loadConfig();
        this.getPathsConfig().loadConfig();
        this.getSettingsConfig().loadConfig();
        this.getInternalPropertiesConfig().loadConfig();
        this.getPlayerDataConfig().loadConfig();

        // ------------- Initialize managers -------------

        // Create backup manager
        setBackupManager(new BackupManager(this));

        // Create warning manager
        setWarningManager(new WarningManager(this));

        // Create MySQL Manager
        setMySQLManager(new MySQLManager(this));

        // Create FlatFile Manager
        setFlatFileManager(new FlatFileManager(this));

        // Load AutorankDependency manager
        setDependencyManager(new DependencyManager(this));

        // Create commands manager
        setCommandsManager(new CommandsManager(this));

        // Create Addon Manager
        setAddonManager(new AddOnManager(this));

        // Create Path Manager
        setPathManager(new PathManager(this));

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
        setPlaytimes(new PlaytimeManager(this));

        // Create player check class
        setPlayerChecker(new PlayerChecker(this));

        // Set debugger
        setDebugger(new Debugger(this));

        // Load uuids - ready for new ones
        getUUIDStorage().createNewFiles();

        // Load data converter
        setDataConverter(new DataConverter(this));

        // ------------- Create files & folders -------------

        // Setup language file
        languageHandler.createNewFile();

        // ------------- Initialize requirements and results -------------
        this.initializeReqsAndRes();

        // Start warning task if a warning has been found
        getWarningManager().startWarningTask();

        // ------------- Register listeners -------------

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // ------------- Schedule tasks -------------

        // Load all third party dependencies
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                try {
                    // Load dependencies
                    dependencyManager.loadDependencies();

                } catch (final Throwable t) {

                    // When an error occured!

                    getLogger().severe("Could not hook into a dependency: \nCause: " + t.getMessage());
                }

                // After dependencies, load paths
                // Initialize paths
                getPathManager().initialiseFromConfigs();

                // Validate paths                
                if (!getValidateHandler().startValidation()) {
                    getServer().getConsoleSender().sendMessage("[Autorank] " + ChatColor.RED + "Detected errors in your Paths.yml file. Log in to your server to see the problems!");
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

        // Remove old data of players
        getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                if (!getSettingsConfig().shouldRemoveOldEntries()) return;

                if (!getInternalPropertiesConfig().isConvertedToNewFormat()) return;

                // Remove old entries
                int removed = getFlatFileManager().removeOldEntries();

                getLogger().info("Removed " + removed + " old data entries from database!");
            }
        }, 0, (long) AutorankTools.TICKS_PER_MINUTE * 60 * 24);

        // ------------- Register commands -------------

        // Register command
        getCommand("autorank").setExecutor(getCommandsManager());

        // ------------- Log messages -------------

        // Debug message telling what plugin is used for timing.
        getLogger().info("Using timings of: " + getConfigHandler().useTimeOf().toString().toLowerCase());

        debugMessage("Autorank debug is turned on!");

        // ------------- Check version -------------

        // Extra warning for dev users
        if (isDevVersion()) {
            this.getLogger().warning("You're running a DEV version, be sure to backup your Autorank folder!");
            this.getLogger().warning(
                    "DEV versions are not guaranteed to be stable and generally shouldn't be used on big production servers with lots of players.");
        }

        // ------------- Do miscalleaneous tasks -------------

        // Start automatic backup
        this.getBackupManager().startBackupSystem();

        // Try to update all leaderboards if needed.
        this.getLeaderboardManager().updateAllLeaderboards();

        // Convert all UUIDS to lowercase.
        this.getUUIDStorage().transferUUIDs();

        // Check whether the data files are still up to date.
        this.getFlatFileManager().doCalendarCheck();

        // Spawn thread to check if MySQL database times are up to date
        this.getMySQLManager().refreshGlobalTime();

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
        RequirementBuilder.registerRequirement("exp", ExpRequirement.class);
        RequirementBuilder.registerRequirement("money", MoneyRequirement.class);
        RequirementBuilder.registerRequirement("gamemode", GamemodeRequirement.class);
        RequirementBuilder.registerRequirement("has item", HasItemRequirement.class);
        RequirementBuilder.registerRequirement("blocks broken", BlocksBrokenRequirement.class);
        RequirementBuilder.registerRequirement("blocks placed", BlocksPlacedRequirement.class);
        RequirementBuilder.registerRequirement("blocks moved", BlocksMovedRequirement.class);
        RequirementBuilder.registerRequirement("votes", TotalVotesRequirement.class);
        RequirementBuilder.registerRequirement("damage taken", DamageTakenRequirement.class);
        RequirementBuilder.registerRequirement("mobs killed", MobKillsRequirement.class);
        RequirementBuilder.registerRequirement("location", LocationRequirement.class);
        RequirementBuilder.registerRequirement("faction power", FactionPowerRequirement.class);
        RequirementBuilder.registerRequirement("players killed", PlayerKillsRequirement.class);
        RequirementBuilder.registerRequirement("global time", GlobalTimeRequirement.class);
        RequirementBuilder.registerRequirement("total time", TotalTimeRequirement.class);
        RequirementBuilder.registerRequirement("world", WorldRequirement.class);
        RequirementBuilder.registerRequirement("worldguard region", WorldGuardRegionRequirement.class);
        RequirementBuilder.registerRequirement("mcmmo skill level", McMMOSkillLevelRequirement.class);
        RequirementBuilder.registerRequirement("mcmmo power level", McMMOPowerLevelRequirement.class);
        RequirementBuilder.registerRequirement("permission", PermissionRequirement.class);
        RequirementBuilder.registerRequirement("fish caught", FishCaughtRequirement.class);
        RequirementBuilder.registerRequirement("items crafted", ItemsCraftedRequirement.class);
        RequirementBuilder.registerRequirement("time", TimeRequirement.class);
        RequirementBuilder.registerRequirement("times sheared", TimesShearedRequirement.class);
        RequirementBuilder.registerRequirement("in biome", InBiomeRequirement.class);
        RequirementBuilder.registerRequirement("food eaten", FoodEatenRequirement.class);
        RequirementBuilder.registerRequirement("has advancement", AdvancementRequirement.class);
        RequirementBuilder.registerRequirement("in group", GroupRequirement.class);

        RequirementBuilder.registerRequirement("essentials geoip location", EssentialsGeoIPRequirement.class);

        RequirementBuilder.registerRequirement("acidisland level", AcidIslandLevelRequirement.class);
        RequirementBuilder.registerRequirement("askyblock level", ASkyBlockLevelRequirement.class);

        RequirementBuilder.registerRequirement("javascript", JavaScriptRequirement.class);

        RequirementBuilder.registerRequirement("jobs current points", JobsCurrentPointsRequirement.class);
        RequirementBuilder.registerRequirement("jobs total points", JobsTotalPointsRequirement.class);
        RequirementBuilder.registerRequirement("jobs level", JobsLevelRequirement.class);
        RequirementBuilder.registerRequirement("jobs experience", JobsExperienceRequirement.class);

        RequirementBuilder.registerRequirement("grief prevention claims", GriefPreventionClaimsCountRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention claimed blocks", GriefPreventionClaimedBlocksRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention remaining blocks", GriefPreventionRemainingBlocksRequirement.class);
        RequirementBuilder.registerRequirement("grief prevention bonus blocks", GriefPreventionBonusBlocksRequirement.class);

        RequirementBuilder.registerRequirement("rpgme skill level", RPGMeSkillLevelRequirement.class);
        RequirementBuilder.registerRequirement("rpgme combat level", RPGMeCombatLevelRequirement.class);

        RequirementBuilder.registerRequirement("battlelevels kdr", BattleLevelsKDRRequirement.class);
        RequirementBuilder.registerRequirement("battlelevels kills", BattleLevelsKillsRequirement.class);
        RequirementBuilder.registerRequirement("battlelevels killstreak", BattleLevelsKillStreakRequirement.class);
        RequirementBuilder.registerRequirement("battlelevels top killstreak", BattleLevelsTopKillStreakRequirement
                .class);
        RequirementBuilder.registerRequirement("battlelevels level", BattleLevelsLevelRequirement.class);
        RequirementBuilder.registerRequirement("battlelevels score", BattleLevelsScoreRequirement.class);

        RequirementBuilder.registerRequirement("quests quest points", QuestsQuestPointsRequirement.class);
        RequirementBuilder.registerRequirement("quests complete quest", QuestsCompleteSpecificQuestRequirement.class);
        RequirementBuilder.registerRequirement("quests active quests", QuestsActiveQuestsRequirement.class);
        RequirementBuilder.registerRequirement("quests completed quests", QuestsCompletedQuestsRequirement.class);

        // Register 'main' results
        ResultBuilder.registerResult("command", CommandResult.class);
        ResultBuilder.registerResult("effect", EffectResult.class);
        ResultBuilder.registerResult("message", MessageResult.class);
        //res.registerResult("rank change", RankChangeResult.class); -- Temporarily disabled until fixed
        ResultBuilder.registerResult("tp", TeleportResult.class);
        ResultBuilder.registerResult("firework", SpawnFireworkResult.class);
        ResultBuilder.registerResult("money", MoneyResult.class);
    }

    /**
     * This method can only be performed from the main class as it tries to do
     * {@link #getFile()}
     *
     * @return Whether an update is available
     */
    public boolean checkForUpdate() {

        // We are not allowed to check for new versions.
        if (!updateHandler.doCheckForNewVersion())
            return false;

        final Updater updater = new Updater(this, 34447, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
        updateHandler.setUpdater(updater);

        return (updater.getResult().equals(Updater.UpdateResult.UPDATE_AVAILABLE));

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
        if (!this.getConfigHandler().useDebugOutput())
            return;

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
    public void registerRequirement(final String name, final Class<? extends Requirement> requirement) {
        RequirementBuilder.registerRequirement(name, requirement);
    }

    /**
     * @see me.armar.plugins.autorank.api.API#registerResult(String, Class)
     * registerResult()
     */
    public void registerResult(final String name, final Class<? extends Result> result) {
        ResultBuilder.registerResult(name, result);
    }

    /**
     * Reloads the Autorank plugin.
     */
    public void reload() {
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    // ---------- GETTERS & SETTERS ---------- \\
    //
    //
    //

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public PermissionsPluginManager getPermPlugHandler() {
        return permPlugHandler;
    }

    public PlayerChecker getPlayerChecker() {
        return playerChecker;
    }

    public PlaytimeManager getPlaytimes() {
        return playtimes;
    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    public UUIDStorage getUUIDStorage() {
        return uuidStorage;
    }

    public ValidateHandler getValidateHandler() {
        return validateHandler;
    }

    public WarningManager getWarningManager() {
        return warningManager;
    }

    public AddOnManager getAddonManager() {
        return addonManager;
    }

    public API getAPI() {
        return new API(this);
    }

    public BackupManager getBackupManager() {
        return backupManager;
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public SettingsConfig getConfigHandler() {
        return settingsConfig;
    }

    public Debugger getDebugger() {
        return debugger;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public void setAddonManager(final AddOnManager addonManager) {
        this.addonManager = addonManager;
    }

    public void setBackupManager(final BackupManager backupManager) {
        this.backupManager = backupManager;
    }

    public void setCommandsManager(final CommandsManager commandsManager) {
        this.commandsManager = commandsManager;
    }

    public void setConfigHandler(final SettingsConfig configHandler) {
        this.settingsConfig = configHandler;
    }

    public void setDebugger(final Debugger debugger) {
        this.debugger = debugger;
    }

    public void setDependencyManager(final DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    private void setLanguageHandler(final LanguageHandler lHandler) {
        this.languageHandler = lHandler;
    }

    /**
     * @return the internalPropertiesConfig
     */
    public InternalPropertiesConfig getInternalPropertiesConfig() {
        return internalPropertiesConfig;
    }

    /**
     * @param internalPropertiesConfig the internalPropertiesConfig to set
     */
    public void setInternalPropertiesConfig(InternalPropertiesConfig internalPropertiesConfig) {
        this.internalPropertiesConfig = internalPropertiesConfig;
    }

    public void setPermPlugHandler(final PermissionsPluginManager permPlugHandler) {
        this.permPlugHandler = permPlugHandler;
    }

    private void setPlayerChecker(final PlayerChecker playerChecker) {
        this.playerChecker = playerChecker;
    }

    private void setPlaytimes(final PlaytimeManager playtimes) {
        this.playtimes = playtimes;
    }

    public void setUpdateHandler(final UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    public void setUUIDStorage(final UUIDStorage uuidStorage) {
        this.uuidStorage = uuidStorage;
    }

    public void setValidateHandler(final ValidateHandler validateHandler) {
        this.validateHandler = validateHandler;
    }

    public void setWarningManager(final WarningManager warningManager) {
        this.warningManager = warningManager;
    }

    /**
     * @return the settingsConfig
     */
    public SettingsConfig getSettingsConfig() {
        return settingsConfig;
    }

    /**
     * @param settingsConfig the settingsConfig to set
     */
    public void setSettingsConfig(SettingsConfig settingsConfig) {
        this.settingsConfig = settingsConfig;
    }

    public PlayerDataConfig getPlayerDataConfig() {
        return playerDataConfig;
    }

    public void setPlayerDataConfig(PlayerDataConfig playerDataConfig) {
        this.playerDataConfig = playerDataConfig;
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

    public MySQLManager getMySQLManager() {
        return mysqlManager;
    }

    public void setMySQLManager(MySQLManager mysqlManager) {
        this.mysqlManager = mysqlManager;
    }

    public FlatFileManager getFlatFileManager() {
        return flatFileManager;
    }

    public void setFlatFileManager(FlatFileManager flatFileManager) {
        this.flatFileManager = flatFileManager;
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
}
