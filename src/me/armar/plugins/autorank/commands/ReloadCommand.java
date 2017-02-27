package me.armar.plugins.autorank.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * The command delegator for the '/ar reload' command.
 */
public class ReloadCommand extends AutorankCommand {

    private final Autorank plugin;

    public ReloadCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.RELOAD_AUTORANK, sender)) {
            return true;
        }

        // Reload files
        plugin.getPathsConfig().reloadConfig();
        plugin.getPlayerDataConfig().reloadConfig();
        plugin.getSettingsConfig().reloadConfig();

        // Rebuild paths
        plugin.getPathManager().initialiseFromConfigs();

        AutorankTools.sendColoredMessage(sender, Lang.AUTORANK_RELOADED.getConfigValue());
        AutorankTools.sendColoredMessage(sender, ChatColor.RED + "Please do not use this command after you've changed any files. Restart your server instead!");

        return true;
    }

    @Override
    public String getDescription() {
        return "Reload Autorank.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.RELOAD_AUTORANK;
    }

    @Override
    public String getUsage() {
        return "/ar reload";
    }
}
