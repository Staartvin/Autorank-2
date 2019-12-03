package me.armar.plugins.autorank.commands.conversations;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class represent a conversation that can be held with any {@link Conversable} object (either a player or the
 * console). Use the {@link #startConversation(Conversable)} to start the conversation, but be sure to attach some
 * callback to receive any acknowledgement of the ending of the conversation (using
 * {@link #afterConversationEnded(ConversationCallback)}).
 * <p>
 * Note that the default keyword to stop a conversation is 'stop'. You can change this using
 * {@link #setEscapeSequence(String)}.
 */
public class AutorankConversation {

    public static String CONVERSATION_SUCCESSFUL_IDENTIFIER = "success";
    static String CONVERSATION_IDENTIFIER = "autorankConversation";

    // A static map to register who's already in a conversation. You can't start a new conversation with a player if
    // they are already in one.
    private static Map<UUID, Boolean> isInConversation = new HashMap<>();

    private ConversationCallback callback; // Store callback to call when conversation has ended.
    private ConversationFactory factory; // Store factory to start it.
    private boolean started, ended; // Store whether the conversation has started or ended.

    /**
     * Create a new conversation with default values. Make sure to set a first prompt (using
     * {@link #setFirstPrompt(Prompt)}) before starting the conversation with {@link #startConversation(Conversable)}.
     * @param plugin Plugin that the player will be talking to.
     */
    public AutorankConversation(Autorank plugin) {
        factory = new ConversationFactory(plugin);

        this.setupConversationFactory();
    }

    /**
     * Convenience method to build a conversation using a first prompt. You can immediately start the conversation if
     * you desire.
     * @param prompt Prompt to start the conversation with.
     * @return an {@link AutorankConversation} object so you can easily chain calls.
     */
    public static AutorankConversation fromFirstPrompt(Prompt prompt) {
        Autorank plugin = (Autorank) Bukkit.getServer().getPluginManager().getPlugin("Autorank");

        AutorankConversation conversation = new AutorankConversation(plugin);
        conversation.setFirstPrompt(prompt);

        return conversation;
    }

    /**
     * Check whether a user is already in a conversation.
     * @param uuid UUID of the user.
     * @return true if the user is currently in a conversation, false otherwise.
     */
    public static boolean isInConversation(UUID uuid) {
        return isInConversation.containsKey(uuid) && isInConversation.get(uuid);
    }

    /**
     * Set whether a user is currently in a conversation.
     * @param uuid UUID of the user
     * @param inConversation Whether the user is in a conversation
     */
    public static void setInConversation(UUID uuid, boolean inConversation) {
        isInConversation.put(uuid, inConversation);
    }

    private void setupConversationFactory() {

        // Fill the factory with initial data so we can find which AutorankConversation instance this conversation
        // belongs to.
        Map<Object, Object> initialData = new HashMap<>();
        initialData.put(CONVERSATION_IDENTIFIER, this);

        factory = factory.withModality(false) // Don't suppress messages sent to the player when they have a
                // conversation
                .withEscapeSequence("stop")
                .withInitialSessionData(initialData)
                // Attach a listener to be able to determine when the conversation is abandoned and we should call
                // the callback.
                .addConversationAbandonedListener(new ConversationAbandonedEvent())
                .withTimeout(30) // Set standard timeout to 30 seconds.
                .withLocalEcho(false); // Don't tell the user what they typed.
    }

    /**
     * Set the keyword that triggers the end of the conversation. Note that this is case-sensitive!
     *
     * @param escapeSequence String
     */
    public void setEscapeSequence(String escapeSequence) {
        factory = factory.withEscapeSequence(escapeSequence);
    }

    /**
     * Set the first prompt that will be shown in the conversation.
     *
     * @param firstPrompt Prompt to show from the beginning.
     */
    public void setFirstPrompt(Prompt firstPrompt) {
        factory = factory.withFirstPrompt(firstPrompt);
    }

    /**
     * Set how many seconds a player should not reply to automatically abandon the conversation due to inactivity.
     *
     * @param seconds Timeout in seconds.
     */
    public void setTimeout(int seconds) {
        factory = factory.withTimeout(seconds);
    }

    /**
     * Start the conversation (if there is no conversation started yet).
     * It is recommended to run this method asynchronously, as you might want to run it on another thread.
     * <p>
     * Note that when the conversable is already involved in another conversation, this will not start a new
     * conversation.
     *
     * @param conversable Object to converse with.
     */
    public void startConversation(Conversable conversable) {

        if (conversable.isConversing()) {
            conversable.sendRawMessage(ChatColor.RED + "You are already in a conversation.");
            return;
        }

        if (conversable instanceof Player) {
            // Player is in conversation, so we cannot open any other conversations.
            if (isInConversation(((Player) conversable).getUniqueId())) {
                conversable.sendRawMessage(ChatColor.RED + "You are already in a conversation.");
                return;
            }

            // Mark that player is now in a conversation.
            setInConversation(((Player) conversable).getUniqueId(), true);
        }

        setEnded(false);
        setStarted(true);

        factory.buildConversation(conversable).begin();
    }

    /**
     * Convenience method to start a conversation from a CommandSender object. Converts the CommandSender to a
     * Conversable object automatically and start the conversation.
     *
     * @param sender CommandSender to start the conversation with.
     */
    public void startConversationAsSender(CommandSender sender) {
        if (sender instanceof Player) {
            this.startConversation((Player) sender);
        } else {
            this.startConversation(sender.getServer().getConsoleSender());
        }
    }

    /**
     * Attach a callback to the conversation to fire after the conversation has been completed. It is up to you to
     * use the {@link org.bukkit.conversations.ConversationContext} object to identify where the conversation has
     * ended. This callback will always be called when a conversation has ended, regardless of the state it was in.
     *
     * @param callback Callback to fire when conversation has ended.
     */
    public void afterConversationEnded(ConversationCallback callback) {
        this.callback = callback;
    }

    /**
     * Indicate that the conversation has ended with the given result.
     *
     * @param conversationResult Result of the conversation.
     */
    public void conversationEnded(ConversationResult conversationResult) {

        // Player is no longer in conversation.
        if (conversationResult.getConversable() instanceof Player) {
            setInConversation(((Player) conversationResult.getConversable()).getUniqueId(), false);
        }

        setEnded(true);
        setStarted(false);

        if (callback != null) {
            // Conversation ended, so call callback.
            callback.conversationEnded(conversationResult);
        }
    }

    /**
     * Get whether the conversation has started or not.
     *
     * @return true if it has started, false otherwise.
     */
    public boolean hasStarted() {
        return started;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * Get whether the conversation has ended or not.
     *
     * @return true if the conversation has ended, false otherwise.
     */
    public boolean hasEnded() {
        return ended;
    }

    private void setEnded(boolean ended) {
        this.ended = ended;
    }
}
