package me.armar.plugins.autorank.playerchecker;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

        // Try to assign paths to a player automatically.
        plugin.getPathManager().autoAssignPaths(player);

        // Get active paths
        List<Path> activePaths = plugin.getPathManager().getActivePaths(player.getUniqueId());

        boolean result = false;

        // For all paths, check the progress of the player.
        for (Path activePath : activePaths) {
            boolean completedPath = activePath.checkPathProgress(player);

            if (completedPath) {
                result = true;
            }
        }

        return result;
    }

    public void doLeaderboardExemptCheck(final Player player) {
        plugin.getPathManager().setLeaderboardExemption(player.getUniqueId(),
                player.hasPermission(AutorankPermission.EXCLUDE_FROM_LEADERBOARD));
    }

    public List<String> formatRequirementsToList(final List<CompositeRequirement> holders,
                                                 final List<CompositeRequirement> metRequirements) {
        // Converts requirements into a list of readable requirements

        final List<String> messages = new ArrayList<String>();

        messages.add(ChatColor.GRAY + " ------------ ");

        for (int i = 0; i < holders.size(); i++) {
            final CompositeRequirement holder = holders.get(i);

            if (holder != null) {
                final StringBuilder message = new StringBuilder("     " + ChatColor.GOLD + (i + 1) + ". ");
                if (metRequirements.contains(holder)) {
                    message.append(ChatColor.GREEN + holder.getDescription() + ChatColor.DARK_AQUA + " ("
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

    public List<String> formatResultsToList(List<AbstractResult> abstractResults) {
        // Converts requirements into a list of readable requirements

        final List<String> messages = new ArrayList<String>();

        messages.add(ChatColor.GRAY + " ------------ ");

        for (int i = 0; i < abstractResults.size(); i++) {
            final AbstractResult abstractResult = abstractResults.get(i);

            if (abstractResult != null) {
                final StringBuilder message = new StringBuilder("     " + ChatColor.GOLD + (i + 1) + ". ");

                message.append(ChatColor.RED + abstractResult.getDescription());

                messages.add(message.toString());

            }
        }

        return messages;

    }

//    /**
//     * Get a list of Requirements that the player completed, given a set of Requirements.
//     * The {@link #getCompletedRequirementsHolders(Player)} uses this method with the requirements of the player's
//     * current path.
//     *
//     * @param holders A list of holders to check.
//     * @param player  Player to check holders for.
//     * @return a subset of the given list of holders that the player completed.
//     */
//    public List<CompositeRequirement> getMetRequirementsHolders(final List<CompositeRequirement> holders, final
//    Player player) {
//        final List<CompositeRequirement> metRequirements = new ArrayList<>();
//
//        boolean onlyOptional = true;
//
//        // Check if we only have optional requirements
//        for (final CompositeRequirement holder : holders) {
//            if (!holder.isOptional())
//                onlyOptional = false;
//        }
//
//        if (onlyOptional) {
//
//            for (final CompositeRequirement holder : holders) {
//                metRequirements.add(holder);
//            }
//
//            return metRequirements;
//        }
//
//        for (final CompositeRequirement holder : holders) {
//            final int reqID = holder.getRequirementId();
//
//            // Use auto completion
//            if (holder.useAutoCompletion()) {
//                // Do auto complete
//                if (holder.meetsRequirement(player, false)) {
//                    // Player meets the requirement -> give him results
//
//                    // Doesn't need to check whether this requirement was
//                    // already done
//                    if (!plugin.getSettingsConfig().usePartialCompletion())
//                        continue;
//
//                    metRequirements.add(holder);
//                    continue;
//                } else {
//
//                    // Only check if player has done this when partial
//                    // completion is used
//                    if (plugin.getSettingsConfig().usePartialCompletion()) {
//                        // Player does not meet requirements, but has done this
//                        // already
//                        if (plugin.getPlayerDataConfig().hasCompletedRequirement(reqID, player.getUniqueId())) {
//                            metRequirements.add(holder);
//                            continue;
//                        }
//                    }
//
//                    // If requirement is optional, we do not check.
//                    if (holder.isOptional()) {
//                        continue;
//                    }
//
//                    // Player does not meet requirements -> do nothing
//                    continue;
//                }
//            } else {
//
//                if (!plugin.getSettingsConfig().usePartialCompletion()) {
//
//                    // Doesn't auto complete and doesn't meet requirement, then
//                    // continue searching
//                    if (!holder.meetsRequirement(player, false)) {
//
//                        // If requirement is optional, we do not check.
//                        if (holder.isOptional()) {
//                            continue;
//                        }
//
//                        continue;
//                    } else {
//                        // Player does meet requirement, continue searching
//                        continue;
//                    }
//
//                }
//
//                // Do not auto complete
//                if (plugin.getPlayerDataConfig().hasCompletedRequirement(reqID, player.getUniqueId())) {
//                    // Player has completed requirement already
//                    metRequirements.add(holder);
//                    continue;
//                } else {
//
//                    // If requirement is optional, we do not check.
//                    if (holder.isOptional()) {
//                        continue;
//                    }
//
//                    continue;
//                }
//            }
//        }
//
//        return metRequirements;
//    }
}