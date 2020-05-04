package me.armar.plugins.autorank.playerchecker;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public boolean checkPlayer(UUID uuid) {

        // Do not rank a player when he is excluded
        if (plugin.getPlayerChecker().isExemptedFromAutomaticChecking(uuid)) {
            plugin.debugMessage("Player '" + uuid.toString() + "' is exempted from automated checking, so we don't " +
                    "check their path progress!");
            return false;
        }

        // Try to assign paths to a player automatically.
        plugin.getPathManager().autoAssignPaths(uuid);

        // Get active paths
        List<Path> activePaths = plugin.getPathManager().getActivePaths(uuid);

        boolean result = false;

        // For all paths, check the progress of the player.
        for (Path activePath : activePaths) {
            if (activePath.checkPathProgress(uuid)) {
                result = true;
            } else {

            }
        }

        return result;
    }

    public void doOfflineExemptionChecks(Player player) {
        this.doLeaderboardExemptCheck(player);
        this.doAutomaticCheckingExemptionCheck(player);
        this.doTimeAdditionExemptionCheck(player);
    }

    public void doLeaderboardExemptCheck(final Player player) {
        plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent(s -> s.setLeaderboardExemption(player.getUniqueId(),
                player.hasPermission(AutorankPermission.EXCLUDE_FROM_LEADERBOARD)));
    }

    public void doAutomaticCheckingExemptionCheck(Player player) {
        plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent(s -> s.setAutoCheckingExemption(player.getUniqueId(),
                AutorankTools.isExcludedFromRanking(player)));
    }

    public void doTimeAdditionExemptionCheck(Player player) {
        plugin.getPlayerDataManager().getPrimaryDataStorage().ifPresent(s -> s.setTimeAdditionExemption(player.getUniqueId(),
                player.hasPermission(AutorankPermission.EXCLUDE_FROM_TIME_UPDATES)));
    }

    /**
     * Check whether a player is exempted from showing up on the leaderboard.
     *
     * @param uuid UUID of the player
     * @return true if the player should not be shown on the leaderboard, false otherwise.
     */
    public boolean isExemptedFromLeaderboard(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();

        // If player is online, check their permission.
        if (player != null) {
            return player.hasPermission(AutorankPermission.EXCLUDE_FROM_LEADERBOARD);
        }

        return plugin.getPlayerDataManager().getPrimaryDataStorage().map(s -> s.hasLeaderboardExemption(uuid)).orElse(false);
    }

    /**
     * Check whether a player is exempted from checking (both automatic and manual) of paths and the progress.
     *
     * @param uuid UUID of the player
     * @return true if the player is excluded and hence may not be checked., false otherwise.
     */
    public boolean isExemptedFromAutomaticChecking(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();

        // If player is online, check their permission.
        if (player != null) {
            return AutorankTools.isExcludedFromRanking(player);
        }

        return plugin.getPlayerDataManager().getPrimaryDataStorage().map(s -> s.hasAutoCheckingExemption(uuid)).orElse(false);
    }

    /**
     * Check whether a player is exempted from obtaining any time. If this method returns true, Autorank will not
     * (automatically) add time to the player.
     *
     * @param uuid UUID of the player
     * @return true if the player will not gain any time, false otherwise.
     */
    public boolean isExemptedFromTimeAddition(UUID uuid) {
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();

        // If player is online, check their permission.
        if (player != null) {
            return player.hasPermission(AutorankPermission.EXCLUDE_FROM_TIME_UPDATES);
        }

        return plugin.getPlayerDataManager().getPrimaryDataStorage().map(s -> s.hasTimeAdditionExemption(uuid)).orElse(false);
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
                    message.append(ChatColor.GREEN).append(holder.getDescription()).append(ChatColor.DARK_AQUA)
                            .append(" (").append(Lang.DONE_MARKER.getConfigValue()).append(")");
                } else {
                    message.append(ChatColor.RED).append(holder.getDescription());
                }

                if (holder.isOptional()) {
                    message.append(ChatColor.AQUA + " (").append(Lang.OPTIONAL_MARKER.getConfigValue()).append(")");
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
}