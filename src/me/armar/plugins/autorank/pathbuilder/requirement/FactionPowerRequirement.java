package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.FactionsHook;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class FactionPowerRequirement extends AbstractRequirement {

    double factionPower = -1;
    private FactionsHook handler;

    @Override
    public String getDescription() {

        String lang = Lang.FACTIONS_POWER_REQUIREMENT.getConfigValue(factionPower + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final DecimalFormat df = new DecimalFormat("#.##");
        final String doubleRounded = df.format(handler.getFactionPower(player.getUniqueId()));

        return doubleRounded + "/" + factionPower;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            // Is player in the same world as specified
            if (!this.getWorld().equals(player.getWorld().getName()))
                return false;
        }

        final double factionPower = handler.getFactionPower(player.getUniqueId());

        return factionPower >= this.factionPower;
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.FACTIONS);

        handler = (FactionsHook) this.getAutorank().getDependencyManager().getLibraryHook(Library.FACTIONS);

        try {
            factionPower = Double.parseDouble(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (factionPower < 0.0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (handler == null || !handler.isAvailable()) {
            this.registerWarningMessage("Factions is not available");
            return false;
        }

        return true;
    }
}
