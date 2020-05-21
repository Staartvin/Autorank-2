package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.TimeType;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import me.armar.plugins.autorank.playtimes.PlayTimeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The command delegator for the '/ar set' command.
 */
public class MultiplierCommand extends AutorankCommand {

    private final Autorank plugin;

    public MultiplierCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {


        if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        int value = AutorankTools.readTimeInput(args, 1);

        if (value >= 1) {

            if (!this.hasPermission(AutorankPermission.SET_LOCAL_TIME, sender)) {
                return true;
            }
            
            PlayTimeManager.MULTIPLIER=value;
            sender.sendMessage(ChatColor.AQUA + "MULTIPLIER = " + ChatColor.GRAY + PlayTimeManager.MULTIPLIER);



        } else {
            AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Set multiplier's time to [value].";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.SET_LOCAL_TIME;
    }

    @Override
    public String getUsage() {
        return "/ar multiplier [value]";
    }
}
