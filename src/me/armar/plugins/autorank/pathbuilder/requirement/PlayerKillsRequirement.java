package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class PlayerKillsRequirement extends Requirement {

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
    public String getProgress(final Player player) {
        final int killed = getStatsPlugin().getNormalStat(StatsPlugin.StatType.PLAYERS_KILLED, player.getUniqueId(),
                AutorankTools.makeStatsInfo("world", this.getWorld()));

        return killed + "/" + totalPlayersKilled + " player(s)";
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        if (!getStatsPlugin().isEnabled())
            return false;

        final int killed = getStatsPlugin().getNormalStat(StatsPlugin.StatType.PLAYERS_KILLED, player.getUniqueId(),
                AutorankTools.makeStatsInfo("world", this.getWorld()));

        return killed >= totalPlayersKilled;
    }

    @Override
    public boolean setOptions(final String[] options) {

        try {
            totalPlayersKilled = Integer.parseInt(options[0]);
        } catch (final Exception e) {
            return false;
        }

        return totalPlayersKilled != -1;
    }
}
