package me.armar.plugins.autorank.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;
import net.md_5.bungee.api.ChatColor;

/**
 * The command delegator for the '/ar gadd' command.
 */
public class GlobalAddCommand extends AutorankCommand {

    private final Autorank plugin;

    public GlobalAddCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        
        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.ADD_GLOBAL_TIME.getPermissionString(), sender)) {
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar gadd <player> <value>"));
            return true;
        }

        if (!plugin.getMySQLManager().isMySQLEnabled()) {
            sender.sendMessage(ChatColor.RED + Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        }

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

        if (uuid == null) {
            sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
            return true;
        }

        if (plugin.getUUIDStorage().hasRealName(uuid)) {
            args[1] = plugin.getUUIDStorage().getRealName(uuid);
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                int value = 0;
                
                if (args.length > 2) {
                    value = AutorankTools.readTimeInput(args, 2);
                }

                if (value >= 0) {
                    if (!plugin.getMySQLManager().setGlobalTime(uuid, plugin.getMySQLManager().getFreshGlobalTime(uuid) + value)) {
                        sender.sendMessage(Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
                        return;
                    }
                    AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(args[1], value + ""));
                } else {
                    AutorankTools.sendColoredMessage(sender,
                            Lang.INVALID_FORMAT.getConfigValue("/ar gadd [player] [value]"));
                }
            }

        });

        return true;
    }

    @Override
    public String getDescription() {
        return "Add [value] to [player]'s global time";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.ADD_GLOBAL_TIME.getPermissionString();
    }

    @Override
    public String getUsage() {
        return "/ar gadd [player] [value]";
    }
}
