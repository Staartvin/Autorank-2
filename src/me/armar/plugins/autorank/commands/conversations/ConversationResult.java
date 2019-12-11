package me.armar.plugins.autorank.commands.conversations;

import org.bukkit.conversations.Conversable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a result of a conversation. You can obtain whether the conversation has ended, how it ended and what
 * objects were stored in storage during the conversation.
 */
public class ConversationResult {

    private final boolean endedSuccessfully;
    private final Conversable conversable;
    private boolean endedByKeyword;

    private Map<Object, Object> conversationStorage = new HashMap<>();

    public ConversationResult(boolean endResult, Conversable conversable) {
        this.endedSuccessfully = endResult;
        this.conversable = conversable;
    }

    /**
     * Check whether the conversation was ended successfully. There is no unambigous term for successful, as this
     * depends on the conversation that took place. A prompt in a conversation is responsible for setting whether it
     * was indeed successful.
     *
     * @return true if the conversation ended successfully, false otherwise.
     */
    public boolean wasSuccessful() {
        return endedSuccessfully;
    }

    /**
     * Get the conversable.
     *
     * @return Conversable of the conversation.
     */
    public Conversable getConversable() {
        return conversable;
    }

    protected void setConversationStorage(Map<Object, Object> storage) {
        this.conversationStorage = storage;
    }

    /**
     * Get an object from the conversation storage. The conversation storage is filled by data during the
     * conversation. It consists of answers the user replied.
     *
     * @param key Key to get the data of.
     * @return Object that corresponds to the key (or null) if it does not exist.
     */
    public Object getStorageObject(Object key) {
        return conversationStorage.get(key);
    }

    /**
     * Convenience method to get a string from the conversation storage.
     * Also see {@link #getStorageObject(Object)}.
     *
     * @param key Key to get the data of.
     * @return String if data exists for the given key, null otherwise.
     */
    public String getStorageString(Object key) {
        Object object = getStorageObject(key);

        if (object == null) {
            return null;
        }

        return object.toString();
    }

    /**
     * Convenience method to get a boolean from the conversation storage.
     * Also see {@link #getStorageObject(Object)}.
     *
     * @param key Key to get the data of.
     * @return false if no object was stored with this key, otherwise the boolean value is returned.
     */
    public boolean getStorageBoolean(Object key) {
        Object object = getStorageObject(key);

        if (object == null) {
            return false;
        }

        return (boolean) object;
    }

    /**
     * Convenience method to get an Integer from the conversation storage.
     * Also see {@link #getStorageObject(Object)}.
     *
     * @param key Key to get the data of.
     * @return the integer that was stored for the given key, or null if nothing was stored.
     */
    public Integer getStorageInteger(Object key) {
        Object object = getStorageObject(key);

        if (object == null) {
            return null;
        }

        return (Integer) object;
    }

    public boolean isEndedByKeyword() {
        return endedByKeyword;
    }

    public void setEndedByKeyword(boolean endedByKeyword) {
        this.endedByKeyword = endedByKeyword;
    }
}
