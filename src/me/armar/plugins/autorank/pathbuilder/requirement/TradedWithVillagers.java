package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class TradedWithVillagers extends AbstractRequirement {

    int tradedWithVillagers = -1;

    @Override
    public String getDescription() {

        String lang = Lang.TIMES_TRADED_WITH_VILLAGERS_REQUIREMENT.getConfigValue(tradedWithVillagers);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        return this.getStatisticsManager().getTimesTradedWithVillagers(uuid) + "/" + this.tradedWithVillagers;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getTimesTradedWithVillagers(uuid) >= this.tradedWithVillagers;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        try {
            this.tradedWithVillagers = Integer.parseInt(options[0]);
        } catch (final Exception e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (tradedWithVillagers < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return this.getStatisticsManager().getTimesTradedWithVillagers(uuid) * 1.0d / this.tradedWithVillagers;
    }
}
