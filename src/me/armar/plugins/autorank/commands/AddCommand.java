package me.armar.plugins.autorank.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * The command delegator for the '/ar add' command.
 */
public class AddCommand extends AutorankCommand {

    private final Autorank plugin;

    public AddCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.ADD_LOCAL_TIME.getPermissionString(), sender)) {
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar add <player> <value>"));
            return true;
        }

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

        if (uuid == null) {
            sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
            return true;
        }

        int value = 0;

        if (args.length > 2) {
            value = AutorankTools.readTimeInput(args, 2);
        }

        if (value >= 0) {

            if (plugin.getUUIDStorage().hasRealName(uuid)) {
                args[1] = plugin.getUUIDStorage().getRealName(uuid);
            }

            plugin.getFlatFileManager().setLocalTime(TimeType.TOTAL_TIME, plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid) + value, uuid);
            AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(args[1], value + ""));
        } else {
            AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue("/ar add [player] [value]"));
        }

        return true;
    }

    @Override
    public String getDescription() {
       return "Add [value] to [player]'s time";
    }

    @Override
    public String getPermission() {
        // TODO Auto-generated method stub
        return AutorankPermission.ADD_LOCAL_TIME.getPermissionString();
    }

    @Override
    public String getUsage() {
        // TODO Auto-generated method stub
        return "/ar add [player] [value]";
    }
}
