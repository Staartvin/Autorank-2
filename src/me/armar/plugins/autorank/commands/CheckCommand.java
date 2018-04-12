package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.CheckCommandEvent;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * The command delegator for the '/ar check' command.
 */
public class CheckCommand extends AutorankCommand {

    private final Autorank plugin;

    public CheckCommand(final Autorank instance) {
        plugin = instance;
    }

    public void check(final CommandSender sender, final Player player) {
        // Call event to let other plugins know that a player wants to check
        // itself.
        // Create the event here
        final CheckCommandEvent event = new CheckCommandEvent(player);
        // Call the event
        Bukkit.getServer().getPluginManager().callEvent(event);

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

        // Check if event is cancelled.
        if (event.isCancelled())
            return;

        Path activePath = plugin.getPathManager().getCurrentPath(uuid);

        if (activePath == null) {
            // Try assign a path to a player first.
            activePath = plugin.getPathManager().autoAssignPath(player);
        }

        // No path assigned to the player.
        if (activePath == null) {
            // Player should first choose a path.

            final List<Path> paths = plugin.getPathManager().getPaths();

            // Remove paths that have already been completed by the
            // user.
            for (Iterator<Path> iterator = paths.iterator(); iterator.hasNext(); ) {
                Path path = iterator.next();

                // If this path can be done over and over again, we obviously
                // don't want to remove it.
                if (plugin.getPathsConfig().allowInfinitePathing(path.getInternalName())) {
                    continue;
                }

                // Remove it if player already completed the path
                if (plugin.getPlayerDataConfig().hasCompletedPath(uuid, path.getInternalName())) {
                    iterator.remove();
                }
            }

            if (paths.isEmpty()) {
                sender.sendMessage(Lang.NO_PATH_LEFT_TO_CHOOSE.getConfigValue(sender.getName(), AutorankTools
                        .timeToString(plugin.getPlayTimeManager().getTimeOfPlayer(player.getName(), true), Time
                                .SECONDS)));

                return;
            }

            sender.sendMessage(ChatColor.BLACK + "-------------------------------------");
            sender.sendMessage(ChatColor.BLUE + "There are multiple ranking paths, please choose one with "
                    + ChatColor.RED + "'/ar choose'" + ChatColor.BLUE + ".");
            sender.sendMessage(
                    ChatColor.GREEN + "You can always change later if you want, but you'll lose your progress.");
            sender.sendMessage(ChatColor.BLUE + "To check what each path looks like, use " + ChatColor.RED
                    + "'/ar view'" + ChatColor.DARK_BLUE + ".");
            sender.sendMessage(ChatColor.YELLOW + "You can see a list of paths with '" + ChatColor.RED + "/ar view list"
                    + ChatColor.YELLOW + "'.");
            sender.sendMessage(ChatColor.BLACK + "-------------------------------------");
            return;

        }

        // Get display name of Path
        String displayName = activePath.getDisplayName();

        if (displayName == null) {
            displayName = "Unknown path name";
        }

        // Start building layout

        String layout = plugin.getSettingsConfig().getCheckCommandLayout();

        layout = layout.replace("&path", displayName);
        layout = layout.replace("&p", player.getName());
        layout = layout.replace("&time", AutorankTools
                .timeToString(plugin.getPlayTimeManager().getTimeOfPlayer(player.getName(), true), Time.SECONDS));
        layout = layout.replace("&globaltime",
                AutorankTools.timeToString(plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid),
                        Time
                        .MINUTES));

        boolean showReqs = false;

        List<RequirementsHolder> holders = activePath.getRequirements();

        if (holders == null || holders.size() == 0) {
            layout = layout.replace("&reqs", Lang.NO_FURTHER_PATH_FOUND.getConfigValue());
        } else {
            layout = layout.replace("&reqs", "");
            showReqs = true;
        }

        // Send layout to player

        AutorankTools.sendColoredMessage(sender, layout);

        // Don't get requirements when the player has no new requirements
        if (!showReqs)
            return;

        //boolean onlyOptional = true;
        boolean meetsAllRequirements = false;

        // Get a list of completed requirements.
        List<RequirementsHolder> completedRequirements = plugin.getPlayerChecker().getCompletedRequirementsHolders(player);

        // Player completed all requirements of his path
        if (completedRequirements.size() == plugin.getPlayerChecker().getAllRequirementsHolders(player).size()) {
            meetsAllRequirements = true;
        }

        String reqMessage2 = "";

        if (plugin.getPlayerDataConfig().hasCompletedPath(uuid, activePath.getInternalName())) {
            reqMessage2 = " " + Lang.ALREADY_COMPLETED_PATH.getConfigValue();
        } else {
            reqMessage2 = Lang.COMPLETED_PATH_NOW.getConfigValue();
        }

        // Player meets all requirements
        if (meetsAllRequirements /*|| onlyOptional*/) {
            AutorankTools.sendColoredMessage(sender, Lang.MEETS_ALL_REQUIREMENTS.getConfigValue(displayName) + reqMessage2);
        } else {
            // Player does not meet all requirements, so show which requirements he does not meet yet.

            // Show requirements list
            final List<String> messages = plugin.getPlayerChecker().formatRequirementsToList(holders,
                    completedRequirements);

            for (final String message : messages) {
                AutorankTools.sendColoredMessage(sender, message);
            }

        }

        // Check player again.
        plugin.getPlayerChecker().checkPlayer(player);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // This is a local check. It will not show you the database numbers
        if (args.length > 1) {

            if (!this.hasPermission(AutorankPermission.CHECK_OTHERS,
                    sender)) {
                return true;
            }

            final Player player = plugin.getServer().getPlayer(args[1]);
            if (player == null) {

                final int time = plugin.getPlayTimeManager().getTimeOfPlayer(args[1], true);

                if (time <= 0) {
                    sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                    return true;
                }

                final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

                if (plugin.getUUIDStorage().hasRealName(uuid)) {
                    args[1] = plugin.getUUIDStorage().getRealName(uuid);
                }

                AutorankTools.sendColoredMessage(sender,
                        Lang.HAS_PLAYED_FOR.getConfigValue(args[1], AutorankTools.timeToString(time, Time.SECONDS)));
            } else {
                if (AutorankTools.isExcludedFromRanking(player)) {
                    sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(player.getName()));
                    return true;
                }
                check(sender, player);
            }
        } else if (sender instanceof Player) {
            if (!this.hasPermission(AutorankPermission.CHECK_SELF,
                    sender)) {
                return true;
            }

            if (AutorankTools.isExcludedFromRanking((Player) sender)) {
                sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender.getName()));
                return true;
            }
            final Player player = (Player) sender;
            check(sender, player);
        } else {
            AutorankTools.sendColoredMessage(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
        }
        return true;
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
        return "/ar check [player]";
    }
}
