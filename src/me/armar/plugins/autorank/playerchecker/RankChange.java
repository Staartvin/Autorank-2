package me.armar.plugins.autorank.playerchecker;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.playerchecker.additionalrequirement.AdditionalRequirement;
import me.armar.plugins.autorank.playerchecker.result.Result;

public class RankChange {
    
    private String rank;
    private List<AdditionalRequirement> req;
    private List<Result> res;

    public RankChange(String rank, List<AdditionalRequirement> req, List<Result> res) {
	this.rank = rank;
	this.req = req;
	this.res = res;
    }

    public String getRank() {
        return rank;
    }

    public List<AdditionalRequirement> getReq() {
        return req;
    }

    public List<Result> getRes() {
        return res;
    }
    
    public boolean checkRequirements(Player player){
	boolean result = true;
	
	for(AdditionalRequirement r:req){
	    if(r != null)
	    if(!r.meetsRequirement(player)){
		result = false;
		break;
	    }
	}
	
	return result;
    }
    
    public List<AdditionalRequirement> getFailedRequirements(Player player){
	List<AdditionalRequirement> failed = new CopyOnWriteArrayList<AdditionalRequirement>();
	failed.addAll(req);
	
	for(AdditionalRequirement r:failed){
	    if(r != null)
	    if(r.meetsRequirement(player)){
		failed.remove(r);
	    }
	}
	
	return failed;
    }
    
    public boolean applyChange(Player player){
	boolean result = true;
	
	if(checkRequirements(player)){
	    for(Result r: res){
		if(r != null)
		if(!r.applyResult(player))
		    result = false;
	    }
	    
	}else{
	    result = false;
	}
	
	return result;
    }
    
    public String toString(){
	StringBuilder b = new StringBuilder();
	b.append(rank);
	b.append(": ");
	
	boolean first = true;
	for(AdditionalRequirement r:req){
	    if(!first)
		b.append(", ");
	    first = false;
	    b.append(r.toString());
	}
	
	    b.append(" -> ");
	
	first = true;
	for(Result r:res){
	    if(!first)
		b.append(", ");
	    first = false;
	    b.append(r.toString());
	}
	return b.toString();
    }

}
