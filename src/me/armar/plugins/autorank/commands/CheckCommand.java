package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The command delegator for the '/ar check' command.
 */
public class CheckCommand extends AutorankCommand {

    private final Autorank plugin;

    public CheckCommand(final Autorank instance) {
        plugin = instance;
    }

    // Show an overview of paths.
    public void showPathsOverview(CommandSender sender, String playerName, UUID uuid) {

        plugin.getPathManager().autoAssignPaths(plugin.getServer().getPlayer(uuid));

        List<Path> activePaths = plugin.getPathManager().getActivePaths(uuid);

        if (activePaths.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + playerName + ChatColor.RED + " does not have any active paths.");
            return;
        }

        sender.sendMessage(String.format(ChatColor.GREEN + "----- " + ChatColor.GRAY + "[Progress of paths for" +
                " " +
                ChatColor.GOLD + "%s" + ChatColor.GRAY + "] " + ChatColor.GREEN + " -----", playerName));

        for (Path activePath : activePaths) {

            StringBuilder message = new StringBuilder(ChatColor.GRAY + "Progress of '" + ChatColor.BLUE + activePath
                    .getDisplayName() + ChatColor.GRAY + "': ");

            int totalRequirements = activePath.getRequirements().size();
            int completedRequirements = activePath.getCompletedRequirements(uuid).size();

            double completeRatio = completedRequirements * 1.0 / totalRequirements * 1.0;

            message.append(ChatColor.GRAY + "[");

            for (int i = 0; i < completeRatio * 10; i++) {
                message.append(ChatColor.GREEN + "|");
            }

            for (int i = 0; i < 10 - completeRatio * 10; i++) {
                message.append(ChatColor.RED + "|");
            }

            message.append(ChatColor.GRAY + "]").append(ChatColor.GOLD + " (").append(new BigDecimal(completeRatio *
                    100).setScale(2, RoundingMode.HALF_UP)).append("%)");

            sender.sendMessage(message.toString());
        }


        sender.sendMessage(ChatColor.GOLD + "To view the progress of a specific path, use /ar check <path name>.");
    }

    // Show specific requirements for a path.
    public void showSpecificPath(CommandSender sender, String playerName, UUID uuid, Path path) {

        // Check player first.
        plugin.getPlayerChecker().checkPlayer(plugin.getServer().getPlayer(uuid));

        sender.sendMessage(ChatColor.DARK_AQUA + "-----------------------");

        sender.sendMessage(ChatColor.DARK_GREEN + "You are viewing the path '" + ChatColor.GOLD + path.getDisplayName
                () + ChatColor.DARK_GREEN + "' for " +
                playerName + ".");

        sender.sendMessage(ChatColor.DARK_AQUA + "-----------------------");

        sender.sendMessage(ChatColor.GRAY + "Requirements:");

        List<CompositeRequirement> allRequirements = path.getRequirements();

        List<CompositeRequirement> completedRequirements = path.getCompletedRequirements(uuid);

        final List<String> messages = plugin.getPlayerChecker().formatRequirementsToList(allRequirements,
                completedRequirements);

        for (final String message : messages) {
            AutorankTools.sendColoredMessage(sender, message);
        }

    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (args.length > 1) {
            // Check a specific path or check player

            boolean showPathsOverview = true;

            String targetPlayerName;
            String targetPathName = null;

            if (args.length > 2) {
                // Sender performed /ar check <player> <path>
                showPathsOverview = false;

                targetPlayerName = args[1];
                targetPathName = AutorankTools.getStringFromArgs(args, 2);

            } else {
                // Sender performed /ar check <player>
                targetPlayerName = args[1];
            }

            // Check if console is performing this command.
            if (targetPlayerName == null) {

                if (!(sender instanceof Player)) {
                    sender.sendMessage("You must specify a player to view path data of!");
                    return true;
                }

                targetPlayerName = sender.getName();
            }

            // Check if the sender is allowed to view other players.
            if (targetPlayerName != null && !targetPlayerName.equalsIgnoreCase(sender.getName())) {
                if (!this.hasPermission(AutorankPermission.CHECK_OTHERS, sender)) {
                    return true;
                }
            }

            // Check to see if the player that was specified was indeed a player or whether we should check for a path.
            if (showPathsOverview && !plugin.getServer().getOfflinePlayer(targetPlayerName).hasPlayedBefore()) {
                // Target player is not a player, so switch to path name instead
                targetPathName = targetPlayerName;
                showPathsOverview = false;
            }

            // If we are showing a path overview, we need a target player.
            if (showPathsOverview && targetPlayerName == null) {
                targetPlayerName = sender.getName();
            }

            // Player does not exist
            if (!showPathsOverview && !plugin.getServer().getOfflinePlayer(targetPlayerName).hasPlayedBefore()) {
                // Player does not exist, but we are expecting a player, so be probably look for a path instead.
                targetPlayerName = sender.getName();
                targetPathName = AutorankTools.getStringFromArgs(args, 1);
            }

            Path targetPath = plugin.getPathManager().findPathByDisplayName(targetPathName, false);

            if (!showPathsOverview && targetPath == null) {
                sender.sendMessage(ChatColor.RED + "Path " + targetPathName + " does not exist.");
                return true;
            }

            final Player player = plugin.getServer().getPlayer(targetPlayerName);

            if (plugin.getServer().getPlayer(targetPlayerName) == null) {
                sender.sendMessage(ChatColor.RED + "The target player must be online to view their data!");
                return true;
            }

            if (AutorankTools.isExcludedFromRanking(player)) {
                sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(player.getName()));
                return true;
            }

            if (!showPathsOverview && !plugin.getPathManager().hasActivePath(player.getUniqueId(), targetPath)) {
                sender.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.RED + " does not have " + ChatColor
                        .GRAY + targetPath.getDisplayName() + ChatColor.RED + " as an active path!");
                return true;
            }

            int time = plugin.getPlayTimeManager().getTimeOfPlayer(targetPlayerName, true);

            AutorankTools.sendColoredMessage(sender,
                    Lang.HAS_PLAYED_FOR.getConfigValue(targetPlayerName, AutorankTools.timeToString(time,
                            AutorankTools.Time.SECONDS)));

            if (showPathsOverview) {
                this.showPathsOverview(sender, player.getName(), player.getUniqueId());
            } else {
                this.showSpecificPath(sender, player.getName(), player.getUniqueId(), targetPath);
            }

        } else if (sender instanceof Player) {
            // Show overview of active paths of the sender
            if (!this.hasPermission(AutorankPermission.CHECK_SELF,
                    sender)) {
                return true;
            }

            if (AutorankTools.isExcludedFromRanking((Player) sender)) {
                sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender.getName()));
                return true;
            }

            final Player player = (Player) sender;

            int time = plugin.getPlayTimeManager().getTimeOfPlayer(player.getName(), true);

            AutorankTools.sendColoredMessage(sender,
                    Lang.HAS_PLAYED_FOR.getConfigValue(player.getName(), AutorankTools.timeToString(time,
                            AutorankTools.Time.SECONDS)));

            this.showPathsOverview(sender, player.getName(), player.getUniqueId());
        } else {
            // We cannot check paths of console.
            AutorankTools.sendColoredMessage(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        // If the sender is not a player, just return all online players.
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        // Return the name of the paths that the player has active.
        return plugin.getPathManager().getActivePaths(player.getUniqueId
                ()).stream().map(Path::getDisplayName).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String getDescription() {
        return "Check [player]'s status";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.CHECK_SELF;
    }

    @Override
    public String getUsage() {
        return "/ar check <player> <path>";
    }
}
