package me.armar.plugins.autorank.commands.conversations.editorcommand.completepath;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.editorcommand.SelectPlayerPrompt;
import me.armar.plugins.autorank.pathbuilder.Path;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompletePathPrompt extends StringPrompt {

    public static String KEY_PATH_TO_BE_COMPLETED = "pathToBeCompleted";

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        String playerName = conversationContext.getSessionData(SelectPlayerPrompt.KEY_PLAYERNAME).toString();

        return ChatColor.GOLD + "What path do you want to complete for " + ChatColor.GRAY + playerName + ChatColor.GOLD + "?";
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

        // Store the path that we want to be completed.
        conversationContext.setSessionData(KEY_PATH_TO_BE_COMPLETED, path.getInternalName());

        // Conversation has ended, we know enough.
        return END_OF_CONVERSATION;
    }
}
