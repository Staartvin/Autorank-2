package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.editorcommand.SelectPlayerPrompt;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * The command delegator for the '/ar editor' command.
 */
public class EditorCommand extends AutorankCommand {

    private final Autorank plugin;

    public EditorCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        }

        AutorankConversation conversation = AutorankConversation.fromFirstPrompt(new SelectPlayerPrompt());

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> conversation.startConversationAsSender(sender));

        return true;
    }

    @Override
    public String getDescription() {
        return "Edit player data of any player";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "/ar editor";
    }
}
