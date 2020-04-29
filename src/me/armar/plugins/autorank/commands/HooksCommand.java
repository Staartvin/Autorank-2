package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.AutorankHook;
import me.staartvin.utils.pluginlibrary.hooks.LibraryHook;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * The command delegator for the '/ar hooks' command.
 */
public class HooksCommand extends AutorankCommand {

    private final Autorank plugin;

    public HooksCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!this.hasPermission(getPermission(), sender)) return true;

        if (!plugin.getDependencyManager().isPluginLibraryLoaded()) {
            sender.sendMessage(ChatColor.RED + "Cannot show dependencies as PluginLibrary is not installed");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "Autorank Hooks:");

        for (final Library dep : Library.values()) {

            final LibraryHook handler = plugin.getDependencyManager()
                    .getLibraryHook(dep);

            if (handler.isAvailable() && !(handler instanceof AutorankHook)) {
                sender.sendMessage(org.bukkit.ChatColor.GRAY + "- " + org.bukkit.ChatColor.GREEN + dep
                        .getHumanPluginName());
            }
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Shows a list of plugins Autorank is hooked into.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.SHOW_HOOKS;
    }

    @Override
    public String getUsage() {
        return "/ar hooks";
    }
}
