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

public class AutorankConversation {

    public static String CONVERSATION_SUCCESSFUL_IDENTIFIER = "success";
    static String CONVERSATION_IDENTIFIER = "autorankConversation";
    private static Map<UUID, Boolean> isInConversation = new HashMap<>();
    private ConversationCallback callback; // Store callback to call when conversation has ended.
    private ConversationFactory factory; // Store factory to start it.
    private boolean started, ended;

    // TODO: Make it so you can't start two conversations at the same time.

    public AutorankConversation(Autorank plugin) {
        factory = new ConversationFactory(plugin);

        this.setupConversationFactory();
    }

    public static AutorankConversation fromFirstPrompt(Prompt prompt) {
        Autorank plugin = (Autorank) Bukkit.getServer().getPluginManager().getPlugin("Autorank");

        AutorankConversation conversation = new AutorankConversation(plugin);
        conversation.setFirstPrompt(prompt);

        return conversation;
    }

    public static boolean isInConversation(UUID uuid) {
        return isInConversation.containsKey(uuid) && isInConversation.get(uuid);
    }

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
                .withLocalEcho(false); // Don't tell the user what they typed.
    }

    public void setEscapeSequence(String escapeSequence) {
        factory = factory.withEscapeSequence(escapeSequence);
    }

    public void setFirstPrompt(Prompt firstPrompt) {
        factory = factory.withFirstPrompt(firstPrompt);
    }

    public void setTimeout(int seconds) {
        factory = factory.withTimeout(seconds);
    }

    public void startConversation(Conversable conversable) {

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

    public void startConversationAsSender(CommandSender sender) {
        if (sender instanceof Player) {
            this.startConversation((Player) sender);
        } else {
            this.startConversation(sender.getServer().getConsoleSender());
        }
    }

    public void afterConversationEnded(ConversationCallback callback) {
        this.callback = callback;
    }

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

    public boolean hasStarted() {
        return started;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }

    public boolean hasEnded() {
        return ended;
    }

    private void setEnded(boolean ended) {
        this.ended = ended;
    }
}
