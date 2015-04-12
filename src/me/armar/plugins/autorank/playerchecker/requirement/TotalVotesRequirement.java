package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class TotalVotesRequirement extends Requirement {

    List<Integer> totalVotes = new ArrayList<Integer>();

    @Override
    public String getDescription() {
        return Lang.VOTE_REQUIREMENT.getConfigValue(AutorankTools.seperateList(
                totalVotes, "or"));
    }

    @Override
    public String getProgress(final Player player) {
        String progress = "";
        final int votes = getStatsPlugin().getNormalStat(
                StatsHandler.statTypes.VOTES.toString(), player.getUniqueId());

        progress = AutorankTools.makeProgressString(totalVotes, "", "" + votes);
        return progress;
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        if (!this.getStatsPlugin().isEnabled()) {
            return false;
        }

        final int votes = getStatsPlugin().getNormalStat(
                StatsHandler.statTypes.VOTES.toString(), player.getUniqueId());

        for (final int totalVote : totalVotes) {
            if (votes >= totalVote) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean setOptions(final List<String[]> optionsList) {

        for (final String[] options : optionsList) {
            try {
                totalVotes.add(Integer.parseInt(options[0]));
            } catch (final Exception e) {
                return false;
            }
        }

        return !totalVotes.isEmpty();
    }
}
