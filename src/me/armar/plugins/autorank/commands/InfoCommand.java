package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * The command delegator for the '/ar info' command.
 */
public class InfoCommand extends AutorankCommand {

    private final Autorank plugin;

    public InfoCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        // Sender is requesting their own information.
        if (args[1].equalsIgnoreCase(sender.getName())) {

            // Check if the console is sending this.
            if (!(sender instanceof Player)) {
                sender.sendMessage(Lang.CANNOT_CHECK_CONSOLE.getDefault());
                return true;
            }

            if (!this.hasPermission(AutorankPermission.VIEW_INFO_SELF, sender)) return true;
        } else {
            if (!this.hasPermission(AutorankPermission.VIEW_INFO_OTHERS, sender)) return true;
        }

        CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept(uuid -> {

            if (uuid == null) {
                sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));

                return;
            }

            String playerName = null;

            try {
                playerName = UUIDManager.getPlayerName(uuid).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            // Send info of player to the sender.
            sender.sendMessage(ChatColor.DARK_AQUA + "---------- [" + ChatColor.GOLD + playerName + ChatColor.DARK_AQUA + "] " +
                    "----------");

            List<Path> activePaths = plugin.getPathManager().getActivePaths(uuid);
            List<Path> completedPaths = plugin.getPathManager().getCompletedPaths(uuid);

            boolean isExemptedFromLeaderboard = plugin.getPathManager().hasLeaderboardExemption(uuid);

            int localTotalTime = plugin.getPlayTimeManager().getLocalPlayTime(TimeType.TOTAL_TIME, uuid);
            int globalTotalTime = plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid);

            StringBuilder activePathsString = new StringBuilder();

            for (int i = 0; i < activePaths.size(); i++) {

                long pathProgress = Math.round(activePaths.get(i).getProgress(uuid) * 100);

                String progressString = null;

                if (pathProgress <= 35) {
                    progressString = ChatColor.RED + "" + pathProgress;
                } else if (pathProgress <= 70) {
                    progressString = ChatColor.YELLOW + "" + pathProgress;
                } else {
                    progressString = ChatColor.GREEN + "" + pathProgress;
                }


                if (i == (activePaths.size() - 1)) {
                    activePathsString.append(ChatColor.DARK_AQUA + activePaths.get(i).toString() + " (" + progressString +
                            ChatColor.DARK_AQUA + "%)");
                } else if (i == (activePaths.size() - 2)) {
                    // Second last
                    activePathsString.append(ChatColor.DARK_AQUA + activePaths.get(i).toString() + " (" + progressString + ChatColor.DARK_AQUA + "%)" + " and ");
                } else {
                    activePathsString.append(ChatColor.DARK_AQUA + activePaths.get(i).toString() + " (" + progressString + ChatColor.DARK_AQUA +
                            "%), ");
                }
            }

            StringBuilder completedPathsString = new StringBuilder();

            for (int i = 0; i < completedPaths.size(); i++) {

                String progressString = ChatColor.YELLOW + "" + completedPaths.get(i).getTimesCompleted(uuid);

                if (i == (completedPaths.size() - 1)) {
                    completedPathsString.append(ChatColor.DARK_AQUA + completedPaths.get(i).toString() + " (" + progressString +
                            ChatColor.DARK_AQUA + ")");
                } else if (i == (completedPaths.size() - 2)) {
                    // Second last
                    completedPathsString.append(ChatColor.DARK_AQUA + completedPaths.get(i).toString() + " (" + progressString + ChatColor.DARK_AQUA + ")" + " and ");
                } else {
                    completedPathsString.append(ChatColor.DARK_AQUA + completedPaths.get(i).toString() + " (" + progressString + ChatColor.DARK_AQUA +
                            "), ");
                }
            }

            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Active paths (" + ChatColor.GOLD + activePaths.size() + ChatColor.LIGHT_PURPLE + "): " + (activePathsString.length() == 0 ? "none" : activePathsString.toString()));
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Completed paths (" + ChatColor.GOLD + completedPaths.size() + ChatColor.LIGHT_PURPLE + "): " + (completedPathsString.length() == 0 ? "none" : completedPathsString.toString()));

            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Is exempted from leaderboard: " + (isExemptedFromLeaderboard
                    ? ChatColor.GREEN : ChatColor.RED) + isExemptedFromLeaderboard);

            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Local playtime: " + ChatColor.GOLD +
                    (localTotalTime <= 0 ? "none" : AutorankTools.timeToString(localTotalTime,
                            AutorankTools.Time.MINUTES)));
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Global playtime: " + ChatColor.GOLD +
                    (globalTotalTime <= 0 ? "none" : AutorankTools.timeToString(globalTotalTime,
                            AutorankTools.Time.MINUTES)));

        });

        return true;
    }

    @Override
    public String getDescription() {
        return "Show info of a player";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.VIEW_INFO_SELF;
    }

    @Override
    public String getUsage() {
        return "/ar info <player>";
    }
}
