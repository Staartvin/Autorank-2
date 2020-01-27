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
import java.util.concurrent.TimeUnit;

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
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        CompletableFuture<Void> task = UUIDManager.getUUID(args[1]).thenAccept(uuid -> {
            if (uuid == null) {
                sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
                return;
            }

            int value = AutorankTools.readTimeInput(args, 2);

            if (value >= 0) {
                plugin.getPlayTimeStorageManager().addPlayerTime(uuid, value);

                String playerName = null;
                int newPlayerTime = 0;
                try {
                    playerName = UUIDManager.getPlayerName(uuid).get();
                    newPlayerTime =
                            plugin.getPlayTimeManager().getPlayTime(TimeType.TOTAL_TIME,
                                    uuid).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                AutorankTools.sendColoredMessage(sender,
                        Lang.PLAYTIME_CHANGED.getConfigValue(playerName,
                                AutorankTools.timeToString(newPlayerTime, TimeUnit.MINUTES)));
            } else {
                AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            }
        });

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

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
