package me.armar.plugins.autorank;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import me.armar.plugins.autorank.addons.AddOnManager;
import me.armar.plugins.autorank.api.API;
import me.armar.plugins.autorank.commands.manager.CommandsManager;
import me.armar.plugins.autorank.config.ConfigHandler;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.factionapi.FactionsHandler;
import me.armar.plugins.autorank.hooks.worldguardapi.WorldGuardAPIHandler;
import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.leaderboard.Leaderboard;
import me.armar.plugins.autorank.listeners.PlayerJoinListener;
import me.armar.plugins.autorank.metrics.Metrics;
import me.armar.plugins.autorank.metrics.Metrics.Graph;
import me.armar.plugins.autorank.mysql.wrapper.MySQLWrapper;
import me.armar.plugins.autorank.permissions.PermissionsPluginManager;
import me.armar.plugins.autorank.playerchecker.PlayerChecker;
import me.armar.plugins.autorank.playerchecker.builders.RequirementBuilder;
import me.armar.plugins.autorank.playerchecker.builders.ResultBuilder;
import me.armar.plugins.autorank.playerchecker.requirement.BlocksBrokenRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.BlocksMovedRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.BlocksPlacedRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.DamageTakenRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.ExpRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.FactionPowerRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.GamemodeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.GlobalTimeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.HasItemRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.LocationRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.MobKillsRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.MoneyRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.PlayerKillsRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.requirement.TimeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.TotalTimeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.TotalVotesRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.WorldRequirement;
import me.armar.plugins.autorank.playerchecker.result.CommandResult;
import me.armar.plugins.autorank.playerchecker.result.EffectResult;
import me.armar.plugins.autorank.playerchecker.result.MessageResult;
import me.armar.plugins.autorank.playerchecker.result.RankChangeResult;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.playerchecker.result.TeleportResult;
import me.armar.plugins.autorank.playtimes.Playtimes;
import me.armar.plugins.autorank.requirementhandler.RequirementHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.statsmanager.StatsPluginManager;
import me.armar.plugins.autorank.statsmanager.handlers.DummyHandler;
import me.armar.plugins.autorank.updater.UpdateHandler;
import me.armar.plugins.autorank.updater.Updater;
import me.armar.plugins.autorank.validations.ValidateHandler;
import me.armar.plugins.autorank.warningmanager.WarningManager;
import me.armar.plugins.autorank.warningmanager.WarningNoticeTask;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Date created: 18:34:00
 * 13 jan. 2014
 * 
 * @author Staartvin
 * 
 */
public class Autorank extends JavaPlugin {

	private Leaderboard leaderboard;
	private Playtimes playtimes;
	private PlayerChecker playerChecker;
	private SimpleYamlConfiguration simpleConfig;
	private SimpleYamlConfiguration advancedConfig;
	private PermissionsPluginManager permPlugHandler;
	private LanguageHandler languageHandler;
	private ValidateHandler validateHandler;
	private MySQLWrapper mysqlWrapper;
	private static Logger log = Bukkit.getLogger();
	private UpdateHandler updateHandler;
	private ConfigHandler configHandler;
	private RequirementHandler requirementHandler;
	private Debugger debugger;
	private FactionsHandler factionsHandler;
	private WarningManager warningManager;
	private CommandsManager commandsManager;
	private StatsPluginManager statsPluginManager;
	private AddOnManager addonManager;
	private WorldGuardAPIHandler worldGuardAPIHandler;

	// Metrics (for custom data)
	private me.armar.plugins.autorank.metrics.Metrics metrics;
	
	// Using MySQL
	public static boolean usingMySQL = false; 

	@Override
	public void onEnable() {

		// TODO: Add our own Stats logger which keeps track of (a lot of) things

		// Register configs
		setSimpleConfig(new SimpleYamlConfiguration(this, "SimpleConfig.yml",
				null, "Simple config"));
		setAdvancedConfig(new SimpleYamlConfiguration(this,
				"AdvancedConfig.yml", null, "Advanced config"));

		// Create warning manager
		setWarningManager(new WarningManager());

		// Create requirement handler
		setRequirementHandler(new RequirementHandler(this));

		// Create files
		requirementHandler.createNewFile();

		// Create config handler
		setConfigHandler(new ConfigHandler(this));

		// Create update handler
		setUpdateHandler(new UpdateHandler(this));

		// Register listeners
		getServer().getPluginManager().registerEvents(
				new PlayerJoinListener(this), this);

		// Create language classes
		setLanguageHandler(new LanguageHandler(this));

		// Create MySQL Wrapper
		setMySQLWrapper(new MySQLWrapper(this));

		// Create playtime class
		setPlaytimes(new Playtimes(this));

		// Create leaderboard class
		setLeaderboard(new Leaderboard(this));

		// Create permission plugin handler class
		setPermPlugHandler(new PermissionsPluginManager(this));

		// Create player check class
		setPlayerChecker(new PlayerChecker(this));

		// Create validate handler
		setValidateHandler(new ValidateHandler(this));

		// Create stats plugin handler
		setStatsPluginManager(new StatsPluginManager(this));

		// Create faction handler
		setFactionsHandler(new FactionsHandler(this));
		
		// Create WorldGuard handler
		setWorldGuardAPIHandler(new WorldGuardAPIHandler(this));

		// Create commands manager
		setCommandsManager(new CommandsManager(this));

		// Check if we found a stats plugin
		if (statsPluginManager.getStatsPlugin() != null
				&& !statsPluginManager.getStatsPlugin().getClass()
						.equals(DummyHandler.class)) {
			String statsPluginName = "none";

			if (statsPluginManager.findStats()) {
				statsPluginName = "Stats (by Lolmewn)";
			}

			getLogger().info("Found Stats plugin: " + statsPluginName);
		} else {
			getLogger().severe("No Stats plugin found!");
		}

		// Setup Factions
		if (factionsHandler.setupFactions()) {
			getLogger().info(
					"Hooked into Factions! Faction requirements can be used.");
		}
		
		// Setup WorldGuard
		worldGuardAPIHandler.setupWorldGuard();

		final RequirementBuilder req = this.getPlayerChecker().getBuilder()
				.getRequirementBuilder();
		final ResultBuilder res = this.getPlayerChecker().getBuilder()
				.getResultBuilder();

		// Register 'main' requirements
		req.registerRequirement("exp", ExpRequirement.class);
		req.registerRequirement("money", MoneyRequirement.class);
		req.registerRequirement("gamemode", GamemodeRequirement.class);
		req.registerRequirement("has item", HasItemRequirement.class);
		req.registerRequirement("world", WorldRequirement.class);
		req.registerRequirement("blocks broken", BlocksBrokenRequirement.class);
		req.registerRequirement("blocks placed", BlocksPlacedRequirement.class);
		req.registerRequirement("votes", TotalVotesRequirement.class);
		req.registerRequirement("damage taken", DamageTakenRequirement.class);
		req.registerRequirement("mobs killed", MobKillsRequirement.class);
		req.registerRequirement("location", LocationRequirement.class);
		req.registerRequirement("faction power", FactionPowerRequirement.class);
		req.registerRequirement("players killed", PlayerKillsRequirement.class);
		req.registerRequirement("global time", GlobalTimeRequirement.class);
		req.registerRequirement("total time", TotalTimeRequirement.class);
		req.registerRequirement("time", TimeRequirement.class);
		req.registerRequirement("blocks moved", BlocksMovedRequirement.class);

		// REGISTER PLURALS IN AUTORANKTOOLS AS WELL!

		// Register 'main' results
		res.registerResult("command", CommandResult.class);
		res.registerResult("effect", EffectResult.class);
		res.registerResult("message", MessageResult.class);
		res.registerResult("rank change", RankChangeResult.class);
		res.registerResult("tp", TeleportResult.class);

		// Load requirements and results per group from config
		playerChecker.initialiseFromConfigs(this);

		// Register command
		getCommand("autorank").setExecutor(getCommandsManager());

		// Validate config files
		if (configHandler.useAdvancedConfig()) {
			getValidateHandler().validateConfigGroups(getAdvancedConfig());
		} else {
			getValidateHandler().validateConfigGroups(getSimpleConfig());
		}

		// Setup language file
		languageHandler.createNewFile();

		// Set debugger
		setDebugger(new Debugger(this));
		
		// Set addon manager
		setAddonManager(new AddOnManager(this));

		Autorank.logMessage(String.format("Autorank %s has been enabled!",
				getDescription().getVersion()));

		// Create a new task that runs every 30 seconds (will show a warning every 30 seconds)
		getServer().getScheduler().runTaskTimer(this,
				new WarningNoticeTask(this), 5 * 20, 30 * 20);

		// Check if using MySQL
		usingMySQL = this.getMySQLWrapper().isMySQLEnabled();
		
		// Start collecting data
		if (!startMetrics()) {
			getLogger().info(
					"Failed to start Metrics, you can ignore this message");
		}
	}

	@Override
	public void onDisable() {
		setLeaderboard(null);

		playtimes.save();
		setPlaytimes(null);

		setPlayerChecker(null);

		setSimpleConfig(null);

		setAdvancedConfig(null);

		setWarningManager(null);

		// Make sure all tasks are cancelled after shutdown. This seems obvious, but when a player /reloads, the server creates an instance of the plugin which causes duplicate tasks to run. 
		getServer().getScheduler().cancelTasks(this);

		Autorank.logMessage(String.format("Autorank %s has been disabled!",
				getDescription().getVersion()));
	}

	private boolean startMetrics() {
		// Try to start metrics
		try {
			// Initialise
			metrics = new me.armar.plugins.autorank.metrics.Metrics(this);

			// Setup graph for MySQL
			Graph weaponsUsedGraph = metrics
					.createGraph("Percentage using MySQL");

			weaponsUsedGraph.addPlotter(new Metrics.Plotter("MySQL") {

				@Override
				public int getValue() {
					return usingMySQL ? 1: 0;
				}

			});

			metrics.start();
			return true;
		} catch (IOException e) {
			// Failed to submit the stats :-(
			return false;
		}
	}

	public LanguageHandler getLanguageHandler() {
		return languageHandler;
	}

	private void setLanguageHandler(final LanguageHandler lHandler) {
		this.languageHandler = lHandler;
	}

	public void reload() {
		getServer().getPluginManager().disablePlugin(this);
		getServer().getPluginManager().enablePlugin(this);
	}

	public int getLocalTime(final String player) {
		return playtimes.getLocalTime(player);
	}

	public int getGlobalTime(final String player) {
		return playtimes.getGlobalTime(player);
	}

	public void setLocalTime(final String player, final int time) {
		playtimes.setLocalTime(player, time);
	}

	public void setGlobalTime(final String player, final int time)
			throws SQLException {
		playtimes.setGlobalTime(player, time);
	}

	public void checkAndChangeRank(final Player player) {
		playerChecker.checkPlayer(player);
	}

	public void registerRequirement(final String name,
			final Class<? extends Requirement> requirement) {
		playerChecker.getBuilder().getRequirementBuilder()
				.registerRequirement(name, requirement);
	}

	public void registerResult(final String name,
			final Class<? extends Result> result) {
		playerChecker.getBuilder().getResultBuilder()
				.registerResult(name, result);
	}

	public static void logMessage(final String message) {
		log.info("[Autorank] " + message);
	}

	public Leaderboard getLeaderboard() {
		return leaderboard;
	}

	private void setLeaderboard(final Leaderboard leaderboard) {
		this.leaderboard = leaderboard;
	}

	public Playtimes getPlaytimes() {
		return playtimes;
	}

	private void setPlaytimes(final Playtimes playtimes) {
		this.playtimes = playtimes;
	}

	public SimpleYamlConfiguration getSimpleConfig() {
		return simpleConfig;
	}

	private void setSimpleConfig(final SimpleYamlConfiguration simpleConfig) {
		this.simpleConfig = simpleConfig;
	}

	public SimpleYamlConfiguration getAdvancedConfig() {
		return advancedConfig;
	}

	private void setAdvancedConfig(final SimpleYamlConfiguration advancedConfig) {
		this.advancedConfig = advancedConfig;
	}

	public PlayerChecker getPlayerChecker() {
		return playerChecker;
	}

	private void setPlayerChecker(final PlayerChecker playerChecker) {
		this.playerChecker = playerChecker;
	}

	public PermissionsPluginManager getPermPlugHandler() {
		return permPlugHandler;
	}

	public void setPermPlugHandler(
			final PermissionsPluginManager permPlugHandler) {
		this.permPlugHandler = permPlugHandler;
	}

	public ValidateHandler getValidateHandler() {
		return validateHandler;
	}

	public void setValidateHandler(final ValidateHandler validateHandler) {
		this.validateHandler = validateHandler;
	}

	public MySQLWrapper getMySQLWrapper() {
		return mysqlWrapper;
	}

	public void setMySQLWrapper(final MySQLWrapper mysqlWrapper) {
		this.mysqlWrapper = mysqlWrapper;
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

		final Updater updater = new Updater(this, 34447, this.getFile(),
				Updater.UpdateType.NO_DOWNLOAD, false);
		updateHandler.setUpdater(updater);

		return (updater.getResult()
				.equals(Updater.UpdateResult.UPDATE_AVAILABLE));

	}

	public UpdateHandler getUpdateHandler() {
		return updateHandler;
	}

	public void setUpdateHandler(final UpdateHandler updateHandler) {
		this.updateHandler = updateHandler;
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public void setConfigHandler(final ConfigHandler configHandler) {
		this.configHandler = configHandler;
	}

	public RequirementHandler getRequirementHandler() {
		return requirementHandler;
	}

	public void setRequirementHandler(
			final RequirementHandler requirementHandler) {
		this.requirementHandler = requirementHandler;
	}

	public API getAPI() {
		return new API(this);
	}

	public Debugger getDebugger() {
		return debugger;
	}

	public void setDebugger(final Debugger debugger) {
		this.debugger = debugger;
	}

	public FactionsHandler getFactionsHandler() {
		return factionsHandler;
	}

	public void setFactionsHandler(final FactionsHandler factionsHandler) {
		this.factionsHandler = factionsHandler;
	}

	public WarningManager getWarningManager() {
		return warningManager;
	}

	public void setWarningManager(final WarningManager warningManager) {
		this.warningManager = warningManager;
	}

	public CommandsManager getCommandsManager() {
		return commandsManager;
	}

	public void setCommandsManager(final CommandsManager commandsManager) {
		this.commandsManager = commandsManager;
	}

	public StatsPluginManager getStatsPluginManager() {
		return statsPluginManager;
	}

	public void setStatsPluginManager(StatsPluginManager statsPluginManager) {
		this.statsPluginManager = statsPluginManager;
	}

	public StatsPlugin getHookedStatsPlugin() {
		return getStatsPluginManager().getStatsPlugin();
	}

	public AddOnManager getAddonManager() {
		return addonManager;
	}

	public void setAddonManager(AddOnManager addonManager) {
		this.addonManager = addonManager;
	}

	public WorldGuardAPIHandler getWorldGuardAPIHandler() {
		return worldGuardAPIHandler;
	}

	public void setWorldGuardAPIHandler(WorldGuardAPIHandler worldGuardAPIHandler) {
		this.worldGuardAPIHandler = worldGuardAPIHandler;
	}

}
