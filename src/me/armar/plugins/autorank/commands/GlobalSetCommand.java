package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.PlayTimeStorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        // This is a global check. It will not show you the database numbers
        if (!plugin.getPlayTimeStorageManager().isStorageTypeActive(PlayTimeStorageProvider.StorageType.DATABASE)) {
            sender.sendMessage(ChatColor.RED + Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        }

        final int value = AutorankTools.readTimeInput(args, 2);

        if (value >= 0) {

            if (!this.hasPermission(AutorankPermission.SET_GLOBAL_TIME, sender)) {
                return true;
            }

            CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept(uuid -> {
                if (uuid == null) {
                    sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                    return;
                }

                for (TimeType timeType : TimeType.values()) {
                    plugin.getPlayTimeManager().setGlobalPlayTime(timeType, uuid, value);
                }

                String playerName = args[1];

                try {
                    playerName = UUIDManager.getPlayerName(uuid).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                AutorankTools.sendColoredMessage(sender,
                        Lang.PLAYTIME_CHANGED.getConfigValue(playerName,
                                value + " " + Lang.MINUTE_PLURAL.getConfigValue()));
            });

            this.runCommandTask(task);

        } else {
            AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
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
