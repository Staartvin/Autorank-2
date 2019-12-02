package me.armar.plugins.autorank.commands.conversations.prompts;

import io.reactivex.annotations.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfirmPrompt extends FixedSetPrompt {

    private String message;
    private Prompt confirmPrompt;
    private Prompt denyPrompt;

    public ConfirmPrompt(String message, @NonNull Prompt confirmPrompt, @NonNull Prompt denyPrompt) {
        super("confirm", "deny");

        // Set default message if none was given.
        if (message == null) {
            this.message = ChatColor.GOLD + "Please " + ChatColor.GREEN + "confirm"
                    + ChatColor.GOLD + " or " + ChatColor.RED + "deny" + ChatColor.GOLD + " this action.";
        } else {
            this.message = message;
        }

        this.confirmPrompt = confirmPrompt;
        this.denyPrompt = denyPrompt;
    }

    public ConfirmPrompt(@NonNull Prompt confirmPrompt, @NonNull Prompt denyPrompt) {
        this(null, confirmPrompt, denyPrompt);
    }

    public ConfirmPrompt(@NonNull Prompt confirmPrompt) {
        this(null, confirmPrompt, END_OF_CONVERSATION);
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext,
                                                    @NotNull String s) {

        if (s.equals("confirm")) {
            return confirmPrompt;
        } else {
            return denyPrompt;
        }
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return message;
    }
}
