package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.util.AutorankTools;
import me.staartvin.plugins.pluginlibrary.Library;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TotalVotesRequirement extends AbstractRequirement {

    int totalVotes = -1;

    @Override
    public String getDescription() {

        String lang = Lang.VOTE_REQUIREMENT.getConfigValue(totalVotes + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final int votes = getStatsPlugin().getNormalStat(StatsPlugin.StatType.VOTES, player.getUniqueId(),
                AutorankTools.makeStatsInfo("world", this.getWorld()));

        return votes + "/" + totalVotes;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        if (!getStatsPlugin().isEnabled())
            return false;

        final int votes = getStatsPlugin().getNormalStat(StatsPlugin.StatType.VOTES, uuid,
                AutorankTools.makeStatsInfo("world", this.getWorld()));

        return votes >= totalVotes;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.STATZ);

        try {
            totalVotes = Integer.parseInt(options[0]);
        } catch (final Exception e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (totalVotes < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }


        return true;
    }
}
