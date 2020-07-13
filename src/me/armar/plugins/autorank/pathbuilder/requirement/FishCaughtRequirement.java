package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class FishCaughtRequirement extends AbstractRequirement {

    int fishCaught = -1;

    @Override
    public String getDescription() {

        String lang = Lang.FISH_CAUGHT_REQUIREMENT.getConfigValue(fishCaught + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        final int progressBar = this.getStatisticsManager().getFishCaught(uuid, this.getWorld());

        return progressBar + "/" + fishCaught;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getFishCaught(uuid, this.getWorld()) >= fishCaught;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        try {
            fishCaught = Integer.parseInt(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (fishCaught < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        final int progressBar = this.getStatisticsManager().getFishCaught(uuid, this.getWorld());

        return progressBar * 1.0d / this.fishCaught;
    }
}
