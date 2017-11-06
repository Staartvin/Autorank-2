package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class HasItemRequirement extends Requirement {

    ItemWrapper neededItem = null;

    @Override
    public String getDescription() {

        final ItemStack item = neededItem.getItem();

        final StringBuilder arg = new StringBuilder(item.getAmount() + " ");

        if (neededItem.getDisplayName() != null) {
            // Show displayname instead of material name
            arg.append(neededItem.getDisplayName());
        } else {

            arg.append(item.getType().toString().replace("_", " ").toLowerCase());

            if (neededItem.showShortValue()) {
                arg.append(" (Dam. value: " + item.getDurability() + ")");
            }
        }

        String lang = Lang.ITEM_REQUIREMENT.getConfigValue(arg.toString());

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {

        final ItemStack item = neededItem.getItem();

        int slotAmount = 0;

        for (ItemStack itemInInv : player.getInventory().getStorageContents()) {
            if (itemInInv != null && itemInInv.isSimilar(item)) {
                slotAmount += itemInInv.getAmount();
            }
        }

        return slotAmount + "/" + item.getAmount();
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            // Is player in the same world as specified
            if (!this.getWorld().equals(player.getWorld().getName()))
                return false;
        }

        final ItemStack item = neededItem.getItem();

        if (item == null)
            return false;

        if (!neededItem.useDisplayName()) {
            return player.getInventory().containsAtLeast(item, item.getAmount());
        } else {
            // Check if player has items WITH proper displayname
            return AutorankTools.containsAtLeast(player, item, item.getAmount(), neededItem.getDisplayName());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean setOptions(final String[] options) {

        int id = -1;
        int amount = 1;
        short data = 0;

        String displayName = null;
        boolean showShortValue = false;
        boolean useDisplayName = false;

        if (options.length > 0)
            id = (int) AutorankTools.stringToDouble(options[0]);
        if (options.length > 1)
            amount = (int) AutorankTools.stringToDouble(options[1]);
        if (options.length > 2) {
            data = (short) AutorankTools.stringToDouble(options[2]);
            // Short value can make a difference, thus we show it.
            showShortValue = true;
        }
        if (options.length > 3) {
            // Displayname
            displayName = options[3];
        }
        if (options.length > 4) {
            // use display name?
            useDisplayName = (options[4].equalsIgnoreCase("true"));
        }

        final ItemStack item = new ItemStack(id, amount, data);

        neededItem = new ItemWrapper(item, displayName, showShortValue, useDisplayName);

        if (neededItem == null) {
            this.registerWarningMessage("No valid item is provided");
            return false;
        }

        if (id < 0) {
            this.registerWarningMessage("ID value cannot be negative");
            return false;
        }

        if (amount <= 0) {
            this.registerWarningMessage("Amount must be strictly higher than 0");
            return false;
        }

        return true;
    }
}

class ItemWrapper {

    private String displayName;
    private ItemStack item;
    private boolean showShortValue = false;

    // If true, the items should also match with displayname.
    private boolean useDisplayName = false;

    public ItemWrapper(final ItemStack item, final String displayName, final boolean showShortValue,
            final boolean useDisplayName) {
        this.setItem(item);
        this.setDisplayName(displayName);
        this.setShowShortValue(showShortValue);
        this.setUseDisplayName(useDisplayName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setItem(final ItemStack item) {
        this.item = item;
    }

    public void setShowShortValue(final boolean showShortValue) {
        this.showShortValue = showShortValue;
    }

    public void setUseDisplayName(final boolean useDisplayName) {
        this.useDisplayName = useDisplayName;
    }

    public boolean showShortValue() {
        return showShortValue;
    }

    public boolean useDisplayName() {
        return useDisplayName;
    }
}
