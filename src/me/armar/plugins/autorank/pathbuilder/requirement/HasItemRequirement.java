package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HasItemRequirement extends AbstractRequirement {

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
    public boolean initRequirement(final String[] options) {

        String materialName = null;
        int amount = 1;

        String displayName = null;
        boolean useDisplayName = false;

        if (options.length > 0)
            materialName = options[0].trim().toUpperCase().replace(" ", "_");
        if (options.length > 1)
            amount = (int) AutorankTools.stringToDouble(options[1]);
        if (options.length > 2) {
            // Displayname
            displayName = options[2];
        }
        if (options.length > 3) {
            // use display name?
            useDisplayName = (options[3].equalsIgnoreCase("true"));
        }

        if (materialName == null) {
            this.registerWarningMessage("There is no material specified.");
            return false;
        }

        Material matchedMaterial = Material.matchMaterial(materialName);

        if (matchedMaterial == null) {
            this.registerWarningMessage("Material '" + materialName + "' is not a valid material.");
            return false;
        }

        final ItemStack item = new ItemStack(matchedMaterial, amount);

        neededItem = new ItemWrapper(item, displayName, false, useDisplayName);

        if (amount <= 0) {
            this.registerWarningMessage("Amount must be strictly higher than 0");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
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

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public ItemStack getItem() {
        return item;
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
