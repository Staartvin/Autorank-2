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
 * The command delegator for the '/ar remove' command.
 */
public class RemoveCommand extends AutorankCommand {

    private final Autorank plugin;

    public RemoveCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!this.hasPermission(AutorankPermission.REMOVE_LOCAL_TIME, sender)) {
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept(uuid -> {
            if (uuid == null) {
                sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                return;
            }

            int value = AutorankTools.readTimeInput(args, 2);

            String playerName = args[1];

            try {
                playerName = UUIDManager.getPlayerName(uuid).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (value >= 0) {
                // Adding negative time
                plugin.getStorageManager().addPlayerTime(uuid, -value);
                AutorankTools.sendColoredMessage(sender,
                        Lang.PLAYTIME_CHANGED.getConfigValue(playerName, plugin
                                .getStorageManager().getPrimaryStorageProvider().getPlayerTime(TimeType.TOTAL_TIME,
                                        uuid)));
            } else {
                AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            }
        });

        this.runCommandTask(task);

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
