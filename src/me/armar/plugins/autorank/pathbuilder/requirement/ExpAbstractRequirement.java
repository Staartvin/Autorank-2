package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class ExpAbstractRequirement extends AbstractRequirement {

    int minExp = -1;

    @Override
    public String getDescription() {

        String lang = Lang.EXP_REQUIREMENT.getConfigValue(minExp + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final int expLevel = player.getLevel();

        return expLevel + "/" + minExp;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            // Is player in the same world as specified
            if (!this.getWorld().equals(player.getWorld().getName()))
                return false;
        }

        final int expLevel = player.getLevel();

        return expLevel >= minExp;
    }

    @Override
    public boolean setOptions(final String[] options) {

        minExp = (int) AutorankTools.stringToDouble(options[0]);

        if (minExp < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }
}
