package me.armar.plugins.autorank.commands.conversations;

/**
 * Simple interface to specify what should happen after a {@link AutorankConversation} has ended.
 */
public interface ConversationCallback {

    /**
     * Called when the conversation has ended. It is up to you to determine whether the conversation has ended
     * correctly, as this method will always be called when a conversation has ended, regardless of the state of the
     * conversation.
     *
     * @param result result of the conversation.
     */
    void conversationEnded(ConversationResult result);
}
