package me.armar.plugins.autorank.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.hooks.DependencyManager.AutorankDependency;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.staartvin.statz.hooks.Dependency;

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

        if (!plugin.getDependencyManager().getDependency(AutorankDependency.STATZ).isAvailable()) {
            sender.sendMessage(ChatColor.RED + "Cannot show dependencies as Statz is not installed");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Autorank is currently hooked into:");

        for (final Dependency dep : Dependency.values()) {
            // There is no AutorankDependency handler for Autorank

            final me.staartvin.statz.hooks.DependencyHandler handler = plugin.getDependencyManager()
                    .getDependencyHandler(dep);

            final StringBuilder message = new StringBuilder(ChatColor.GRAY + "- " + ChatColor.GREEN + dep.getInternalString());

            if (!handler.isAvailable()) {
                continue;
            }

            sender.sendMessage(message.toString());
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Shows a list of hookable plugins for Autorank";
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
