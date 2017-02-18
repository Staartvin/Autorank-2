package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.pathbuilder.result.Result;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * The command delegator for the '/ar view' command.
 */
public class ViewCommand extends AutorankCommand {

    private final Autorank plugin;

    public ViewCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // This command will give a preview of a certain path of ranking.
        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.VIEW_PATH, sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar view <path name> or /ar view list"));
            return true;
        }

        String pathName;

        boolean isPlayer = false;

        // Check if sender is player or console
        if (sender instanceof Player) {
            isPlayer = true;
        }

        // /ar view list (or a name of a path)
        if (args.length == 2) {

            pathName = AutorankTools.getStringFromArgs(args, 1);

            // Get a list of possible paths that a player can take?
            if (pathName.equals("list")) {

                final List<Path> paths = plugin.getPathManager().getPaths();

                if (isPlayer) {
                    UUID uuid = ((Player) sender).getUniqueId();

                    // Remove paths that have already been completed by the
                    // user.
                    for (Iterator<Path> iterator = paths.iterator(); iterator.hasNext();) {
                        Path path = iterator.next();

                        // If this path can be done over and over again, we
                        // obviously don't want to remove it.
                        if (plugin.getPathsConfig().allowInfinitePathing(path.getInternalName())) {
                            continue;
                        }

                        // Remove it if player already completed the path
                        if (plugin.getPlayerDataConfig().hasCompletedPath(uuid, path.getInternalName())) {
                            iterator.remove();
                        }

                        // Remove path from list if this path can only be shown
                        // when a player meets the path's prerequisites (and the
                        // player does not match the prerequisites).
                        if (plugin.getPathsConfig().showBasedOnPrerequisites(path.getInternalName())
                                && !plugin.getPathManager().matchPathbyInternalName(path.getInternalName(), false)
                                        .meetsPrerequisites((Player) sender)) {
                            iterator.remove();
                        }
                    }
                }

                if (paths.isEmpty()) {
                    sender.sendMessage(Lang.NO_PATHS_TO_CHOOSE.getConfigValue());
                    return true;
                }

                sender.sendMessage(ChatColor.GREEN + "You can choose these paths: ");

                final String pathsString = AutorankTools.createStringFromList(paths);
                sender.sendMessage(ChatColor.WHITE + pathsString);
                return true;
            } else {
                // Third argument is probably a name of a path

                // Show details of path

                Path targetPath = plugin.getPathManager().matchPathbyDisplayName(pathName, false);

                if (targetPath == null) {
                    sender.sendMessage(Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                    return true;
                }

                sender.sendMessage(ChatColor.GREEN
                        + "You can preview requirements (reqs), prerequisites (prereqs) or results (res) of this path.");
                sender.sendMessage(ChatColor.GOLD + "To view these, perform " + ChatColor.AQUA
                        + "/ar view reqs/prereqs/res " + targetPath.getDisplayName());

                return true;

            }

        } else if (args.length > 2) {
            // /ar view (req/prereq/result) (name of path)
            String viewType = args[1];

            if (!viewType.contains("prereq") && !viewType.contains("req") && !viewType.contains("res")) {
                pathName = AutorankTools.getStringFromArgs(args, 1);
                viewType = null;
            } else {
                pathName = AutorankTools.getStringFromArgs(args, 2);

                viewType = args[1];
            }

            Path targetPath = plugin.getPathManager().matchPathbyDisplayName(pathName, false);

            if (targetPath == null) {
                sender.sendMessage(Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
                return true;
            }

            if (viewType == null) {
                sender.sendMessage(
                        Lang.INVALID_FORMAT.getConfigValue("/ar view reqs/prereqs/res " + targetPath.getDisplayName()));
                return true;
            }

            if (viewType.contains("prereq")) {

                List<RequirementsHolder> holders = targetPath.getPrerequisites();

                // Set messages depending on console or player
                List<String> messages = (isPlayer
                        ? plugin.getPlayerChecker().formatRequirementsToList(holders,
                                plugin.getPlayerChecker().getMetRequirementsHolders(holders, (Player) sender))
                        : plugin.getPlayerChecker().formatRequirementsToList(holders, new ArrayList<Integer>()));

                sender.sendMessage(ChatColor.GREEN + "Prerequisites of path '" + ChatColor.GRAY
                        + targetPath.getDisplayName() + ChatColor.GREEN + "':");

                for (final String message : messages) {
                    AutorankTools.sendColoredMessage(sender, message);
                }

                return true;

            } else if (viewType.contains("res")) {

                List<Result> results = targetPath.getResults();

                // Set messages depending on console or player
                List<String> messages = plugin.getPlayerChecker().formatResultsToList(results);

                sender.sendMessage(ChatColor.GREEN + "Results of path '" + ChatColor.GRAY + targetPath.getDisplayName()
                        + ChatColor.GREEN + "':");

                for (final String message : messages) {
                    AutorankTools.sendColoredMessage(sender, message);
                }

                return true;
            } else {
                List<RequirementsHolder> holders = targetPath.getRequirements();

                // Set messages depending on console or player
                List<String> messages = (isPlayer
                        ? plugin.getPlayerChecker().formatRequirementsToList(holders,
                                plugin.getPlayerChecker().getMetRequirementsHolders(holders, (Player) sender))
                        : plugin.getPlayerChecker().formatRequirementsToList(holders, new ArrayList<Integer>()));

                sender.sendMessage(ChatColor.GREEN + "Requirements of path '" + ChatColor.GRAY
                        + targetPath.getDisplayName() + ChatColor.GREEN + "':");

                for (final String message : messages) {
                    AutorankTools.sendColoredMessage(sender, message);
                }

                return true;
            }

        }
        return true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(
     * org.bukkit.command.CommandSender, org.bukkit.command.Command,
     * java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
            final String[] args) {
        final List<String> possibilities = new ArrayList<String>();

        // List shows a list of changegroups to view
        possibilities.add("list");

        for (final Path path : plugin.getPathManager().getPaths()) {
            possibilities.add(path.getDisplayName());
        }

        return possibilities;
    }

    @Override
    public String getDescription() {
        return "Gives a preview of a certain ranking path";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.VIEW_PATH;
    }

    @Override
    public String getUsage() {
        return "/ar view <path name>";
    }

}
