package me.armar.plugins.autorank.placeholders;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AutorankPlaceholder extends PlaceholderExpansion {

    private final Autorank plugin;

    public AutorankPlaceholder(Autorank instance) {
        this.plugin = instance;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "autorank";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Staartvin";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if (params.equalsIgnoreCase("total_time_of_player")) {
            try {
                return plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME, player.getUniqueId()).get() + "";
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain total time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("total_time_of_player_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME,
                        player.getUniqueId(), TimeUnit.MINUTES).get()), TimeUnit.MINUTES);
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain total time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("daily_time_of_player")) {
            try {
                return plugin.getPlayTimeManager().getPlayTime(TimeType.DAILY_TIME, player.getUniqueId()).get() + "";
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain daily time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("daily_time_of_player_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(plugin.getPlayTimeManager().getPlayTime(TimeType.DAILY_TIME,
                        player.getUniqueId(), TimeUnit.MINUTES).get()), TimeUnit.MINUTES);
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain daily time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("weekly_time_of_player")) {
            try {
                return plugin.getPlayTimeManager().getPlayTime(TimeType.WEEKLY_TIME, player.getUniqueId()).get() + "";
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain weekly time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("weekly_time_of_player_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(plugin.getPlayTimeManager().getPlayTime(TimeType.WEEKLY_TIME,
                        player.getUniqueId(), TimeUnit.MINUTES).get()), TimeUnit.MINUTES);
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain weekly time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("monthly_time_of_player")) {
            try {
                return plugin.getPlayTimeManager().getPlayTime(TimeType.MONTHLY_TIME, player.getUniqueId()).get() + "";
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain monthly time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("monthly_time_of_player_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(plugin.getPlayTimeManager().getPlayTime(TimeType.MONTHLY_TIME,
                        player.getUniqueId(), TimeUnit.MINUTES).get()), TimeUnit.MINUTES);
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain monthly time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("local_time")) {
            try {
                return plugin.getPlayTimeManager().getLocalPlayTime(TimeType.DAILY_TIME, player.getUniqueId()).get() + "";
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain local time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("local_time_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(plugin.getPlayTimeManager().getLocalPlayTime(TimeType.DAILY_TIME,
                        player.getUniqueId()).get()), TimeUnit.MINUTES);
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain local time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("global_time")) {
            try {
                return plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.DAILY_TIME, player.getUniqueId()).get() + "";
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain global time of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("global_time_formatted")) {
            try {
                return AutorankTools.timeToString(Math.toIntExact(plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.DAILY_TIME,
                        player.getUniqueId()).get()), TimeUnit.MINUTES);
            } catch (InterruptedException | ExecutionException e) {
                return "Couldn't obtain global time (formatted) of " + player.getName();
            }
        } else if (params.equalsIgnoreCase("completed_paths")) {
            return plugin.getAPI().getCompletedPaths(player.getUniqueId())
                    .stream().map(Path::getDisplayName).collect(Collectors.joining(","));
        } else if (params.equalsIgnoreCase("active_paths")) {
            return plugin.getAPI().getActivePaths(player.getUniqueId())
                    .stream().map(Path::getDisplayName).collect(Collectors.joining(","));
        } else if (params.equalsIgnoreCase("eligible_paths")) {
            return plugin.getAPI().getEligiblePaths(player.getUniqueId())
                    .stream().map(Path::getDisplayName).collect(Collectors.joining(","));
        }

        // Return null as default because it does not seem to be a valid placeholder.
        return null;
    }
}
