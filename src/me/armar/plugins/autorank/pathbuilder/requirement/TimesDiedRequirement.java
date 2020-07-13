package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class TimesDiedRequirement extends AbstractRequirement {

    int timesDied = -1;

    @Override
    public String getDescription() {

        String lang = Lang.TIMES_DIED_REQUIREMENT.getConfigValue(timesDied);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getTimesDied(uuid) + "/" + this.timesDied;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getTimesDied(uuid) >= this.timesDied;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        try {
            this.timesDied = Integer.parseInt(options[0]);
        } catch (final Exception e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (timesDied < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return this.getStatisticsManager().getTimesDied(uuid) * 1.0d / this.timesDied;
    }
}
