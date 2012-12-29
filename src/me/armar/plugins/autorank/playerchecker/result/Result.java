package me.armar.plugins.autorank.playerchecker.result;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.entity.Player;

public abstract class Result {
    
    private Autorank autorank;
    
    public final void setAutorank(Autorank autorank){
	this.autorank = autorank;
    }
    public final Autorank getAutorank() {
	return autorank;
    }
    
    public abstract boolean setOptions(String[] options);
    public abstract boolean applyResult(Player player);
    
    public String toString(){
	return this.getClass().getSimpleName();
    }

}
