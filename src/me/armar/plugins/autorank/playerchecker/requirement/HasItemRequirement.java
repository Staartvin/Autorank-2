package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

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

				arg.append(item.getType().toString().replace("_", " ").toLowerCase());

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

		String lang = Lang.ITEM_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(names, "or"));

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			lang = lang.concat(" (in world '" + this.getWorld() + "')");
		}

		return lang;
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

		// Check if this requirement is world-specific
		if (this.isWorldSpecific()) {
			// Is player in the same world as specified
			if (!this.getWorld().equals(player.getWorld().getName()))
				return false;
		}

		for (final ItemWrapper wrapper : neededItems) {
			final ItemStack item = wrapper.getItem();

			if (item == null)
				return false;

			if (!wrapper.useDisplayName()) {
				return player.getInventory().containsAtLeast(item,
						item.getAmount());
			} else {
				// Check if player has items WITH proper displayname
				return AutorankTools.containsAtLeast(player, item,
						item.getAmount(), wrapper.getDisplayName());

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
			boolean useDisplayName = false;

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
			if (options.length > 4) {
				// use display name?
				useDisplayName = (options[4].equalsIgnoreCase("true") ? true
						: false);
			}

			//item = new ItemStack(id, 1, (short) 0, data);
			final ItemStack item = new ItemStack(id, amount, data);

			neededItems.add(new ItemWrapper(item, displayName, showShortValue,
					useDisplayName));
		}

		return !neededItems.isEmpty();
	}
}

class ItemWrapper {

	private ItemStack item;
	private String displayName;
	private boolean showShortValue = false;

	// If true, the items should also match with displayname.
	private boolean useDisplayName = false;

	public ItemWrapper(final ItemStack item, final String displayName,
			final boolean showShortValue, boolean useDisplayName) {
		this.setItem(item);
		this.setDisplayName(displayName);
		this.setShowShortValue(showShortValue);
		this.setUseDisplayName(useDisplayName);
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

	public boolean useDisplayName() {
		return useDisplayName;
	}

	public void setUseDisplayName(boolean useDisplayName) {
		this.useDisplayName = useDisplayName;
	}
}
