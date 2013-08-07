package me.armar.plugins.autorank.playerchecker.requirement;

import me.armar.plugins.autorank.AutorankTools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class HasItemRequirement extends Requirement {

	ItemStack item = null;
	private boolean optional = false;

	@Override
	public boolean setOptions(String[] options, boolean optional) {
		int id = 0;
		int amount = 1;
		byte data = 0;

		this.optional = optional;
		
		if (options.length > 0)
			id = AutorankTools.stringtoInt(options[0]);
		if (options.length > 1)
			amount = AutorankTools.stringtoInt(options[1]);
		if (options.length > 1)
		    data = (byte) AutorankTools.stringtoInt(options[2]);

		//item = new ItemStack(id, 1, (short) 0, data);
		item = (new MaterialData(id, (byte) data)).toItemStack(amount);

		return item != null;
	}

	@Override
	public boolean meetsRequirement(Player player) {
		return item != null && player.getInventory().contains(item);
	}

	@Override
	public String getDescription() {
		return "Need to have " + item.getAmount() + " "
				+ item.getType().toString() + ".";
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

}
