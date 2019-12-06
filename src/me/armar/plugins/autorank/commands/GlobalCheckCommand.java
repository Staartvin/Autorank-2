package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        CompletableFuture<Void> task = CompletableFuture.completedFuture(null).thenAccept(nothing -> {

            UUID uuid = null;
            String playerName = null;

            // There was a player specified
            if (args.length > 1) {
                if (!this.hasPermission(AutorankPermission.CHECK_OTHERS, sender)) {
                    return;
                }

                final Player player = plugin.getServer().getPlayer(args[1]);

                try {
                    uuid = UUIDManager.getUUID(args[1]).get();
                    playerName = UUIDManager.getPlayerName(uuid).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                if (uuid == null) {
                    sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                    return;
                }

                if (player != null) {
                    if (player.hasPermission(AutorankPermission.EXCLUDE_FROM_PATHING)) {
                        sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(player.getName()));
                        return;
                    }

                    playerName = player.getName();
                }
            } else if (sender instanceof Player) { // There was no player specified, so take sender as target
                if (!this.hasPermission(AutorankPermission.CHECK_GLOBAL, sender)) {
                    return;
                }

                if (sender.hasPermission("autorank.exclude")) {
                    sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender.getName()));
                    return;
                }

                final Player player = (Player) sender;

                uuid = player.getUniqueId();
                playerName = player.getName();

            } else {
                AutorankTools.sendColoredMessage(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
                return;
            }

            int globalPlayTime = 0;
            try {
                globalPlayTime = plugin.getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (globalPlayTime < 0) {
                sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(playerName));
                return;
            }

            AutorankTools.sendColoredMessage(sender, playerName + " has played for "
                    + AutorankTools.timeToString(globalPlayTime, Time.MINUTES) + " across all servers.");

        });

        this.runCommandTask(task);

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
