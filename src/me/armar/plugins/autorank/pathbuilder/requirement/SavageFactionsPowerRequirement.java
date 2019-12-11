package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.SavageFactionsHook;

import java.text.DecimalFormat;
import java.util.UUID;

public class SavageFactionsPowerRequirement extends AbstractRequirement {

    double factionPower = -1;
    private SavageFactionsHook handler;

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
    public String getProgressString(UUID uuid) {
        final DecimalFormat df = new DecimalFormat("#.##");
        final String doubleRounded = df.format(handler.getFactionPower(uuid));

        return doubleRounded + "/" + factionPower;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        return handler.getFactionPower(uuid) >= this.factionPower;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.SAVAGE_FACTIONS);

        handler = (SavageFactionsHook) this.getAutorank().getDependencyManager().getLibraryHook(Library
                .SAVAGE_FACTIONS);

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
            this.registerWarningMessage("SavageFactions is not available");
            return false;
        }

        return true;
    }


}
