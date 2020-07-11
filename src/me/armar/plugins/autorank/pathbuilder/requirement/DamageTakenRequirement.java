package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class DamageTakenRequirement extends AbstractRequirement {

    int damageTaken = -1;

    @Override
    public String getDescription() {

        String lang = Lang.DAMAGE_TAKEN_REQUIREMENT.getConfigValue(damageTaken + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        final int damTaken = this.getStatsPlugin().getDamageTaken(uuid, this.getWorld());

        return damTaken + "/" + damageTaken;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!getStatsPlugin().isEnabled())
            return false;

        final int damTaken = this.getStatsPlugin().getDamageTaken(uuid, this.getWorld());

        return damTaken >= damageTaken;
    }

    @Override
    public boolean initRequirement(final String[] options) {
        try {
            damageTaken = Integer.parseInt(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (damageTaken < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        final int damTaken = this.getStatsPlugin().getDamageTaken(uuid, this.getWorld());

        return damTaken * 1.0d / this.damageTaken;
    }
}
