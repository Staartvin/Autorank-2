package me.armar.plugins.autorank.commands.conversations.editorcommand;


import me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath.AssignPathPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath.UnAssignPathPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.completepath.CompletePathPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.completerequirement.CompleteRequirementPrompt;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditorMenuPrompt extends FixedSetPrompt {

    public static String KEY_ACTION_TYPE = "actionType";

    public static String ACTION_TYPE_ASSIGN_PATH = "assign a path";
    public static String ACTION_TYPE_UNASSIGN_PATH = "unassign a path";
    public static String ACTION_TYPE_COMPLETE_PATH = "complete a path";
    public static String ACTION_TYPE_COMPLETE_REQUIREMENT = "complete a requirement";

    public EditorMenuPrompt() {
        super(ACTION_TYPE_ASSIGN_PATH, ACTION_TYPE_COMPLETE_PATH, ACTION_TYPE_UNASSIGN_PATH,
                ACTION_TYPE_COMPLETE_REQUIREMENT);
    }


    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext,
                                                    @NotNull String s) {

        conversationContext.setSessionData(KEY_ACTION_TYPE, s.trim());

        Prompt nextPrompt = END_OF_CONVERSATION;

        if (s.trim().equalsIgnoreCase(ACTION_TYPE_ASSIGN_PATH)) {
            nextPrompt = new AssignPathPrompt();
        } else if (s.trim().equalsIgnoreCase(ACTION_TYPE_UNASSIGN_PATH)) {
            nextPrompt = new UnAssignPathPrompt();
        } else if (s.trim().equalsIgnoreCase(ACTION_TYPE_COMPLETE_PATH)) {
            nextPrompt = new CompletePathPrompt();
        } else if (s.trim().equalsIgnoreCase(ACTION_TYPE_COMPLETE_REQUIREMENT)) {
            nextPrompt = new CompleteRequirementPrompt();
        }

        return nextPrompt;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatColor.GOLD + "What type of edit do you want to make? " + ChatColor.LIGHT_PURPLE + this.formatFixedSet();
    }
}
