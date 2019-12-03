package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.StorageProvider;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        if (!this.hasPermission(AutorankPermission.ADD_GLOBAL_TIME, sender)) {
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        if (!plugin.getStorageManager().isStorageTypeActive(StorageProvider.StorageType.DATABASE)) {
            sender.sendMessage(ChatColor.RED + Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        }

        CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept(uuid -> {
            if (uuid == null) {
                sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                return;
            }

            int value = AutorankTools.readTimeInput(args, 2);

            if (value >= 0) {

                for (TimeType timeType : TimeType.values()) {
                    plugin.getPlayTimeManager().addGlobalPlayTime(timeType, uuid, value);
                }

                String playerName = args[1];

                try {
                    playerName = UUIDManager.getPlayerName(uuid).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(playerName, plugin
                        .getPlayTimeManager().getGlobalPlayTime(TimeType.TOTAL_TIME, uuid) + value));
            } else {
                AutorankTools.sendColoredMessage(sender,
                        Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            }
        });

        this.runCommandTask(task);

        return true;
    }

    @Override
    public String getDescription() {
        return "Add [value] to [player]'s global time";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.ADD_GLOBAL_TIME;
    }

    @Override
    public String getUsage() {
        return "/ar gadd [player] [value]";
    }
}
