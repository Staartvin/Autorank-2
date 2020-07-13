package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;

import java.util.UUID;

public class PlayerKillsRequirement extends AbstractRequirement {

    int totalPlayersKilled = -1;

    @Override
    public String getDescription() {
        String lang = Lang.PLAYER_KILLS_REQUIREMENT.getConfigValue(totalPlayersKilled + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        final int killed = this.getStatisticsManager().getPlayersKilled(uuid, this.getWorld());

        return killed + "/" + totalPlayersKilled + " player(s)";
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        return this.getStatisticsManager().getPlayersKilled(uuid, this.getWorld()) >= totalPlayersKilled;
    }

    @Override
    public boolean initRequirement(final String[] options) {
        try {
            totalPlayersKilled = Integer.parseInt(options[0]);
        } catch (final Exception e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (totalPlayersKilled < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        final int killed = this.getStatisticsManager().getPlayersKilled(uuid, this.getWorld());

        return killed * 1.0d / this.totalPlayersKilled;
    }
}
