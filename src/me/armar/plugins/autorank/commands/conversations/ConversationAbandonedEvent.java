package me.armar.plugins.autorank.commands.conversations;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.InactivityConversationCanceller;
import org.jetbrains.annotations.NotNull;

public class ConversationAbandonedEvent implements ConversationAbandonedListener {
    @Override
    public void conversationAbandoned(org.bukkit.conversations.@NotNull ConversationAbandonedEvent conversationAbandonedEvent) {
        Object conversationObject =
                conversationAbandonedEvent.getContext().getSessionData(AutorankConversation.CONVERSATION_IDENTIFIER);

        // There was not AutorankConversation responsible for this conversation
        if (conversationObject == null) {
            return;
        }

        AutorankConversation conversation = (AutorankConversation) conversationObject;

        ConversationResult result;

        // Check if we have an object to indicate that the conversation was successful.
        Object endedSuccesfully =
                conversationAbandonedEvent.getContext().getSessionData(AutorankConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER);

        Conversable conversable = conversationAbandonedEvent.getContext().getForWhom();

        // Build result object.
        if (endedSuccesfully == null) {
            result = new ConversationResult(false, conversable);
        } else {
            result = new ConversationResult((Boolean) endedSuccesfully, conversable);
        }

        // Indicate to conversation that it has ended.
        conversation.conversationEnded(result);

        ConversationCanceller canceller = conversationAbandonedEvent.getCanceller();

        // Inform the user that the conversation has ended and that the player may talk freely again.
        if (!(canceller instanceof InactivityConversationCanceller)) {
            conversable.sendRawMessage(ChatColor.GRAY + "This conversation has ended.");
        } else {
            conversable.sendRawMessage(ChatColor.GRAY + "This conversation has ended because you didn't reply in time" +
                    ".");
        }
    }
}
