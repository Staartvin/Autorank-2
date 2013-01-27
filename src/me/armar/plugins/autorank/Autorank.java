package me.armar.plugins.autorank;

import java.util.logging.Logger;

import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.leaderboard.Leaderboard;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import me.armar.plugins.autorank.permissions.VaultPermissionsHandler;
import me.armar.plugins.autorank.playerchecker.*;
import me.armar.plugins.autorank.playerchecker.additionalrequirement.*;
import me.armar.plugins.autorank.playerchecker.builders.*;
import me.armar.plugins.autorank.playerchecker.result.*;
import me.armar.plugins.autorank.playtimes.Playtimes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Autorank extends JavaPlugin {

    private Leaderboard leaderboard;
    private Playtimes playtimes;
    private PermissionsHandler permissions;
    private PlayerChecker playerChecker;
    private SimpleYamlConfiguration simpleConfig;
    private SimpleYamlConfiguration advancedConfig;
    private VaultPermissionsHandler vaultPermHandler;
    private static Logger log = Bukkit.getLogger();

    public void onEnable() {
    
	getCommand("ar").setExecutor(new Commands(this));

	setSimpleConfig(new SimpleYamlConfiguration(this, "SimpleConfig.yml", null, "Simple config"));
	setAdvancedConfig(new SimpleYamlConfiguration(this, "AdvancedConfig.yml", null, "Advanced config"));
	
	setPermissionsHandler(new PermissionsHandler(this));
	setPlaytimes(new Playtimes(this));
	setLeaderboard(new Leaderboard(this));
	setPlayerChecker(new PlayerChecker(this));
	setVaultPermHandler(new VaultPermissionsHandler(this));
	
	AdditionalRequirementBuilder req = this.getPlayerChecker().getBuilder().getRequirementBuilder();
	ResultBuilder res = this.getPlayerChecker().getBuilder().getResultBuilder();
	
	req.registerAdditionalRequirement("exp", ExpRequirement.class);
	req.registerAdditionalRequirement("money", MoneyRequirement.class);
	req.registerAdditionalRequirement("time", TimeRequirement.class);
	req.registerAdditionalRequirement("gamemode", GamemodeRequirement.class);
	req.registerAdditionalRequirement("has item", HasItemRequirement.class);
	req.registerAdditionalRequirement("world", WorldRequirement.class);
	
	res.registerResult("command", CommandResult.class);
	res.registerResult("message", MessageResult.class);
	res.registerResult("rank change", RankChangeResult.class);
	
	playerChecker.initialiseFromConfigs(this);

	Autorank.logMessage("Autorank has been enabled!");
    }



    public void onDisable() {
	setLeaderboard(null);
	
	playtimes.save();
	setPlaytimes(null);
	
	setPlayerChecker(null);
	
	setPlayerChecker(null);
	
	setSimpleConfig(null);
	
	setAdvancedConfig(null);
	
	// Make sure all tasks are cancelled after shutdown. This seems obvious, but when a player /reloads, the server creates an instance of the plugin which causes duplicate tasks to run. 
	getServer().getScheduler().cancelAllTasks();
	
	Autorank.logMessage("Autorank has been disabled!");
    }

    public void reload() {
	getServer().getPluginManager().disablePlugin(this);
	getServer().getPluginManager().enablePlugin(this);
    }

    public int getTime(String player) {
	return playtimes.getTime(player);
    }

    public void setTime(String player, int time) {
	playtimes.setTime(player, time);
    }

    public void checkAndChangeRank(Player player) {
	playerChecker.checkPlayer(player);
    }

    public void registerAdditionalRequirement(String name, Class<? extends AdditionalRequirement> requirement) {
	playerChecker.getBuilder().getRequirementBuilder().registerAdditionalRequirement(name, requirement);
    }

    public void registerAdditionalResult(String name, Class<? extends Result> result) {
	playerChecker.getBuilder().getResultBuilder().registerResult(name, result);
    }
    
    public static void logMessage(String message){
	log.info("[Autorank] " + message);
    }

    public PermissionsHandler getPermissionsHandler() {
	return permissions;	
    }
    
    private void setPermissionsHandler(PermissionsHandler permissions) {
	this.permissions = permissions;	
    }
    
    public Leaderboard getLeaderboard() {
	return leaderboard;
    }
    
    private void setLeaderboard(Leaderboard leaderboard) {
	this.leaderboard = leaderboard;
    }
    
    public Playtimes getPlaytimes(){
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

	public VaultPermissionsHandler getVaultPermHandler() {
		return vaultPermHandler;
	}

	public void setVaultPermHandler(VaultPermissionsHandler vaultPermHandler) {
		this.vaultPermHandler = vaultPermHandler;
	}
    
}
