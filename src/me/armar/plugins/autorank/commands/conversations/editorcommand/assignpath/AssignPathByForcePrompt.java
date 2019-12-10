package me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath;

import me.armar.plugins.autorank.commands.conversations.editorcommand.SelectPlayerPrompt;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPrompt;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AssignPathByForcePrompt extends ConfirmPrompt {

    public static String KEY_ASSIGN_PATH_BY_FORCE = "assignPathByForce";

    public AssignPathByForcePrompt() {
        super(END_OF_CONVERSATION, END_OF_CONVERSATION);
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatColor.GRAY + conversationContext.getSessionData(SelectPlayerPrompt.KEY_PLAYERNAME).toString()
                + ChatColor.GOLD + " does not meet the prerequisites of this path. Do you still want to assign them " +
                "this path?";
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext,
                                                    @NotNull String s) {
        Prompt nextPrompt = super.acceptValidatedInput(conversationContext, s);

        // Store whether we will do so by force.
        conversationContext.setSessionData(KEY_ASSIGN_PATH_BY_FORCE, s.equalsIgnoreCase("yes"));

        return nextPrompt;
    }
}
