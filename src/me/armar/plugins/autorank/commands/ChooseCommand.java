package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The command delegator for the '/ar choose' command.
 */
public class ChooseCommand extends AutorankCommand {

    private final Autorank plugin;

    public ChooseCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // This command will give a preview of a certain path of ranking.
        if (!this.hasPermission(AutorankPermission.CHOOSE_PATH, sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.YOU_ARE_A_ROBOT.getConfigValue("you can't choose ranking paths, silly.."));
            return true;
        }

        final Player player = (Player) sender;

        final String pathName = AutorankTools.getStringFromArgs(args, 1);

        // Try to find path that matches given name
        Path targetPath = plugin.getPathManager().findPathByDisplayName(pathName, false);

        // Check if the path exists
        if (targetPath == null) {
            sender.sendMessage(Lang.NO_PATH_FOUND_WITH_THAT_NAME.getConfigValue());
            return true;
        }

        // Check if player is already on this path
        if (targetPath.isActive(player.getUniqueId())) {
            sender.sendMessage(Lang.ALREADY_ON_THIS_PATH.getConfigValue());
            return true;
        }

        // Check if player can retake this path if it has already been completed before.
        if (targetPath.hasCompletedPath(player.getUniqueId()) && !targetPath.isRepeatable()) {
            sender.sendMessage(Lang.PATH_NOT_ALLOWED_TO_RETAKE.getConfigValue());
            return true;
        }

        // Check if the path is eligible.
        if (!targetPath.meetsPrerequisites(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You do not meet the prerequisites of this path!");
            sender.sendMessage(ChatColor.RED + "Type " + ChatColor.GOLD + "/ar view "
                    + targetPath.getDisplayName() + ChatColor.RED + " to see a list of prerequisites.");
            return true;
        }

        // Assign path to the player.
        plugin.getPathManager().assignPath(targetPath, player.getUniqueId());

        // Give player confirmation message.
        sender.sendMessage(Lang.CHOSEN_PATH.getConfigValue(targetPath.getDisplayName()));

        if (!targetPath.shouldStoreProgressOnDeactivation()) {
            sender.sendMessage(Lang.PROGRESS_RESET.getConfigValue());
        } else {
            sender.sendMessage(Lang.PROGRESS_RESTORED.getConfigValue());
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

        // If the sender is not a player, just return all paths
        if (!(sender instanceof Player)) {
            return plugin.getPathManager().getAllPaths().stream().map(Path::getDisplayName).collect(Collectors.toList
                    ());
        }

        final Player player = (Player) sender;

        // Return the name of the paths that the player has active.
        return plugin.getPathManager().getEligiblePaths(player.getUniqueId()).stream().map(Path::getDisplayName).collect(Collectors
                .toList());
    }

    @Override
    public String getDescription() {
        return "Activate a path";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.CHOOSE_PATH;
    }

    @Override
    public String getUsage() {
        return "/ar choose <path>";
    }

}
