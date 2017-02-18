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
import me.armar.plugins.autorank.util.AutorankTools.Time;

/**
 * The command delegator for the '/ar remove' command.
 */
public class RemoveCommand extends AutorankCommand {

    private final Autorank plugin;

    public RemoveCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!plugin.getCommandsManager().hasPermission(AutorankPermission.REMOVE_LOCAL_TIME, sender)) {
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar rem <player> <value>"));
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

        int value = 0;

        if (args.length > 2) {

            final StringBuilder builder = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                builder.append(args[i]);
            }

            int changeValue = 0;

            if (!builder.toString().contains("m") && !builder.toString().contains("h")
                    && !builder.toString().contains("d")) {
                changeValue = AutorankTools.stringtoInt(builder.toString().trim());
            } else {
                changeValue = AutorankTools.stringToTime(builder.toString(), Time.MINUTES);
            }

            if (changeValue < 0) {
                value = -1;
            } else {
                value += plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid) - changeValue;
            }
        }

        if (value >= 0) {
            plugin.getFlatFileManager().setLocalTime(TimeType.TOTAL_TIME, value, uuid);
            AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(args[1], value + ""));
        } else {
            AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue("/ar remove [player] [value]"));
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Remove [value] from [player]'s time.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.REMOVE_LOCAL_TIME;
    }

    @Override
    public String getUsage() {
        return "/ar remove [player] [value]";
    }
}
