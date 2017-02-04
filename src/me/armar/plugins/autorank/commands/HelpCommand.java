package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;

/**
 * The command delegator for the '/ar help' command.
 */
public class HelpCommand extends AutorankCommand {

    private final Autorank plugin;

    public HelpCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (args.length == 1) {
            showHelpPages(sender, 1);
        } else {
            int page = 1;
            try {
                page = Integer.parseInt(args[1]);
            } catch (final Exception e) {
                sender.sendMessage(ChatColor.RED + Lang.INVALID_NUMBER.getConfigValue(args[1]));
                return true;
            }
            showHelpPages(sender, page);
        }
        return true;
    }

    private void showHelpPages(final CommandSender sender, int page) {
        List<AutorankCommand> commands = new ArrayList<AutorankCommand>(
                plugin.getCommandsManager().getRegisteredCommands().values());

        // Change commands list
        if (plugin.getConfigHandler().doBaseHelpPageOnPermissions()) {
            // Create a new list that will be new commands list. This is done so
            // Autorank automatically adjusts help pages.

            // If sender is OP then all commands are available, no need to
            // refactor.
            if (!sender.isOp()) {

                final List<AutorankCommand> newList = new ArrayList<AutorankCommand>();

                for (final AutorankCommand cmd : commands) {
                    // Check if player has permission to do this, before
                    // presenting this command
                    if (cmd.getPermission().allows(sender)) {
                        newList.add(cmd);
                    }
                }

                commands = newList;
            }
        }

        final int listSize = commands.size();

        // Don't show more than 6 commands per page
        // (Does she want the D?)
        final int maxPages = (int) Math.ceil(listSize / 6D);

        if (page > maxPages || page == 0)
            page = maxPages;

        int start = 0;
        int end = 6;

        if (page != 1) {
            final int pageDifference = page - 1;

            // Because we need 7, not 6.
            start += 1;

            start += (6 * pageDifference);
            end = start + 6;
        }

        sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");

        for (int i = start; i < end; i++) {
            // Can't go any further
            if (i >= listSize)
                break;

            final AutorankCommand command = commands.get(i);

            sender.sendMessage(ChatColor.AQUA + command.getUsage() + ChatColor.GRAY + " - " + command.getDescription());
        }

        sender.sendMessage(ChatColor.BLUE + "Page " + page + " of " + maxPages);
    }

    @Override
    public String getDescription() {
        return "Show a list of commands.";
    }

    @Override
    public AutorankPermission getPermission() {
        return AutorankPermission.HELP_PAGES;
    }

    @Override
    public String getUsage() {
        return "/ar help <page>";
    }
}
