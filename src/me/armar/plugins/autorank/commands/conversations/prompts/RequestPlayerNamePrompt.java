package me.armar.plugins.autorank.commands.conversations.prompts;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A convenience prompt that request a playername from the user. The playername is valid if the given player has
 * played on the server before.
 */
public class RequestPlayerNamePrompt extends ValidatingPrompt {

    private final String message;
    private String PLAYERNAME_KEY = "playerName";
    private final Prompt nextPrompt;

    /**
     * Request a player name with given request message and store it in the {@link ConversationContext} using the
     * given key.
     *
     * @param message    Message to show to the user. Cannot be null.
     * @param key        Key to store the playername to. If null, the default is "playerName".
     * @param nextPrompt Next prompt to show. If this is null, the conversation will be ended.
     */
    public RequestPlayerNamePrompt(String message, String key, Prompt nextPrompt) {
        super();
        this.message = message;

        if (key != null) {
            PLAYERNAME_KEY = key;
        }

        this.nextPrompt = nextPrompt == null ? Prompt.END_OF_CONVERSATION : nextPrompt;
    }

    /**
     * Request a player name with given request message and store it in the "playerName" key in the
     * {@link ConversationContext} object.
     *
     * @param message    Message to show to the user. Cannot be null.
     * @param nextPrompt Next prompt to show. If this is null, the conversation will be ended.
     */
    public RequestPlayerNamePrompt(String message, Prompt nextPrompt) {
        this(message, null, nextPrompt);
    }

    /**
     * Request a player name with the given request message. The playername will be stored in the "playerName" key
     * and the conversation will end after this prompt.
     *
     * @param message Message to show to the user. Cannot be null.
     */
    public RequestPlayerNamePrompt(String message) {
        this(message, null);
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return message;
    }

    @Override
    protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {

        return Bukkit.getOfflinePlayer(s).hasPlayedBefore();
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext,
                                                    @NotNull String s) {
        conversationContext.setSessionData(PLAYERNAME_KEY, s); // Store player name for use in other prompts.
        return nextPrompt;
    }
}
