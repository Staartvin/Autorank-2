package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Material;

import java.util.UUID;

public class ItemsCraftedRequirement extends AbstractRequirement {

    int timesCrafted = -1;
    Material itemCrafted = null;

    @Override
    public String getDescription() {

        String lang = itemCrafted == null ? Lang.ITEMS_CRAFTED_REQUIREMENT.getConfigValue(timesCrafted) :
                Lang.ITEM_CRAFTED_REQUIREMENT.getConfigValue(timesCrafted, itemCrafted);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getItemsCrafted(uuid, this.getWorld(), itemCrafted) + "/" + this.timesCrafted;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getItemsCrafted(uuid, this.getWorld(), itemCrafted) >= this.timesCrafted;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        if (options.length == 0) {
            return false;
        }

        if (options.length == 1) {
            // Only specified a number, so no specific item.

            try {
                this.timesCrafted = Integer.parseInt(options[0]);
            } catch (final Exception e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }

        } else {

            // Specified both a number and a material.

            try {
                this.itemCrafted = Material.getMaterial(options[0].trim().toUpperCase());
                this.timesCrafted = Integer.parseInt(options[1]);
            } catch (final Exception e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (timesCrafted < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return this.getStatisticsManager().getItemsCrafted(uuid, this.getWorld(), itemCrafted) * 1.0d / this.timesCrafted;
    }
}
