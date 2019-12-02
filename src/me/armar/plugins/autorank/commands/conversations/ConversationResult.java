package me.armar.plugins.autorank.commands.conversations;

import org.bukkit.conversations.Conversable;

import java.util.HashMap;
import java.util.Map;

public class ConversationResult {

    private final boolean endedSuccessfully;
    private final Conversable conversable;

    private Map<Object, Object> conversationStorage = new HashMap<>();

    public ConversationResult(boolean endResult, Conversable conversable) {
        this.endedSuccessfully = endResult;
        this.conversable = conversable;
    }

    public boolean wasSuccessful() {
        return endedSuccessfully;
    }

    public Conversable getConversable() {
        return conversable;
    }
}
