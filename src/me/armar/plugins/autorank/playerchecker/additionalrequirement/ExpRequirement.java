package me.armar.plugins.autorank.playerchecker.additionalrequirement;

import me.armar.plugins.autorank.AutorankTools;

import org.bukkit.entity.Player;

public class ExpRequirement extends AdditionalRequirement{
    
    private int minExp = 999999999;

    @Override
    public boolean setOptions(String[] options) {
	try{
	minExp = AutorankTools.stringtoInt(options[0]);
	}catch(Exception e){}
	
	return minExp == 999999999;
    }

    @Override
    public boolean meetsRequirement(Player player) {
	return player.getLevel() >= minExp;
    }

    @Override
    public String getDescription() {
	return "Need a minimum level of " + minExp + ".";
    }

}
