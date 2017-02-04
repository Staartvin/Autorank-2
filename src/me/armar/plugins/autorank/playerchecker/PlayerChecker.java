package me.armar.plugins.autorank.playerchecker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.result.Result;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;

/*
 * PlayerChecker is where the magic happens :P It has a RankChangeBuilder that reads
 * the config and makes new RankChange objects. It sends the names of the needed results
 * and requirements to AdditionalRequirementBuilder and ResultBuilder. Those are dynamic
 * factories because they don't have any hardcoded classes to build. You register all
 * the requirements or results when the plugin is started. Because of this other
 * plugins / addons can register their own custom requirements and results very easily.
 * 
 * So: PlayerChecker has a list of RankChanges and a RankChange has a list of AdditionalRequirement and Results.
 * 
 */
public class PlayerChecker {

    private final Autorank plugin;

    public PlayerChecker(final Autorank plugin) {
        this.plugin = plugin;
    }

    public boolean checkPlayer(final Player player) {

        // Do not rank a player when he is excluded
        if (AutorankTools.isExcludedFromRanking(player))
            return false;

        // Get chosen path
        Path chosenPath = plugin.getPathManager().getCurrentPath(player.getUniqueId());

        if (chosenPath == null)
            return false;

        return chosenPath.applyChange(player);
    }

    public void doLeaderboardExemptCheck(final Player player) {
        plugin.getPlayerDataConfig().hasLeaderboardExemption(player.getUniqueId(),
                AutorankPermission.EXCLUDE_FROM_LEADERBOARD.allows(player));
    }

    public List<String> formatRequirementsToList(final List<RequirementsHolder> holders,
            final List<Integer> metRequirements) {
        // Converts requirements into a list of readable requirements

        final List<String> messages = new ArrayList<String>();

        messages.add(ChatColor.GRAY + " ------------ ");

        for (int i = 0; i < holders.size(); i++) {
            final RequirementsHolder holder = holders.get(i);
            final int reqID = holder.getReqID();

            if (holder != null) {
                final StringBuilder message = new StringBuilder("     " + ChatColor.GOLD + (i + 1) + ". ");
                if (metRequirements.contains(reqID)) {
                    message.append(ChatColor.RED + holder.getDescription() + ChatColor.BLUE + " ("
                            + Lang.DONE_MARKER.getConfigValue() + ")");
                } else {
                    message.append(ChatColor.RED + holder.getDescription());
                }

                if (holder.isOptional()) {
                    message.append(ChatColor.AQUA + " (" + Lang.OPTIONAL_MARKER.getConfigValue() + ")");
                }

                messages.add(message.toString());

            }
        }

        return messages;

    }

    public List<String> formatResultsToList(List<Result> results) {
        // Converts requirements into a list of readable requirements

        final List<String> messages = new ArrayList<String>();

        messages.add(ChatColor.GRAY + " ------------ ");

        for (int i = 0; i < results.size(); i++) {
            final Result result = results.get(i);

            if (result != null) {
                final StringBuilder message = new StringBuilder("     " + ChatColor.GOLD + (i + 1) + ". ");

                message.append(ChatColor.RED + result.getDescription());

                messages.add(message.toString());

            }
        }

        return messages;

    }

    public List<RequirementsHolder> getAllRequirementsHolders(final Player player) {
        // Get chosen path
        Path chosenPath = plugin.getPathManager().getCurrentPath(player.getUniqueId());

        if (chosenPath != null) {
            return chosenPath.getRequirements();
        } else {
            return new ArrayList<RequirementsHolder>();
        }
    }

    public List<RequirementsHolder> getFailedRequirementsHolders(final Player player) {

        // Get chosen path
        Path chosenPath = plugin.getPathManager().getCurrentPath(player.getUniqueId());

        if (chosenPath != null) {
            return chosenPath.getFailedRequirementsHolders(player);
        } else {
            return new ArrayList<RequirementsHolder>();
        }
    }

    public List<Integer> getMetRequirementsHolders(final List<RequirementsHolder> holders, final Player player) {
        final List<Integer> metRequirements = new ArrayList<Integer>();

        boolean onlyOptional = true;

        // Check if we only have optional requirements
        for (final RequirementsHolder holder : holders) {
            if (!holder.isOptional())
                onlyOptional = false;
        }

        if (onlyOptional) {
            final List<Integer> optionalRequirements = new ArrayList<Integer>();

            for (final RequirementsHolder holder : holders) {
                optionalRequirements.add(holder.getReqID());
            }

            return optionalRequirements;
        }

        for (final RequirementsHolder holder : holders) {
            final int reqID = holder.getReqID();

            // Use auto completion
            if (holder.useAutoCompletion()) {
                // Do auto complete
                if (holder.meetsRequirement(player, player.getUniqueId(), false)) {
                    // Player meets the requirement -> give him results

                    // Doesn't need to check whether this requirement was
                    // already done
                    if (!plugin.getConfigHandler().usePartialCompletion())
                        continue;

                    metRequirements.add(reqID);
                    continue;
                } else {

                    // Only check if player has done this when partial
                    // completion is used
                    if (plugin.getConfigHandler().usePartialCompletion()) {
                        // Player does not meet requirements, but has done this
                        // already
                        if (plugin.getPlayerDataConfig().hasCompletedRequirement(reqID, player.getUniqueId())) {
                            metRequirements.add(reqID);
                            continue;
                        }
                    }

                    // If requirement is optional, we do not check.
                    if (holder.isOptional()) {
                        continue;
                    }

                    // Player does not meet requirements -> do nothing
                    continue;
                }
            } else {

                if (!plugin.getConfigHandler().usePartialCompletion()) {

                    // Doesn't auto complete and doesn't meet requirement, then
                    // continue searching
                    if (!holder.meetsRequirement(player, player.getUniqueId(), false)) {

                        // If requirement is optional, we do not check.
                        if (holder.isOptional()) {
                            continue;
                        }

                        continue;
                    } else {
                        // Player does meet requirement, continue searching
                        continue;
                    }

                }

                // Do not auto complete
                if (plugin.getPlayerDataConfig().hasCompletedRequirement(reqID, player.getUniqueId())) {
                    // Player has completed requirement already
                    metRequirements.add(reqID);
                    continue;
                } else {

                    // If requirement is optional, we do not check.
                    if (holder.isOptional()) {
                        continue;
                    }

                    continue;
                }
            }
        }
        return metRequirements;
    }
}
