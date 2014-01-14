package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class HasItemRequirement extends Requirement {

	ItemStack item = null;
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	@SuppressWarnings("deprecation")
	@Override
	public boolean setOptions(final String[] options, final boolean optional,
			final List<Result> results, final boolean autoComplete,
			final int reqId) {
		int id = 0;
		int amount = 1;
		byte data = 0;
		this.reqId = reqId;

		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;

		if (options.length > 0)
			id = AutorankTools.stringtoInt(options[0]);
		if (options.length > 1)
			amount = AutorankTools.stringtoInt(options[1]);
		if (options.length > 2)
			data = (byte) AutorankTools.stringtoInt(options[2]);

		//item = new ItemStack(id, 1, (short) 0, data);
		item = (new MaterialData(id, data)).toItemStack(amount);

		return item != null;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}

		return item != null && player.getInventory().containsAtLeast(item, item.getAmount());
	}

	@Override
	public String getDescription() {
		final String arg = item.getAmount() + " " + item.getType().toString();
		return Lang.ITEM_REQUIREMENT.getConfigValue(new String[] { arg });
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

	@Override
	public String getProgress(final Player player) {
		int firstSlot = player.getInventory().first(item.getType());
		int slotAmount = 0;
		
		if (firstSlot >= 0) {
			slotAmount = player.getInventory().getItem(firstSlot).getAmount();	
		}
		
		String progress = "";
		progress = progress
				.concat(slotAmount + "/" + item.getAmount());
		return progress;
	}

	@Override
	public boolean useAutoCompletion() {
		return autoComplete;
	}

	@Override
	public int getReqId() {
		return reqId;
	}
}
