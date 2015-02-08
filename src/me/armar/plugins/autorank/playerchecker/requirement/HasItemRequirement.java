package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HasItemRequirement extends Requirement {

	// id;amount;data;name
	private final List<ItemWrapper> neededItems = new ArrayList<ItemWrapper>();

	//List<String> neededItems = new ArrayList<String>();

	//private String displayName = null;
	//ItemStack item = null;
	//private boolean showShortValue = false;

	@Override
	public String getDescription() {

		final List<String> names = new ArrayList<String>();

		for (int i = 0; i < neededItems.size(); i++) {
			final ItemWrapper wrapper = neededItems.get(i);

			final ItemStack item = wrapper.getItem();

			final StringBuilder arg = new StringBuilder(item.getAmount() + " ");

			if (wrapper.getDisplayName() != null) {
				// Show displayname instead of material name
				arg.append(wrapper.getDisplayName());
			} else {

				arg.append(item.getType().toString());

				if (wrapper.showShortValue()) {
					arg.append(" (Dam. value: " + item.getDurability() + ")");
				}
			}

			names.add(arg.toString());
			/*if (i == 0) {
				names.add(arg.toString());
			} else {
				names.add(" or " + arg.toString());
			}*/
		}

		return Lang.ITEM_REQUIREMENT.getConfigValue(AutorankTools.seperateList(
				names, "or"));
	}

	@Override
	public String getProgress(final Player player) {

		String progress = "";

		for (int i = 0; i < neededItems.size(); i++) {
			final ItemWrapper wrapper = neededItems.get(i);
			final ItemStack item = wrapper.getItem();

			final int firstSlot = player.getInventory().first(item.getType());
			int slotAmount = 0;

			if (firstSlot >= 0) {
				slotAmount = player.getInventory().getItem(firstSlot)
						.getAmount();
			}

			if (i == 0) {
				progress = progress.concat(slotAmount + "/" + item.getAmount());
			} else {
				progress = progress.concat(" or " + slotAmount + "/"
						+ item.getAmount());
			}
		}

		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		for (final ItemWrapper wrapper : neededItems) {
			final ItemStack item = wrapper.getItem();

			if (item != null
					&& player.getInventory().containsAtLeast(item,
							item.getAmount())) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			int id = 0;
			int amount = 1;
			short data = 0;

			String displayName = null;
			boolean showShortValue = false;

			if (options.length > 0)
				id = AutorankTools.stringtoInt(options[0]);
			if (options.length > 1)
				amount = AutorankTools.stringtoInt(options[1]);
			if (options.length > 2) {
				data = (short) AutorankTools.stringtoInt(options[2]);
				// Short value can make a difference, thus we show it.
				showShortValue = true;
			}
			if (options.length > 3) {
				// Displayname
				displayName = options[3];
			}

			//item = new ItemStack(id, 1, (short) 0, data);
			final ItemStack item = new ItemStack(id, amount, data);

			neededItems.add(new ItemWrapper(item, displayName, showShortValue));
		}

		return !neededItems.isEmpty();
	}
}

class ItemWrapper {

	private ItemStack item;
	private String displayName;
	private boolean showShortValue = false;

	public ItemWrapper(final ItemStack item, final String displayName,
			final boolean showShortValue) {
		this.setItem(item);
		this.setDisplayName(displayName);
		this.setShowShortValue(showShortValue);
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(final ItemStack item) {
		this.item = item;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public boolean showShortValue() {
		return showShortValue;
	}

	public void setShowShortValue(final boolean showShortValue) {
		this.showShortValue = showShortValue;
	}
}
