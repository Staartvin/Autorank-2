package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The command delegator for the '/ar gcheck' command.
 */
public class GlobalCheckCommand extends AutorankCommand {

    private final Autorank plugin;

    public GlobalCheckCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // This is a global check. It will not show you the database numbers
        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.DATABASE)) {
            sender.sendMessage(ChatColor.RED + Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        }

        UUID uuid;
        String playerName = null;

        // There was a player specified
        if (args.length > 1) {
            if (!this.hasPermission(AutorankPermission.CHECK_OTHERS, sender)) {
                return true;
            }

            final Player player = plugin.getServer().getPlayer(args[1]);
            if (player == null) {

                uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

                if (uuid == null) {
                    sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                    return true;
                }

                if (plugin.getUUIDStorage().hasRealName(uuid)) {
                    playerName = plugin.getUUIDStorage().getRealName(uuid);
                }
            } else {
                if (player.hasPermission(AutorankPermission.EXCLUDE_FROM_PATHING)) {
                    sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(player.getName()));
                    return true;
                }

                uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

                if (uuid == null) {
                    sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                    return true;
                }

                playerName = player.getName();
            }
        } else if (sender instanceof Player) { // There was no player specified, so take sender as target
            if (!this.hasPermission(AutorankPermission.CHECK_GLOBAL, sender)) {
                return true;
            }

            if (sender.hasPermission("autorank.exclude")) {
                sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender.getName()));
                return true;
            }

            final Player player = (Player) sender;

            uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());
            playerName = player.getName();

        } else {
            AutorankTools.sendColoredMessage(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
            return true;
        }

        UUID finalUuid = uuid;
        String finalPlayerName = playerName;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final int minutes = plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, finalUuid);

            if (minutes < 0) {
                sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(finalPlayerName));
                return;
            }

            AutorankTools.sendColoredMessage(sender, finalPlayerName + " has played for "
                    + AutorankTools.timeToString(minutes, Time.MINUTES) + " across all servers.");

        });

        return true;
    }

    @Override
    public String getDescription() {
        return "Check [player]'s global playtime.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.CHECK_GLOBAL;
    }

    @Override
    public String getUsage() {
        return "/ar gcheck [player]";
    }
}
