package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * The command delegator for the '/ar fcheck' command.
 */
public class ForceCheckCommand extends AutorankCommand {

    private final Autorank plugin;

    public ForceCheckCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!this.hasPermission(AutorankPermission.FORCE_CHECK, sender))
            return true;

        if (args.length != 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        final String target = args[1];

        CompletableFuture<Void> task = UUIDManager.getUUID(target).thenAccept(uuid -> {

            if (uuid == null) {
                sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(target));
                return;
            }
            String playerName = null;

            try {
                playerName = UUIDManager.getPlayerName(uuid).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (plugin.getPlayerChecker().isExemptedFromAutomaticChecking(uuid)) {
                sender.sendMessage(Lang.PLAYER_IS_EXCLUDED.getConfigValue(playerName));
                return;
            }

            // Check the player
            plugin.getPlayerChecker().checkPlayer(uuid);

            // Let checker know that we checked.
            sender.sendMessage(ChatColor.GREEN + playerName + " checked!");
        });

        this.runCommandTask(task);

        return true;
    }

    @Override
    public String getDescription() {
        return "Do a manual silent check.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.FORCE_CHECK;
    }

    @Override
    public String getUsage() {
        return "/ar forcecheck <player>";
    }
}
