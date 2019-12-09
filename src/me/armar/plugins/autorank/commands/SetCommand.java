package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * The command delegator for the '/ar set' command.
 */
public class SetCommand extends AutorankCommand {

    private final Autorank plugin;

    public SetCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {


        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        int value = AutorankTools.readTimeInput(args, 2);

        if (value >= 0) {

            if (!this.hasPermission(AutorankPermission.SET_LOCAL_TIME, sender)) {
                return true;
            }

            CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept(uuid -> {
                if (uuid == null) {
                    sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                    return;
                }

                String playerName = args[1];

                try {
                    playerName = UUIDManager.getPlayerName(uuid).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                plugin.getStorageManager().setPlayerTime(uuid, value);

                int newPlayerTime = 0;
                try {
                    newPlayerTime = plugin.getStorageManager()
                            .getPrimaryStorageProvider().getPlayerTime(TimeType.TOTAL_TIME, uuid).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                AutorankTools.sendColoredMessage(sender,
                        Lang.PLAYTIME_CHANGED.getConfigValue(playerName,
                                AutorankTools.timeToString(newPlayerTime, AutorankTools.Time.MINUTES)));
            });

            this.runCommandTask(task);

        } else {
            AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Set [player]'s time to [value].";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.SET_LOCAL_TIME;
    }

    @Override
    public String getUsage() {
        return "/ar set [player] [value]";
    }
}
