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
 * The command delegator for the '/ar deactivate' command.
 */
public class DeactivateCommand extends AutorankCommand {

    private final Autorank plugin;

    public DeactivateCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // This command will give a preview of a certain path of ranking.
        if (!this.hasPermission(AutorankPermission.DEACTIVATE_PATH, sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.YOU_ARE_A_ROBOT.getConfigValue("you can't deactivate paths, silly.."));
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

        // Check if player is not on this path
        if (!targetPath.isActive(player.getUniqueId())) {
            sender.sendMessage(Lang.PATH_IS_NOT_ACTIVE.getConfigValue(targetPath.getDisplayName()));
            return true;
        }

        plugin.getPathManager().deassignPath(targetPath, player.getUniqueId());

        if (!targetPath.shouldStoreProgressOnDeactivation()) {
            sender.sendMessage(ChatColor.GREEN + "Path '" + targetPath.getDisplayName() + "' is deactivated " +
                    ChatColor.RED + "and your progress for this path has been reset.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Path '" + targetPath.getDisplayName() + "' is deactivated " +
                    ChatColor.GOLD + "but your progress for this path has been stored.");
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

        return plugin.getPathManager().getActivePaths(player.getUniqueId()).stream().map(Path::getDisplayName)
                .collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return "Deactivate a path";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.DEACTIVATE_PATH;
    }

    @Override
    public String getUsage() {
        return "/ar deactivate <path>";
    }

}
