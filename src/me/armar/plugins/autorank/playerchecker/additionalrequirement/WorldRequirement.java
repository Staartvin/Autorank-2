package me.armar.plugins.autorank.playerchecker.additionalrequirement;

import org.bukkit.entity.Player;

public class WorldRequirement extends AdditionalRequirement{

    String world = null;
    
    @Override
    public boolean setOptions(String[] options) {
	if(options.length>0)
	this.world = options[0];
	return (world != null);
    }

    @Override
    public boolean meetsRequirement(Player player) {
	return world != null && world.equals(player.getWorld().getName());
    }

    @Override
    public String getDescription() {
	return "Player needs to be in world " + world + ".";
    }

}
