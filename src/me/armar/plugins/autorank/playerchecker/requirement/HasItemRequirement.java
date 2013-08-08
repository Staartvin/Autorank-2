package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class HasItemRequirement extends Requirement {

	ItemStack item = null;
	private boolean optional = false;
	List<Result> results = new ArrayList<Result>();

	@Override
	public boolean setOptions(String[] options, boolean optional, List<Result> results) {
		int id = 0;
		int amount = 1;
		byte data = 0;

		this.optional = optional;
		this.results = results;
		
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
		if (isCompleted(getReqID(this.getClass(), player), player.getName())) {
			return true;
		}
		
		return item != null && player.getInventory().contains(item);
	}

	@Override
	public String getDescription() {
		return "Obtain " + item.getAmount() + " "
				+ item.getType().toString() + ".";
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

}
