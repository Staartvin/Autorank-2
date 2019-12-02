package me.armar.plugins.autorank.commands.conversations.resetcommand;

import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPrompt;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResetConversation extends FixedSetPrompt {

    public ResetConversation() {
        super("progress", "completedpaths", "activepaths");
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext,
                                                    @NotNull String s) {
        conversationContext.getForWhom().sendRawMessage(ChatColor.GREEN + "You want to reset " + s);

        conversationContext.setSessionData(AutorankConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);

        return new ConfirmPrompt(Prompt.END_OF_CONVERSATION);
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatColor.GOLD + "What do you want to reset? You can reset " + this.formatFixedSet();
    }
}
