package me.armar.plugins.autorank;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.api.API;
import me.armar.plugins.autorank.backup.BackupManager;
import me.armar.plugins.autorank.commands.manager.CommandsManager;
import me.armar.plugins.autorank.config.ConfigHandler;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.hooks.DependencyManager;
import me.armar.plugins.autorank.internalproperties.InternalProperties;
import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.leaderboard.Leaderboard;
import me.armar.plugins.autorank.listeners.PlayerJoinListener;
import me.armar.plugins.autorank.metrics.Metrics;
import me.armar.plugins.autorank.metrics.Metrics.Graph;
import me.armar.plugins.autorank.mysql.wrapper.MySQLWrapper;
import me.armar.plugins.autorank.permissions.PermissionsPluginManager;
import me.armar.plugins.autorank.playerchecker.PlayerChecker;
import me.armar.plugins.autorank.playerchecker.requirement.ASkyBlockLevelRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.AchievementRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.AcidIslandLevelRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.BlocksBrokenRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.BlocksMovedRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.BlocksPlacedRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.DamageTakenRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.EssentialsGeoIPRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.ExpRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.FactionPowerRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.FishCaughtRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.FoodEatenRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.GamemodeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.GlobalTimeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.GriefPreventionBonusBlocksRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.GriefPreventionClaimedBlocksRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.GriefPreventionClaimsCountRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.GriefPreventionRemainingBlocksRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.HasItemRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.InBiomeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.ItemsCraftedRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.JavaScriptRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.JobsCurrentPointsRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.JobsExperienceRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.JobsLevelRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.JobsTotalPointsRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.LocationRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.McMMOPowerLevelRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.McMMOSkillLevelRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.MobKillsRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.MoneyRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.PermissionRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.PlayerKillsRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.RPGMeCombatLevelRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.RPGMeSkillLevelRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.requirement.TimeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.TimesShearedRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.TotalTimeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.TotalVotesRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.WorldGuardRegionRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.WorldRequirement;
import me.armar.plugins.autorank.playerchecker.result.CommandResult;
import me.armar.plugins.autorank.playerchecker.result.EffectResult;
import me.armar.plugins.autorank.playerchecker.result.MessageResult;
import me.armar.plugins.autorank.playerchecker.result.RankChangeResult;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.playerchecker.result.SpawnFireworkResult;
import me.armar.plugins.autorank.playerchecker.result.TeleportResult;
import me.armar.plugins.autorank.playerdatahandler.PlayerDataHandler;
import me.armar.plugins.autorank.playtimes.Playtimes;
import me.armar.plugins.autorank.rankbuilder.builders.RequirementBuilder;
import me.armar.plugins.autorank.rankbuilder.builders.ResultBuilder;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.handlers.DummyHandler;
import me.armar.plugins.autorank.updater.UpdateHandler;
import me.armar.plugins.autorank.updater.Updater;
import me.armar.plugins.autorank.util.uuid.storage.UUIDStorage;
import me.armar.plugins.autorank.validations.ValidateHandler;
import me.armar.plugins.autorank.warningmanager.WarningManager;

/**
 * 
 * Main class of Autorank
 * <p>
 * Date created: 18:34:00 13 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class Autorank extends JavaPlugin {

	private AddOnManager addonManager;
	private SimpleYamlConfiguration advancedConfig;
	private CommandsManager commandsManager;
	private ConfigHandler configHandler;
	private Debugger debugger;
	private DependencyManager dependencyManager;
	private LanguageHandler languageHandler;
	private Leaderboard leaderboard;
	// Metrics (for custom data)
	private me.armar.plugins.autorank.metrics.Metrics metrics;
	private MySQLWrapper mysqlWrapper;
	private PermissionsPluginManager permPlugHandler;
	private PlayerChecker playerChecker;
	private Playtimes playtimes;
	private PlayerDataHandler playerDataHandler;
	private SimpleYamlConfiguration settingsConfig;
	private SimpleYamlConfiguration simpleConfig;
	private InternalProperties internalProps;
	private UUIDStorage uuidStorage;

	private BackupManager backupManager;

	private UpdateHandler updateHandler;

	private ValidateHandler validateHandler;

	private WarningManager warningManager;

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
	 * Sends a message via the debug channel of Autorank.
	 * <br>
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

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() {

		// Make sure all tasks are cancelled after shutdown. This seems obvious,
		// but when a player /reloads, the server creates an instance of the
		// plugin which causes duplicate tasks to run.
		getServer().getScheduler().cancelTasks(this);

		playtimes.save();

		getUUIDStorage().saveAllFiles();

		// Close database connection
		this.getMySQLWrapper().disconnectDatabase();

		// Save playerdata.yml
		this.getPlayerDataHandler().saveConfig();

		getLogger().info(String.format("Autorank %s has been disabled!", getDescription().getVersion()));
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() {

		// Register configs
		setSimpleConfig(new SimpleYamlConfiguration(this, "SimpleConfig.yml", null, "Simple config"));
		setAdvancedConfig(new SimpleYamlConfiguration(this, "AdvancedConfig.yml", null, "Advanced config"));
		setSettingsConfig(new SimpleYamlConfiguration(this, "Settings.yml", null, "Settings config"));

		setInternalProps(new InternalProperties(this));

		// Create config handler
		setConfigHandler(new ConfigHandler(this));

		// Create internal properties file
		this.getInternalProps().loadFile();

		// Create backup manager
		setBackupManager(new BackupManager(this));

		// Create uuid storage
		setUUIDStorage(new UUIDStorage(this));

		// Load uuids - ready for new ones
		getUUIDStorage().createNewFiles();

		// Create warning manager
		setWarningManager(new WarningManager(this));

		// Create requirement handler
		setPlayerDataHandler(new PlayerDataHandler(this));

		// Create files
		playerDataHandler.createNewFile();

		// Create update handler
		setUpdateHandler(new UpdateHandler(this));

		// Register listeners
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

		// Create language classes
		setLanguageHandler(new LanguageHandler(this));

		// Create MySQL Wrapper
		setMySQLWrapper(new MySQLWrapper(this));

		// Load dependency manager
		setDependencyManager(new DependencyManager(this));

		try {
			// Load dependencies
			dependencyManager.loadDependencies();
		} catch (final Throwable t) {

			// When an error occured!

			getLogger().severe("Could not hook into a dependency: \nCause: " + t.getCause());
		}

		// Create playtime class
		setPlaytimes(new Playtimes(this));

		// Refresh player data - store it so we can cancel it later
		// uuidRefresherTask =
		// this.getServer().getScheduler().runTaskAsynchronously(this, new
		// UUIDRefresher(this));

		// Convert data folder
		// playtimes.convertToUUIDStorage();

		// Create permission plugin handler class
		setPermPlugHandler(new PermissionsPluginManager(this));

		// Create player check class
		setPlayerChecker(new PlayerChecker(this));

		// Create validate handler
		setValidateHandler(new ValidateHandler(this));

		// Create leaderboard class
		setLeaderboard(new Leaderboard(this));

		// Create commands manager
		setCommandsManager(new CommandsManager(this));

		final RequirementBuilder req = this.getPlayerChecker().getChangeGroupManager().getBuilder()
				.getRequirementBuilder();
		final ResultBuilder res = this.getPlayerChecker().getChangeGroupManager().getBuilder().getResultBuilder();

		// Register 'main' requirements
		req.registerRequirement("exp", ExpRequirement.class);
		req.registerRequirement("money", MoneyRequirement.class);
		req.registerRequirement("gamemode", GamemodeRequirement.class);
		req.registerRequirement("has item", HasItemRequirement.class);
		req.registerRequirement("blocks broken", BlocksBrokenRequirement.class);
		req.registerRequirement("blocks placed", BlocksPlacedRequirement.class);
		req.registerRequirement("blocks moved", BlocksMovedRequirement.class);
		req.registerRequirement("votes", TotalVotesRequirement.class);
		req.registerRequirement("damage taken", DamageTakenRequirement.class);
		req.registerRequirement("mobs killed", MobKillsRequirement.class);
		req.registerRequirement("location", LocationRequirement.class);
		req.registerRequirement("faction power", FactionPowerRequirement.class);
		req.registerRequirement("players killed", PlayerKillsRequirement.class);
		req.registerRequirement("global time", GlobalTimeRequirement.class);
		req.registerRequirement("total time", TotalTimeRequirement.class);
		req.registerRequirement("world", WorldRequirement.class);
		req.registerRequirement("worldguard region", WorldGuardRegionRequirement.class);
		req.registerRequirement("mcmmo skill level", McMMOSkillLevelRequirement.class);
		req.registerRequirement("mcmmo power level", McMMOPowerLevelRequirement.class);
		req.registerRequirement("permission", PermissionRequirement.class);
		req.registerRequirement("fish caught", FishCaughtRequirement.class);
		req.registerRequirement("items crafted", ItemsCraftedRequirement.class);
		req.registerRequirement("time", TimeRequirement.class);
		req.registerRequirement("times sheared", TimesShearedRequirement.class);
		req.registerRequirement("essentials geoip location", EssentialsGeoIPRequirement.class);
		req.registerRequirement("in biome", InBiomeRequirement.class);
		req.registerRequirement("food eaten", FoodEatenRequirement.class);
		req.registerRequirement("acidisland level", AcidIslandLevelRequirement.class);
		req.registerRequirement("askyblock level", ASkyBlockLevelRequirement.class);
		req.registerRequirement("javascript", JavaScriptRequirement.class);
		req.registerRequirement("jobs current points", JobsCurrentPointsRequirement.class);
		req.registerRequirement("jobs total points", JobsTotalPointsRequirement.class);
		req.registerRequirement("jobs level", JobsLevelRequirement.class);
		req.registerRequirement("jobs experience", JobsExperienceRequirement.class);
		req.registerRequirement("grief prevention claims", GriefPreventionClaimsCountRequirement.class);
		req.registerRequirement("grief prevention claimed blocks", GriefPreventionClaimedBlocksRequirement.class);
		req.registerRequirement("grief prevention remaining blocks", GriefPreventionRemainingBlocksRequirement.class);
		req.registerRequirement("grief prevention bonus blocks", GriefPreventionBonusBlocksRequirement.class);
		req.registerRequirement("has achievement", AchievementRequirement.class);
		req.registerRequirement("rpgme skill level", RPGMeSkillLevelRequirement.class);
		req.registerRequirement("rpgme combat level", RPGMeCombatLevelRequirement.class);

		// Register 'main' results
		res.registerResult("command", CommandResult.class);
		res.registerResult("effect", EffectResult.class);
		res.registerResult("message", MessageResult.class);
		res.registerResult("rank change", RankChangeResult.class);
		res.registerResult("tp", TeleportResult.class);
		res.registerResult("firework", SpawnFireworkResult.class);

		// Load requirements and results per group from config
		// playerChecker.getChangeGroupManager().initialiseFromConfigs();

		// Load again after 5 seconds so custom commands can be listed
		getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
			@Override
			public void run() {

				getPlayerChecker().getChangeGroupManager().initialiseFromConfigs();

				// Validate configs after that
				if (configHandler.useAdvancedConfig()) {
					getValidateHandler().validateConfigGroups(getAdvancedConfig());
				} else {
					getValidateHandler().validateConfigGroups(getSimpleConfig());
				}

				// Start warning task if a warning has been found
				if (getWarningManager().getHighestWarning() != null) {
					getWarningManager().startWarningTask();
				}
			}
		}, 100);

		// Register command
		getCommand("autorank").setExecutor(getCommandsManager());

		// Setup language file
		languageHandler.createNewFile();

		// Set debugger
		setDebugger(new Debugger(this));

		// Debug message telling what plugin is used for timing.
		getLogger().info("Using timings of: " + getConfigHandler().useTimeOf().toString().toLowerCase());

		// Note that custom requirements and results are not yet loaded into
		// memory.
		// TODO Add support for custom requirements and results.
		setAddonManager(new AddOnManager(this));

		getLogger().info(String.format("Autorank %s has been enabled!", getDescription().getVersion()));

		// Start collecting data
		if (!startMetrics()) {
			getLogger().info("Failed to start Metrics, you can ignore this message.");
		}

		debugMessage("Autorank debug is turned on!");

		// Extra warning for dev users
		if (isDevVersion()) {
			this.getLogger().warning("You're running a DEV version, be sure to backup your Autorank folder!");
			this.getLogger().warning(
					"DEV versions are not guaranteed to be stable and generally shouldn't be used on big production servers with lots of players.");
		}

		// Start automatic backup
		this.getBackupManager().startBackupSystem();

		// Try to update all leaderboards if needed.
		this.getLeaderboard().updateAllLeaderboards();

		// Convert all UUIDS to lowercase.
		this.getUUIDStorage().transferUUIDs();

		// Check whether the data files are still up to date.
		this.getPlaytimes().doCalendarCheck();

		// Spawn thread to check if MySQL database times are up to date
		this.getMySQLWrapper().refreshGlobalTime();
	}

	public AddOnManager getAddonManager() {
		return addonManager;
	}

	public SimpleYamlConfiguration getAdvancedConfig() {
		return advancedConfig;
	}

	public API getAPI() {
		return new API(this);
	}

	public CommandsManager getCommandsManager() {
		return commandsManager;
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public Debugger getDebugger() {
		return debugger;
	}

	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}

	/**
	 * Get the current {@linkplain StatsPlugin} that is hooked.
	 * 
	 * @return current {@linkplain StatsPlugin} that is hooked or
	 *         {@linkplain DummyHandler} if no stats plugin is found.
	 */
	public StatsPlugin getHookedStatsPlugin() {
		return getDependencyManager().getStatsPlugin();
	}

	public LanguageHandler getLanguageHandler() {
		return languageHandler;
	}

	public Leaderboard getLeaderboard() {
		return leaderboard;
	}

	public MySQLWrapper getMySQLWrapper() {
		return mysqlWrapper;
	}

	public PermissionsPluginManager getPermPlugHandler() {
		return permPlugHandler;
	}

	public PlayerChecker getPlayerChecker() {
		return playerChecker;
	}

	public Playtimes getPlaytimes() {
		return playtimes;
	}

	public PlayerDataHandler getPlayerDataHandler() {
		return playerDataHandler;
	}

	public SimpleYamlConfiguration getSettingsConfig() {
		return settingsConfig;
	}

	public SimpleYamlConfiguration getSimpleConfig() {
		return simpleConfig;
	}

	public UpdateHandler getUpdateHandler() {
		return updateHandler;
	}

	public ValidateHandler getValidateHandler() {
		return validateHandler;
	}

	public WarningManager getWarningManager() {
		return warningManager;
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
	 * @see {@linkplain me.armar.plugins.autorank.api.API#registerRequirement(String, Class)
	 *      registerRequirement()}
	 */
	public void registerRequirement(final String name, final Class<? extends Requirement> requirement) {
		this.getPlayerChecker().getChangeGroupManager().getBuilder().getRequirementBuilder().registerRequirement(name,
				requirement);
	}

	/**
	 * @see {@linkplain me.armar.plugins.autorank.api.API#registerResult(String, Class)
	 *      registerResult()}
	 */
	public void registerResult(final String name, final Class<? extends Result> result) {
		this.getPlayerChecker().getChangeGroupManager().getBuilder().getResultBuilder().registerResult(name, result);
	}

	/**
	 * Reloads the Autorank plugin.
	 */
	public void reload() {
		getServer().getPluginManager().disablePlugin(this);
		getServer().getPluginManager().enablePlugin(this);
	}

	public void setAddonManager(final AddOnManager addonManager) {
		this.addonManager = addonManager;
	}

	private void setAdvancedConfig(final SimpleYamlConfiguration advancedConfig) {
		this.advancedConfig = advancedConfig;
	}

	public void setCommandsManager(final CommandsManager commandsManager) {
		this.commandsManager = commandsManager;
	}

	public void setConfigHandler(final ConfigHandler configHandler) {
		this.configHandler = configHandler;
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

	private void setLeaderboard(final Leaderboard leaderboard) {
		this.leaderboard = leaderboard;
	}

	public void setMySQLWrapper(final MySQLWrapper mysqlWrapper) {
		this.mysqlWrapper = mysqlWrapper;
	}

	public void setPermPlugHandler(final PermissionsPluginManager permPlugHandler) {
		this.permPlugHandler = permPlugHandler;
	}

	private void setPlayerChecker(final PlayerChecker playerChecker) {
		this.playerChecker = playerChecker;
	}

	private void setPlaytimes(final Playtimes playtimes) {
		this.playtimes = playtimes;
	}

	public void setPlayerDataHandler(final PlayerDataHandler requirementHandler) {
		this.playerDataHandler = requirementHandler;
	}

	public void setSettingsConfig(final SimpleYamlConfiguration settingsConfig) {
		this.settingsConfig = settingsConfig;
	}

	private void setSimpleConfig(final SimpleYamlConfiguration simpleConfig) {
		this.simpleConfig = simpleConfig;
	}

	public void setUpdateHandler(final UpdateHandler updateHandler) {
		this.updateHandler = updateHandler;
	}

	public void setValidateHandler(final ValidateHandler validateHandler) {
		this.validateHandler = validateHandler;
	}

	public void setWarningManager(final WarningManager warningManager) {
		this.warningManager = warningManager;
	}

	/**
	 * Starts the metrics of Autorank.
	 * 
	 * @return true if succesfully started, false otherwise.
	 */
	private boolean startMetrics() {
		// Try to start metrics
		try {
			// Initialise
			metrics = new me.armar.plugins.autorank.metrics.Metrics(this);

			// Setup graph for MySQL
			final Graph mysqlUsedGraph = metrics.createGraph("Using MySQL");

			mysqlUsedGraph.addPlotter(new Metrics.Plotter("MySQL") {

				@Override
				public int getValue() {
					return getConfigHandler().useMySQL() ? 1 : 0;
				}

			});

			mysqlUsedGraph.addPlotter(new Metrics.Plotter("No MySQL") {

				@Override
				public int getValue() {
					if (!getConfigHandler().useMySQL())
						return 1;
					else
						return 0;
				}

			});

			metrics.start();
			return true;
		} catch (final IOException e) {
			// Failed to submit the stats :-(
			return false;
		}
	}

	public UUIDStorage getUUIDStorage() {
		return uuidStorage;
	}

	public void setUUIDStorage(final UUIDStorage uuidStorage) {
		this.uuidStorage = uuidStorage;
	}

	public BackupManager getBackupManager() {
		return backupManager;
	}

	public void setBackupManager(final BackupManager backupManager) {
		this.backupManager = backupManager;
	}

	public InternalProperties getInternalProps() {
		return internalProps;
	}

	public void setInternalProps(final InternalProperties internalProps) {
		this.internalProps = internalProps;
	}
}
