package me.armar.plugins.autorank.commands.conversations.editorcommand;

import me.armar.plugins.autorank.commands.conversations.prompts.RequestPlayerNamePrompt;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SelectPlayerPrompt extends RequestPlayerNamePrompt {

    public static String KEY_PLAYERNAME = "playerName";
    public static String KEY_UUID = "uuid";

    public SelectPlayerPrompt() {
        super(ChatColor.GOLD + "What player do you want to edit?", KEY_PLAYERNAME, new EditorMenuPrompt());
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext,
                                                    @NotNull String s) {
        Prompt nextPrompt = super.acceptValidatedInput(conversationContext, s);

        // Store UUID of the player so we can easily retrieve it in other prompts.
        try {
            UUID uuid = UUIDManager.getUUID(s).get();
            conversationContext.setSessionData(KEY_UUID, uuid);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return nextPrompt;
    }
}
