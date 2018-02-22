package me.armar.plugins.autorank.commands.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

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
}
