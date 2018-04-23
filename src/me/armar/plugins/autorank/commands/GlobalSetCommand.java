package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * The command delegator for the '/ar gset' command.
 */
public class GlobalSetCommand extends AutorankCommand {

    private final Autorank plugin;

    public GlobalSetCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        int value = -1;

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar gset <player> <value>"));
            return true;
        }

        // This is a global check. It will not show you the database numbers
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.DATABASE)) {
            sender.sendMessage(ChatColor.RED + Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        }

        value = AutorankTools.readTimeInput(args, 2);

        if (value >= 0) {

            if (!this.hasPermission(AutorankPermission.SET_GLOBAL_TIME, sender)) {
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

            for (TimeType timeType : TimeType.values()) {
                plugin.getPlayTimeManager().setGlobalPlayTime(timeType, uuid, value);
            }

            AutorankTools.sendColoredMessage(sender,
                    Lang.PLAYTIME_CHANGED.getConfigValue(args[1], value + " " + Lang.MINUTE_PLURAL.getConfigValue()));
        } else {
            AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue("/ar gset <player> <value>"));
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Set [player]'s global time to [value].";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.SET_GLOBAL_TIME;
    }

    @Override
    public String getUsage() {
        return "/ar gset [player] [value]";
    }
}
