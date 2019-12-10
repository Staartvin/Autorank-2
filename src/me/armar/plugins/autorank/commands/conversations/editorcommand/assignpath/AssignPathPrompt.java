package me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.editorcommand.EditorMenuPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.SelectPlayerPrompt;
import me.armar.plugins.autorank.pathbuilder.Path;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AssignPathPrompt extends StringPrompt {

    public static String KEY_PATH_TO_BE_ASSIGNED = "pathToBeAssigned";

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        String playerName = conversationContext.getSessionData(SelectPlayerPrompt.KEY_PLAYERNAME).toString();

        return ChatColor.GOLD + "What path do you want to assign to " + ChatColor.GRAY + playerName + ChatColor.GOLD + "?";
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {

        Path path = Autorank.getInstance().getPathManager().findPathByDisplayName(s, false);
        Conversable conversable = conversationContext.getForWhom();

        if (path == null) {
            conversable.sendRawMessage(ChatColor.RED + "The path " + ChatColor.GRAY + s + ChatColor.RED + " does not " +
                    "exist!");
            return this; // Return this prompt so the user may try again.
        }

        UUID uuid = (UUID) conversationContext.getSessionData(SelectPlayerPrompt.KEY_UUID);
        String playerName = (String) conversationContext.getSessionData(SelectPlayerPrompt.KEY_PLAYERNAME);

        // If the path is already active, we're not going to add it yet again.
        if (path.isActive(uuid)) {
            conversable.sendRawMessage(ChatColor.GRAY + playerName + ChatColor.RED + " is already on that path.");
            return new EditorMenuPrompt(); // Send user back to the editor menu.
        }

        // Store the path that we want to assign.
        conversationContext.setSessionData(KEY_PATH_TO_BE_ASSIGNED, path.getInternalName());

        // If the player may not join the path, we ask the conversable if they want to set assign the path by force.
        if (!path.isEligible(uuid)) {
            return new AssignPathByForcePrompt();
        }

        // Conversation has ended, we know enough.
        return END_OF_CONVERSATION;
    }
}
