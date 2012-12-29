package me.armar.plugins.autorank.playerchecker.additionalrequirement;

import me.armar.plugins.autorank.AutorankTools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HasItemRequirement extends AdditionalRequirement{

    ItemStack item = null;
    
    @Override
    public boolean setOptions(String[] options) {
	int id = 0;
	byte data = 0;
	
	if(options.length>0)
	id = AutorankTools.stringtoInt(options[0]);
	if(options.length>1)
	data = (byte) AutorankTools.stringtoInt(options[1]);
	
	item = new ItemStack(id, 1, (short) 0, data);
	
	return item != null;
    }

    @Override
    public boolean meetsRequirement(Player player) {
	return item !=null && player.getInventory().contains(item);
    }

    @Override
    public String getDescription() {
	return "Player needs to have item " + item + ".";
    }

}
