package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.conversations.AutorankConversation;
import me.armar.plugins.autorank.commands.conversations.editorcommand.EditorMenuPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.SelectPlayerPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath.AssignPathByForcePrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath.AssignPathPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.assignpath.UnAssignPathPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.completepath.CompletePathPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.completerequirement.CompleteRequirementPrompt;
import me.armar.plugins.autorank.commands.conversations.editorcommand.completerequirement.CompleteRequirementRequestRequirementIdPrompt;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.pathbuilder.Path;
import me.armar.plugins.autorank.pathbuilder.holders.CompositeRequirement;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * The command delegator for the '/ar editor' command.
 */
public class EditorCommand extends AutorankCommand {

    private final Autorank plugin;

    public EditorCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (!this.hasPermission(this.getPermission(), sender)) {
            return true;
        }

        AutorankConversation conversation = AutorankConversation.fromFirstPrompt(new SelectPlayerPrompt());

        conversation.afterConversationEnded((callback) -> {
            String actionType = callback.getStorageString(EditorMenuPrompt.KEY_ACTION_TYPE);
            UUID uuid = (UUID) callback.getStorageObject(SelectPlayerPrompt.KEY_UUID);
            String playerName = callback.getStorageString(SelectPlayerPrompt.KEY_PLAYERNAME);

            // Don't do anything when no action type is selected.
            if (actionType == null || callback.isEndedByKeyword()) {
                return;
            }

            if (actionType.equals(EditorMenuPrompt.ACTION_TYPE_ASSIGN_PATH)) {

                boolean assignedByForce =
                        callback.getStorageBoolean(AssignPathByForcePrompt.KEY_ASSIGN_PATH_BY_FORCE);

                // Check for the correct permissions.
                if (!this.hasPermission(assignedByForce ? AutorankPermission.EDITOR_ASSIGN_PATH_FORCE :
                        AutorankPermission.EDITOR_ASSIGN_PATH, sender)) return;

                String pathToAssign = callback.getStorageString(AssignPathPrompt.KEY_PATH_TO_BE_ASSIGNED);

                if (pathToAssign == null) {
                    return;
                }

                Path path = plugin.getPathManager().findPathByInternalName(pathToAssign, false);

                if (path == null) {
                    return;
                }

                // Assign the path.
                try {
                    plugin.getPathManager().assignPath(path, uuid, assignedByForce);
                    sender.sendMessage(ChatColor.GREEN + "Assigned '" + ChatColor.GOLD + path.getDisplayName() + ChatColor.GREEN + "' to " + playerName);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Could not assign '" + ChatColor.GOLD + path.getDisplayName() + ChatColor.RED + "' to " + playerName);
                    return;
                }
            } else if (actionType.equals(EditorMenuPrompt.ACTION_TYPE_UNASSIGN_PATH)) {

                // Check for the correct permission.
                if (!this.hasPermission(AutorankPermission.EDITOR_UNASSIGN_PATH, sender)) return;

                String pathToUnAssign = callback.getStorageString(UnAssignPathPrompt.KEY_PATH_TO_BE_UNASSIGNED);

                if (pathToUnAssign == null) {
                    return;
                }

                Path path = plugin.getPathManager().findPathByInternalName(pathToUnAssign, false);

                if (path == null) {
                    return;
                }

                // De-assign the path
                plugin.getPathManager().deassignPath(path, uuid);
                sender.sendMessage(ChatColor.GREEN + "Unassigned '" + ChatColor.GOLD + path.getDisplayName() + ChatColor.GREEN + "' from " + playerName);
            } else if (actionType.equals(EditorMenuPrompt.ACTION_TYPE_COMPLETE_PATH)) {

                // Check for the correct permission.
                if (!this.hasPermission(AutorankPermission.EDITOR_COMPLETE_PATH, sender)) return;

                String pathToComplete = callback.getStorageString(CompletePathPrompt.KEY_PATH_TO_BE_COMPLETED);

                if (pathToComplete == null) {
                    return;
                }

                Path path = plugin.getPathManager().findPathByInternalName(pathToComplete, false);

                if (path == null) {
                    return;
                }

                sender.sendMessage(ChatColor.GREEN + "Path '" + ChatColor.GOLD + path.getDisplayName() + ChatColor.GREEN + "' has been completed for " + playerName);
                plugin.getPathManager().completePath(path, uuid);

            } else if (actionType.equals(EditorMenuPrompt.ACTION_TYPE_COMPLETE_REQUIREMENT)) {

                // Check for the correct permission.
                if (!this.hasPermission(AutorankPermission.EDITOR_COMPLETE_REQUIREMENT, sender)) return;

                String pathOfRequirement = callback.getStorageString(CompleteRequirementPrompt.KEY_PATH_OF_REQUIREMENT);

                if (pathOfRequirement == null) {
                    return;
                }

                Path path = plugin.getPathManager().findPathByInternalName(pathOfRequirement, false);

                if (path == null) {
                    return;
                }

                Integer requirementId =
                        callback.getStorageInteger(CompleteRequirementRequestRequirementIdPrompt.KEY_REQUIREMENT_TO_BE_COMPLETED);

                if (requirementId == null) {
                    return;
                }

                CompositeRequirement requirement = path.getRequirement(requirementId);

                if (requirement == null) {
                    return;
                }

                sender.sendMessage(ChatColor.GREEN + "Requirement '" + ChatColor.GOLD + path.getDisplayName() + ChatColor.GREEN + "' has been completed for " + playerName);
                path.completeRequirement(uuid, requirementId);

            }
        });

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> conversation.startConversationAsSender(sender));

        return true;
    }

    @Override
    public String getDescription() {
        return "Edit player data of any player";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "/ar editor";
    }
}
