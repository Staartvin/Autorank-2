package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.holders.RequirementsHolder;

/**
 * The command delegator for the '/ar complete' command.
 */
public class CompleteCommand extends AutorankCommand {

    private final Autorank plugin;

    public CompleteCommand(final Autorank instance) {
        this.setUsage("/ar complete #");
        this.setDesc("Complete a requirement at this moment");
        this.setPermission("autorank.complete");

        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // Implemented /ar complete #
        if (args.length != 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar complete #"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.YOU_ARE_A_ROBOT.getConfigValue("you can't rank up, silly.."));
            return true;
        }

        if (!plugin.getConfigHandler().usePartialCompletion()) {
            sender.sendMessage(Lang.PARTIAL_COMPLETION_NOT_ENABLED.getConfigValue());
            return true;
        }

        if (!plugin.getCommandsManager().hasPermission("autorank.complete", sender))
            return true;

        final Player player = (Player) sender;

        int completionID = 0;

        try {
            completionID = Integer.parseInt(args[1]);

            if (completionID < 1) {
                completionID = 1;
            }
        } catch (final Exception e) {
            player.sendMessage(ChatColor.RED + Lang.INVALID_NUMBER.getConfigValue(args[1]));
            return true;
        }

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

        // final List<Requirement> requirements = plugin.getPlayerChecker()
        // .getAllRequirements(player);

        final List<RequirementsHolder> holders = plugin.getPlayerChecker().getAllRequirementsHolders(player);

        if (holders.size() == 0) {
            player.sendMessage(ChatColor.RED + "You don't have a next rank up!");
            return true;
        }

        // Rank player as he has fulfilled all requirements
        if (holders.size() == 0) {
            player.sendMessage(ChatColor.GREEN + "You don't have any requirements left.");
            return true;
        } else {
            // Get the specified requirement
            if (completionID > holders.size()) {
                completionID = holders.size();
            }

            // Human logic = first number is 1 not 0.
            final RequirementsHolder holder = holders.get((completionID - 1));

            if (plugin.getPlayerDataConfig().hasCompletedRequirement((completionID - 1), uuid)) {
                player.sendMessage(ChatColor.RED + Lang.ALREADY_COMPLETED_REQUIREMENT.getConfigValue());
                return true;
            }

            if (holder.meetsRequirement(player, uuid, true)) {
                // Player meets requirement
                player.sendMessage(
                        ChatColor.GREEN + Lang.SUCCESSFULLY_COMPLETED_REQUIREMENT.getConfigValue(completionID + ""));
                player.sendMessage(ChatColor.AQUA + holder.getDescription());

                // Run results
                holder.runResults(player);

                // Log that a player has passed this requirement
                plugin.getPlayerDataConfig().addCompletedRequirement(uuid, (completionID - 1));

            } else {
                // player does not meet requirements
                player.sendMessage(ChatColor.RED + Lang.DO_NOT_MEET_REQUIREMENTS_FOR.getConfigValue(completionID + ""));
                player.sendMessage(ChatColor.AQUA + holder.getDescription());
                player.sendMessage(ChatColor.GREEN + "Current: " + ChatColor.GOLD + holder.getProgress(player));
            }
        }

        return true;
    }
}
