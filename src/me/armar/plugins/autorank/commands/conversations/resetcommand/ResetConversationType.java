package me.armar.plugins.autorank.commands.conversations.resetcommand;

import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPrompt;
import me.armar.plugins.autorank.commands.conversations.prompts.ConfirmPromptCallback;
import me.armar.plugins.autorank.commands.conversations.prompts.RequestPlayerNamePrompt;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResetConversationType extends FixedSetPrompt {

    public static String RESET_TYPE = "resetType";
    public static String RESET_ACTIVE_PROGRESS = "active progress";
    public static String RESET_ALL_PROGRESS = "all progress";
    public static String RESET_COMPLETED_PATHS = "completed paths";
    public static String RESET_ACTIVE_PATHS = "active paths";

    public ResetConversationType() {
        super(RESET_ACTIVE_PROGRESS, RESET_COMPLETED_PATHS, RESET_ACTIVE_PATHS, RESET_ALL_PROGRESS);
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext,
                                                    @NotNull String s) {

        conversationContext.setSessionData(RESET_TYPE, s); // Save reset type

        String requestPlayerMessage;

        if (s.equals(ResetConversationType.RESET_COMPLETED_PATHS)) {
            requestPlayerMessage = ChatColor.DARK_AQUA + "Of which player do you want to reset the " +
                    "completed paths?";
        } else if (s.equals(ResetConversationType.RESET_ACTIVE_PATHS)) {
            requestPlayerMessage = ChatColor.DARK_AQUA + "Of which player do you want to reset the " +
                    "active paths?";
        } else if (s.equals(ResetConversationType.RESET_ALL_PROGRESS)) {
            requestPlayerMessage = ChatColor.DARK_AQUA + "Of which player do you want to reset all progress?";
        } else {
            requestPlayerMessage = ChatColor.DARK_AQUA + "Of which player do you want to reset the active progress?";
        }

        return new RequestPlayerNamePrompt(requestPlayerMessage, new ResetConfirmation());
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatColor.DARK_AQUA + "What do you want to reset? You can reset " + ChatColor.RED + this.formatFixedSet();
    }
}

class ResetConfirmation extends MessagePrompt {

    @Override
    protected @Nullable Prompt getNextPrompt(@NotNull ConversationContext conversationContext) {
        String message =
                ChatColor.DARK_AQUA + "Are you sure you want to reset the %type% of "
                        + ChatColor.GOLD + conversationContext.getSessionData("playerName")
                        + ChatColor.DARK_AQUA + "?"
                        + " Please confirm or deny.";

        String resetType = conversationContext.getSessionData(ResetConversationType.RESET_TYPE).toString();

        if (resetType.equals(ResetConversationType.RESET_COMPLETED_PATHS)) {
            message = message.replace("%type%", "completed paths");
        } else if (resetType.equals(ResetConversationType.RESET_ACTIVE_PATHS)) {
            message = message.replace("%type%", "active paths");
        } else if (resetType.equals(ResetConversationType.RESET_ALL_PROGRESS)) {
            message = message.replace("%type%", "all progress");
        } else {
            message = message.replace("%type%", "active progress");
        }

        return new ConfirmPrompt(message, new ConfirmPromptCallback() {
            @Override
            public void promptConfirmed() {
                conversationContext.setSessionData(AutorankConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER,
                        true);
            }

            @Override
            public void promptDenied() {
                conversationContext.setSessionData(AutorankConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, false);
            }
        });
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return "";
    }
}
