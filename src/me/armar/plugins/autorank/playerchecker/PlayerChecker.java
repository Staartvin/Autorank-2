package me.armar.plugins.autorank.playerchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.permissions.PermissionsHandler;
import me.armar.plugins.autorank.playerchecker.additionalrequirement.AdditionalRequirement;
import me.armar.plugins.autorank.playerchecker.builders.RankChangeBuilder;

import org.bukkit.entity.Player;

public class PlayerChecker{
    
    private PermissionsHandler permissions;
    private PlayerCheckerTrigger trigger;
    private Map<String, List<RankChange>> rankChanges = new HashMap<String, List<RankChange>>();
    private RankChangeBuilder builder;
    
    public PlayerChecker(Autorank plugin) {
	permissions = plugin.getPermissionsHandler();
	setTrigger(new PlayerCheckerTrigger(plugin, this));
	setBuilder(new RankChangeBuilder(plugin));
    }
 
    public void initialiseFromConfigs(Autorank plugin) {
	SimpleYamlConfiguration simpleConfig = plugin.getSimpleConfig();
	SimpleYamlConfiguration advancedConfig = plugin.getAdvancedConfig();
	
	List<RankChange> ranks;
	if((Boolean) advancedConfig.get("use advanced config")){
	    ranks = builder.createFromAdvancedConfig(advancedConfig);
	}else{
	    ranks = builder.createFromSimpleConfig(simpleConfig);
	}
	
	for(RankChange rank : ranks){
	    addRankChange(rank.getRank(), rank);
	}
	
    }

    public void addRankChange(String name, RankChange change){
	if(rankChanges.get(name) == null){
	    List<RankChange> list = new ArrayList<RankChange>();
	    list.add(change);
	    rankChanges.put(name, list);
	}else{
	    rankChanges.get(name).add(change);
	}
    }
    
    public boolean checkPlayer(Player player){
	boolean result = false;
	
	String[] groups = permissions.getPlayerGroups(player);
	
	for(String group : groups){
	    	List<RankChange> changes = rankChanges.get(group);
		if(changes != null){
		    for(RankChange change: changes){
			if(change.applyChange(player)){
			    result = true;
			}
		    }
		}
	}
	
	return result;
    }
    
    public Map<RankChange, List<AdditionalRequirement>> getFailedRequirementsForApplicableGroup(Player player){
	Map<RankChange, List<AdditionalRequirement>> result = new HashMap<RankChange, List<AdditionalRequirement>>();
	
	String[] groups = permissions.getPlayerGroups(player);
	
	for(String group : groups){
	    	List<RankChange> changes = rankChanges.get(group);
		if(changes != null){
		    for(RankChange change: changes){
			result.put(change, change.getFailedRequirements(player));
		    }
		}
	}
	
	return result;
    }

    public PlayerCheckerTrigger getTrigger() {
	return trigger;
    }

    private void setTrigger(PlayerCheckerTrigger trigger) {
	this.trigger = trigger;
    }

    public RankChangeBuilder getBuilder() {
	return builder;
    }

    private void setBuilder(RankChangeBuilder builder) {
	this.builder = builder;
    }
    
    public String[] toStringArray(){
	List<String> list = new ArrayList<String>();
	
	for(String name : rankChanges.keySet()){
	    
	    List<RankChange> changes = rankChanges.get(name);
	    for(RankChange change: changes){
		    
		if(change == null){
		    list.add("- NULL");
		}else{
		    list.add("- " + change.toString());
		}
	    }
	   }
	
	return list.toArray(new String[]{});
    }

}
