package me.armar.plugins.autorank;

import java.sql.SQLException;
import java.util.logging.Logger;

import me.armar.plugins.autorank.api.API;
import me.armar.plugins.autorank.config.ConfigHandler;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.debugger.Debugger;
import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.leaderboard.Leaderboard;
import me.armar.plugins.autorank.listeners.PlayerJoinListener;
import me.armar.plugins.autorank.mysql.wrapper.MySQLWrapper;
import me.armar.plugins.autorank.permissions.PermissionsPluginHandler;
import me.armar.plugins.autorank.playerchecker.PlayerChecker;
import me.armar.plugins.autorank.playerchecker.builders.RequirementBuilder;
import me.armar.plugins.autorank.playerchecker.builders.ResultBuilder;
import me.armar.plugins.autorank.playerchecker.requirement.BlocksBrokenRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.BlocksPlacedRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.DamageTakenRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.ExpRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.GamemodeRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.HasItemRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.LocationRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.MobKillsRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.MoneyRequirement;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.requirement.TimeRequirement;
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
import me.armar.plugins.autorank.statsapi.StatsHandler;
import me.armar.plugins.autorank.updater.UpdateHandler;
import me.armar.plugins.autorank.updater.Updater;
import me.armar.plugins.autorank.validations.ValidateHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Autorank extends JavaPlugin {

	private Leaderboard leaderboard;
	private Playtimes playtimes;
	private PlayerChecker playerChecker;
	private SimpleYamlConfiguration simpleConfig;
	private SimpleYamlConfiguration advancedConfig;
	private PermissionsPluginHandler permPlugHandler;
	private LanguageHandler languageHandler;
	private ValidateHandler validateHandler;
	private StatsHandler statsHandler;
	private MySQLWrapper mysqlWrapper;
	private static Logger log = Bukkit.getLogger();
	private UpdateHandler updateHandler;
	private ConfigHandler configHandler;
	private RequirementHandler requirementHandler;
	private Debugger debugger;

	public void onEnable() {

		// TODO: Add our own Stats logger which keeps track of (a lot of) things
		
		// Register configs
		setSimpleConfig(new SimpleYamlConfiguration(this, "SimpleConfig.yml",
				null, "Simple config"));
		setAdvancedConfig(new SimpleYamlConfiguration(this,
				"AdvancedConfig.yml", null, "Advanced config"));

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

		// Create playtime class
		setPlaytimes(new Playtimes(this));

		// Create leaderboard class
		setLeaderboard(new Leaderboard(this));

		// Create permission plugin handler class
		setPermPlugHandler(new PermissionsPluginHandler(this));

		// Create player check class
		setPlayerChecker(new PlayerChecker(this));

		// Create validate handler
		setValidateHandler(new ValidateHandler(this));

		// Create stats handler
		setStatsHandler(new StatsHandler(this));

		if (statsHandler.setupStatsAPI()) {
			getLogger().info(
					"Hooked into Stats! Extra requirements can be used.");
		}

		RequirementBuilder req = this.getPlayerChecker().getBuilder()
				.getRequirementBuilder();
		ResultBuilder res = this.getPlayerChecker().getBuilder()
				.getResultBuilder();

		// Register 'main' requirements
		req.registerRequirement("exp", ExpRequirement.class);
		req.registerRequirement("money", MoneyRequirement.class);
		req.registerRequirement("time", TimeRequirement.class);
		req.registerRequirement("gamemode", GamemodeRequirement.class);
		req.registerRequirement("has item", HasItemRequirement.class);
		req.registerRequirement("world", WorldRequirement.class);
		req.registerRequirement("blocks broken", BlocksBrokenRequirement.class);
		req.registerRequirement("blocks placed", BlocksPlacedRequirement.class);
		req.registerRequirement("votes", TotalVotesRequirement.class);
		req.registerRequirement("damage taken", DamageTakenRequirement.class);
		req.registerRequirement("mobs killed", MobKillsRequirement.class);
		req.registerRequirement("location", LocationRequirement.class);

		// Register 'main' results
		res.registerResult("command", CommandResult.class);
		res.registerResult("effect", EffectResult.class);
		res.registerResult("message", MessageResult.class);
		res.registerResult("rank change", RankChangeResult.class);
		res.registerResult("tp", TeleportResult.class);

		playerChecker.initialiseFromConfigs(this);

		// Register command
		getCommand("ar").setExecutor(new Commands(this));

		if (configHandler.useAdvancedConfig()) {
			if (!getValidateHandler().validateConfigGroups(getAdvancedConfig())) {
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		} else {
			if (!getValidateHandler().validateConfigGroups(getSimpleConfig())) {
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		
		// Setup language file
		languageHandler.createNewFile();
		
		// Create MySQL Wrapper
		setMySQLWrapper(new MySQLWrapper(this));
		
		// Set debugger
		setDebugger(new Debugger(this));

		Autorank.logMessage(String.format("Autorank %s has been enabled!",
				getDescription().getVersion()));
	}

	public void onDisable() {
		setLeaderboard(null);

		playtimes.save();
		setPlaytimes(null);

		setPlayerChecker(null);

		setSimpleConfig(null);

		setAdvancedConfig(null);

		// Make sure all tasks are cancelled after shutdown. This seems obvious, but when a player /reloads, the server creates an instance of the plugin which causes duplicate tasks to run. 
		getServer().getScheduler().cancelTasks(this);

		Autorank.logMessage(String.format("Autorank %s has been disabled!",
				getDescription().getVersion()));
	}

	public LanguageHandler getLanguageHandler() {
		return languageHandler;
	}

	private void setLanguageHandler(LanguageHandler lHandler) {
		this.languageHandler = lHandler;
	}

	public void reload() {
		getServer().getPluginManager().disablePlugin(this);
		getServer().getPluginManager().enablePlugin(this);
	}

	public int getLocalTime(String player) {
		return playtimes.getLocalTime(player);
	}

	public int getGlobalTime(String player) {
		return playtimes.getGlobalTime(player);
	}

	public void setLocalTime(String player, int time) {
		playtimes.setLocalTime(player, time);
	}

	public void setGlobalTime(String player, int time) throws SQLException {
		playtimes.setGlobalTime(player, time);
	}

	public void checkAndChangeRank(Player player) {
		playerChecker.checkPlayer(player);
	}

	public void registerRequirement(String name,
			Class<? extends Requirement> requirement) {
		playerChecker.getBuilder().getRequirementBuilder()
				.registerRequirement(name, requirement);
	}

	public void registerResult(String name, Class<? extends Result> result) {
		playerChecker.getBuilder().getResultBuilder()
				.registerResult(name, result);
	}

	public static void logMessage(String message) {
		log.info("[Autorank] " + message);
	}

	public Leaderboard getLeaderboard() {
		return leaderboard;
	}

	private void setLeaderboard(Leaderboard leaderboard) {
		this.leaderboard = leaderboard;
	}

	public Playtimes getPlaytimes() {
		return playtimes;
	}

	private void setPlaytimes(Playtimes playtimes) {
		this.playtimes = playtimes;
	}

	public SimpleYamlConfiguration getSimpleConfig() {
		return simpleConfig;
	}

	private void setSimpleConfig(SimpleYamlConfiguration simpleConfig) {
		this.simpleConfig = simpleConfig;
	}

	public SimpleYamlConfiguration getAdvancedConfig() {
		return advancedConfig;
	}

	private void setAdvancedConfig(SimpleYamlConfiguration advancedConfig) {
		this.advancedConfig = advancedConfig;
	}

	public PlayerChecker getPlayerChecker() {
		return playerChecker;
	}

	private void setPlayerChecker(PlayerChecker playerChecker) {
		this.playerChecker = playerChecker;
	}

	public PermissionsPluginHandler getPermPlugHandler() {
		return permPlugHandler;
	}

	public void setPermPlugHandler(PermissionsPluginHandler permPlugHandler) {
		this.permPlugHandler = permPlugHandler;
	}

	public ValidateHandler getValidateHandler() {
		return validateHandler;
	}

	public void setValidateHandler(ValidateHandler validateHandler) {
		this.validateHandler = validateHandler;
	}

	public StatsHandler getStatsHandler() {
		return statsHandler;
	}

	public void setStatsHandler(StatsHandler statsHandler) {
		this.statsHandler = statsHandler;
	}

	public MySQLWrapper getMySQLWrapper() {
		return mysqlWrapper;
	}

	public void setMySQLWrapper(MySQLWrapper mysqlWrapper) {
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

		Updater updater = new Updater(this, "autorank", this.getFile(),
				Updater.UpdateType.NO_DOWNLOAD, false);
		updateHandler.setUpdater(updater);

		return (updater.getResult()
				.equals(Updater.UpdateResult.UPDATE_AVAILABLE));

	}

	public UpdateHandler getUpdateHandler() {
		return updateHandler;
	}

	public void setUpdateHandler(UpdateHandler updateHandler) {
		this.updateHandler = updateHandler;
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public void setConfigHandler(ConfigHandler configHandler) {
		this.configHandler = configHandler;
	}

	public RequirementHandler getRequirementHandler() {
		return requirementHandler;
	}

	public void setRequirementHandler(RequirementHandler requirementHandler) {
		this.requirementHandler = requirementHandler;
	}
	
	public API getAPI() {
		return new API(this);
	}

	public Debugger getDebugger() {
		return debugger;
	}

	public void setDebugger(Debugger debugger) {
		this.debugger = debugger;
	}
}
