package me.armar.plugins.autorank.commands.manager;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This class represents an Autorank command, such as /ar check or /ar times.
 *
 * @author Staartvin
 */
public abstract class AutorankCommand implements TabExecutor {

    /**
     * Get the description that is used for this command, can be null or empty.
     */
    public abstract String getDescription();

    /**
     * Get the permission that is used to check if a player has permission to
     * perform this command. Note that a command does not have a permission that
     * necessarily fits the command. Sometimes, multiple permissions are needed
     * to check if a player has access to this command.
     */
    public abstract String getPermission();

    /**
     * Get the way this command is supposed to be used. For example, /ar times
     * &lt;player&gt; &lt;type&gt;
     */
    public abstract String getUsage();

    /*
     * (non-Javadoc)
     *
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.
     * CommandSender, org.bukkit.command.Command, java.lang.String,
     * java.lang.String[])
     */
    @Override
    public abstract boolean onCommand(final CommandSender sender, final Command cmd, final String label,
                                      final String[] args);

    /*
     * (non-Javadoc)
     *
     * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.
     * CommandSender, org.bukkit.command.Command, java.lang.String,
     * java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return null;
    }

    /**
     * Get whether the given sender has the given permission. <br>
     * Will also send a 'you don't have this permission' message if the sender
     * does not have the given permission.
     *
     * @param permission Permission to check
     * @param sender     Sender to check
     * @return true if this sender has the given permission, false otherwise.
     */
    public boolean hasPermission(String permission, CommandSender sender) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + Lang.NO_PERMISSION.getConfigValue(permission));
            return false;
        }
        return true;
    }

    /**
     * Return all arguments that were provided in the given string list. An argument is specified as a string with
     * two dashes (--) in front of it. Note that return arguments are all lowercased and the dashes are removed.
     *
     * @param strings Strings provided by the onCommand() method.
     * @return all strings that identify as an argument.
     */
    public List<String> getArgumentOptions(String[] strings) {
        List<String> arguments = new ArrayList<>();

        Arrays.stream(strings).forEach(string -> {
            if (string.matches("[-]{2}[a-zA-Z_-]+")) {
                arguments.add(string.replace("--", "").toLowerCase());
            }
        });

        return arguments;
    }

    /**
     * Run a task of type {@link CompletableFuture} on a separate thread. This is a convenience to easily run tasks
     * on separate threads.
     *
     * @param task Task to run.
     */
    public void runCommandTask(CompletableFuture<?> task) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Autorank");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
