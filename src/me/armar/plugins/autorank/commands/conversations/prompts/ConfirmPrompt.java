package me.armar.plugins.autorank.commands.conversations.prompts;

import io.reactivex.annotations.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is a convenience class for showing a confirmation prompt easily.
 * If no message is provided, a default message is shown to the user to confirm or deny the current action.
 * If no prompt is given when the conversable denies, the conversation will end (using
 * {@link Prompt#END_OF_CONVERSATION}.
 * If a prompt is given when the conversable confirms, the next prompt will be the given prompt.
 * Additionally, a callback can be attached that will be called when the prompt has ended. It can be used to signal
 * whether the conversable confirmed or denied the prompt.
 */
public class ConfirmPrompt extends FixedSetPrompt {

    private String message = ChatColor.GOLD + "Are you sure you want to perform this action? Type " + ChatColor.GREEN +
            "yes" + ChatColor.GOLD + " or " + ChatColor.RED + "no" + ChatColor.GOLD + ".";
    private Prompt confirmPrompt;
    private Prompt denyPrompt;

    // A callback that should be performed when the prompt has finished.
    private ConfirmPromptCallback callback;

    /**
     * Create a confirmation prompt with the given message.
     *
     * @param message       Message to show to the conversable. If left null, a default message will be used.
     * @param confirmPrompt Prompt to go to if the conversable confirms. If none is given, the conversation is ended.
     * @param denyPrompt    Prompt to go to if the conversable denies. If none is given, the conversation is ended.
     * @param callback      Callback that will be called when this prompt has ended. Can be null
     */
    public ConfirmPrompt(String message, @NonNull Prompt confirmPrompt, @NonNull Prompt denyPrompt,
                         ConfirmPromptCallback callback) {
        super("yes", "no");

        // Override default message if something was given.
        if (message != null) {
            this.message = message;
        }

        if (confirmPrompt == null) {
            this.confirmPrompt = Prompt.END_OF_CONVERSATION;
        } else {
            this.confirmPrompt = confirmPrompt;
        }

        if (denyPrompt == null) {
            this.denyPrompt = Prompt.END_OF_CONVERSATION;
        } else {
            this.denyPrompt = denyPrompt;
        }

        // Register the callback.
        this.callback = callback;
    }

    /**
     * Create a confirmation prompt with a default message.
     *
     * @param confirmPrompt Prompt to go to if the conversable confirms.
     * @param denyPrompt    Prompt to go to if the conversable denies. If none is given, the conversation is ended.
     */
    public ConfirmPrompt(@NonNull Prompt confirmPrompt, @NonNull Prompt denyPrompt) {
        this(null, confirmPrompt, denyPrompt, null);
    }

    /**
     * Create a confirmation prompt with a default message. If the conversable denies, the conversation will
     * automatically end.
     *
     * @param confirmPrompt Prompt to go to if the conversable confirms.
     */
    public ConfirmPrompt(@NonNull Prompt confirmPrompt) {
        this(null, confirmPrompt, END_OF_CONVERSATION, null);
    }

    public ConfirmPrompt(String message, ConfirmPromptCallback callback) {
        this(message, null, null, callback);
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext,
                                                    @NotNull String s) {

        if (s.equals("yes")) {

            if (callback != null) {
                callback.promptConfirmed();
            }

            return confirmPrompt;
        } else {

            if (callback != null) {
                callback.promptDenied();
            }

            return denyPrompt;
        }
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return message;
    }

    /**
     * Get the message that will be shown to the conversable. If no message was given when this {@link ConfirmPrompt}
     * was created, this will return the default message.
     *
     * @return String message.
     */
    public String getMessage() {
        return this.message;
    }
}
