package me.armar.plugins.autorank.commands.conversations.editorcommand.completerequirement;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.Path;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompleteRequirementPrompt extends StringPrompt {

    public static String KEY_PATH_OF_REQUIREMENT = "pathOfRequirement";

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatColor.GOLD + "What path does the requirement belong to?";
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
        conversationContext.setSessionData(KEY_PATH_OF_REQUIREMENT, path.getInternalName());

        // Conversation has ended, we know enough.
        return new CompleteRequirementRequestRequirementIdPrompt();
    }
}
