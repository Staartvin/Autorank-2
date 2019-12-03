package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.resetcommand.ResetConversationType;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The command delegator for the '/ar reset' command.
 */
public class ResetCommand extends AutorankCommand {

    private final Autorank plugin;

    public ResetCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!this.hasPermission(AutorankPermission.RESET_DATA, sender)) {
            return true;
        }

        AutorankConversation resetConversation = AutorankConversation.fromFirstPrompt(new ResetConversationType());

        resetConversation.afterConversationEnded(result -> {

            if (!result.wasSuccessful()) return;

            String playerName = result.getStorageString("playerName");
            String resetType = result.getStorageString(ResetConversationType.RESET_TYPE);

            UUID uuid = plugin.getUUIDStorage().getStoredUUID(playerName);

            if (uuid == null) {
                sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(playerName));
                return;
            }

            if (resetType.equalsIgnoreCase(ResetConversationType.RESET_PROGRESS)) {
                // Reset progress of active paths.
                plugin.getPathManager().resetProgressOnActivePaths(uuid);
                sender.sendMessage(ChatColor.GREEN + "Reset progress on all (active) paths of " + ChatColor.YELLOW +
                        playerName);
            } else if (resetType.equalsIgnoreCase(ResetConversationType.RESET_ACTIVE_PATHS)) {
                plugin.getPathManager().resetActivePaths(uuid);
                sender.sendMessage(ChatColor.GREEN + "Removed all active paths of " + ChatColor.YELLOW + playerName);
            } else if (resetType.equalsIgnoreCase(ResetConversationType.RESET_COMPLETED_PATHS)) {
                plugin.getPathManager().resetCompletedPaths(uuid);
                sender.sendMessage(ChatColor.GREEN + "Removed all completed paths of " + ChatColor.YELLOW + playerName);
            }


        });

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> resetConversation.startConversationAsSender(sender));

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
                                      final String[] args) {

        if (args.length == 2) {
            return null;
        } else {
            return Arrays.asList("progress", "activepaths", "completedpaths");
        }
    }

    @Override
    public String getDescription() {
        return "Reset certain storage of a player";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.RESET_DATA;
    }

    @Override
    public String getUsage() {
        return "/ar reset <player> <action>";
    }
}
