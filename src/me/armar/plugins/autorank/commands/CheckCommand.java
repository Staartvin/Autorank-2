package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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

        // First try to assign paths to the player.
        plugin.getPathManager().autoAssignPaths(uuid);

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

            double completeRatio = activePath.getProgress(uuid);

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

        // Check whether the player completed any current paths.
        plugin.getPlayerChecker().checkPlayer(uuid);

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

        // Possibilities:
        // 1. /ar check
        // 2. /ar check <player>
        // 3. /ar check <path>
        // 4. /ar check <player> <path>

        boolean showListOfPaths = false;
        Path targetPath = null;
        OfflinePlayer targetPlayer = null;

        // ---- Read what kind of command should be performed based on the input ----

        if (args.length == 1) {
            // Possibility 1: /ar check.

            // Check if it is send from console.
            if (!(sender instanceof Player)) {
                // We cannot check paths of console.
                sender.sendMessage(ChatColor.RED + "You should specify a player to check.");
                return true;
            }

            // Check if player is allowed to check their own paths
            if (!this.hasPermission(AutorankPermission.CHECK_SELF,
                    sender)) {
                return true;
            }

            // Check if player is excluded from ranking
            if (plugin.getPlayerChecker().isExemptedFromAutomaticChecking(((Player) sender).getUniqueId())) {
                sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender.getName()));
                return true;
            }

            final Player player = (Player) sender;

            int time = plugin.getPlayTimeManager().getTimeOfPlayer(player.getName(), true);

            AutorankTools.sendColoredMessage(sender,
                    Lang.HAS_PLAYED_FOR.getConfigValue(player.getName(), AutorankTools.timeToString(time,
                            AutorankTools.Time.SECONDS)));

            this.showPathsOverview(sender, player.getName(), player.getUniqueId());

            return true;
        }

        // Other possibilities:
        // 2. /ar check <player>
        // 3. /ar check <path>
        // 4. /ar check <player> <path>

        else if (args.length >= 2) {
            // Possibility 2 or 3:

            // For length = 2
            // /ar check <player>
            // /ar check <path> (if path is a single name)

            // For length > 2
            // /ar check <player> <path>
            // /ar check <path>

            boolean isPath = false;
            boolean isPlayer = false;

            // Check if the argument could be a player
            targetPlayer = plugin.getServer().getOfflinePlayer(args[1].trim());

            // Check if player is valid
            if (targetPlayer.hasPlayedBefore()) {
                isPlayer = true;
            }

            // Check if we also have a path besides the player.
            if (isPlayer && args.length > 2) {
                // Check if the argument could be a path
                targetPath = plugin.getPathManager().findPathByDisplayName(
                        AutorankTools.getStringFromArgs(args, 2).trim(), false);
            } else {
                // We must have a path.
                targetPath =
                        plugin.getPathManager().findPathByDisplayName(AutorankTools.getStringFromArgs(args, 1).trim(),
                                false);
            }

            if (targetPath != null) {
                isPath = true;
            }

            if (isPath) { // If it is a path, we want to specifically show that path
                showListOfPaths = false;

                // If we don't have a playername, we should use ourselves.
                if (!isPlayer) {

                    // If the sender is a console, we cannot check that.
                    if (!(sender instanceof Player)) {
                        // We cannot check paths of console.
                        AutorankTools.sendColoredMessage(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
                        return true;
                    }

                    targetPlayer = (OfflinePlayer) sender;
                }
            } else if (isPlayer) { // If it is a player, we show an overview of the path
                showListOfPaths = true;
            } else {
                // The argument was not a player and not a path
                sender.sendMessage(ChatColor.RED + "There is no player or path named '" + args[1] + "'.");
                return true;
            }
        }

        // ---- Perform checks after it has been decided what action should be performed ----

        // Player is looking up themselves
        if (targetPlayer.getName().equalsIgnoreCase(sender.getName())) {
            // Check if player is allowed to check their own paths
            if (!this.hasPermission(AutorankPermission.CHECK_SELF,
                    sender)) {
                return true;
            }
        } else {
            // Player is looking up someone else

            // Check if player is allowed to check their other paths
            if (!this.hasPermission(AutorankPermission.CHECK_OTHERS,
                    sender)) {
                return true;
            }

//            // Check if the target player is online
//            if (targetPlayer.getPlayer() == null) {
//                sender.sendMessage(Lang.PLAYER_NOT_ONLINE.getConfigValue(targetPlayer.getName()));
//                return true;
//            }
        }

        boolean isTargetOnline = targetPlayer.isOnline();

        String targetPlayerName = null;
        UUID targetUUID = null;

        if (isTargetOnline) {
            // If the target is online, find their UUID and name
            Player onlineTargetPlayer = targetPlayer.getPlayer();

            // Check if player is excluded from ranking
            if (plugin.getPlayerChecker().isExemptedFromAutomaticChecking(onlineTargetPlayer.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(onlineTargetPlayer.getName()));
                return true;
            }

            targetPlayerName = onlineTargetPlayer.getName();
            targetUUID = onlineTargetPlayer.getUniqueId();
        } else {

            // The target is not online, so we used the stored data of the UUID and player name.

            try {
                targetUUID = UUIDManager.getUUID(targetPlayer.getName()).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            targetPlayerName = targetPlayer.getName();
        }

        int time = plugin.getPlayTimeManager().getTimeOfPlayer(targetPlayerName, true);

        AutorankTools.sendColoredMessage(sender,
                Lang.HAS_PLAYED_FOR.getConfigValue(targetPlayerName, AutorankTools.timeToString(time,
                        AutorankTools.Time.SECONDS)));

        if (showListOfPaths) {
            // We will show a list of paths
            // Show overview of paths
            this.showPathsOverview(sender, targetPlayerName, targetUUID);
        } else {
            // We will show a specific path of a player to the sender

            // Check if the path the player wants to check is active
            if (targetPath != null && !targetPath.isActive(targetUUID)) {
                sender.sendMessage(ChatColor.GOLD + targetPlayerName + ChatColor.RED + " does not have "
                        + ChatColor
                        .GRAY + targetPath.getDisplayName() + ChatColor.RED + " as an active path!");
                return true;
            }

            this.showSpecificPath(sender, targetPlayerName, targetUUID, targetPath);
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
