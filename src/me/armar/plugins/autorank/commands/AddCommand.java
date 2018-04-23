package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;

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

        if (!this.hasPermission(AutorankPermission.ADD_LOCAL_TIME, sender)) {
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

            plugin.getStorageManager().addPlayerTime(uuid, value);

            AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(args[1], plugin
                    .getStorageManager().getPrimaryStorageProvider().getPlayerTime(TimeType.TOTAL_TIME, uuid) + ""));
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
        return AutorankPermission.ADD_LOCAL_TIME;
    }

    @Override
    public String getUsage() {
        return "/ar add [player] [value]";
    }
}
