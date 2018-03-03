package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * The command delegator for the '/ar backup' command.
 */
public class BackupCommand extends AutorankCommand {

    private final Autorank plugin;

    public BackupCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        boolean backupAll = true;
        String fileToBackup = null;

        if (!this.hasPermission(getPermission(), sender)) {
            return true;
        }

        if (args.length >= 2) {
            // A specific data file was given to backup, so backup only the specific file.
            backupAll = false;
            fileToBackup = args[1].toLowerCase();
        }

        if (fileToBackup != null && !fileToBackup.equals("playerdata") && !fileToBackup.equals("data")) {
            sender.sendMessage(ChatColor.RED + "Invalid data file. You can only backup 'playerdata' or 'data'.");
            return true;
        }

        if (backupAll || fileToBackup.equals("playerdata")) {
            plugin.getBackupManager().backupDataFolders("playerdata");
            sender.sendMessage(ChatColor.GREEN + "Successfully created a backup of playerdata!");
        }

        if (backupAll || fileToBackup.equals("data")) {
            plugin.getBackupManager().backupDataFolders("data");
            sender.sendMessage(ChatColor.GREEN + "Successfully created a backup of regular time data!");
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Backup files with playerdata and/or regular data.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.BACKUP_DATA_FILES;
    }

    @Override
    public String getUsage() {
        return "/ar backup <file>";
    }
}
