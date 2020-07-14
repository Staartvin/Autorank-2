package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Material;

import java.util.UUID;

public class ItemThrownRequirement extends AbstractRequirement {

    int numberofThrows = -1;
    Material itemThrown = null;

    @Override
    public String getDescription() {

        String lang = Lang.ITEM_THROWN_REQUIREMENT.getConfigValue(numberofThrows, itemThrown);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getItemThrown(uuid, itemThrown) + "/" + this.numberofThrows;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getItemThrown(uuid, itemThrown) >= this.numberofThrows;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        try {
            this.itemThrown = Material.getMaterial(options[0].trim().toUpperCase());
            this.numberofThrows = Integer.parseInt(options[1]);
        } catch (final Exception e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (numberofThrows < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return numberofThrows > 0 && this.itemThrown != null;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return this.getStatisticsManager().getItemThrown(uuid, itemThrown) * 1.0d / this.numberofThrows;
    }
}
