package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.migration.MigrationManager;
import me.armar.plugins.autorank.migration.MigrationablePlugin;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.storage.TimeType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The command delegator for the '/ar migrate' command.
 */
public class MigrateCommand extends AutorankCommand {

    private final Autorank plugin;

    public MigrateCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // This command will give a preview of a certain path of ranking.
        if (!this.hasPermission(AutorankPermission.MIGRATE_TIME, sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue(this.getUsage()));
            return true;
        }

        MigrationManager.Migrationable migrationableType;

        try {
            migrationableType =
                    MigrationManager.Migrationable.valueOf(args[1].toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "This is not a valid type of migration!");
            return true;
        }

        MigrationablePlugin migrationablePlugin =
                plugin.getMigrationManager().getMigrationablePlugin(migrationableType).orElse(null);

        if (migrationablePlugin == null) {
            sender.sendMessage(ChatColor.RED + "Could not find a migration plugin for the type you specified.");
            return true;
        }

        if (!migrationablePlugin.isReady()) {
            sender.sendMessage(ChatColor.RED + "This migration plugin is not ready to be used. Are the plugins that " +
                    "depend on it active?");
            return true;
        }

        List<UUID> uuids = plugin.getStorageManager().getPrimaryStorageProvider().getStoredPlayers(TimeType.TOTAL_TIME);

        CompletableFuture<Void> task = migrationablePlugin.migratePlayTime(uuids).thenAccept(migratedPlayers -> {
            sender.sendMessage(ChatColor.GREEN + "" + migratedPlayers + " players have been migrated to Autorank.");
        });

        this.runCommandTask(task);

        return true;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(
     * org.bukkit.command.CommandSender, org.bukkit.command.Command,
     * java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
                                      final String[] args) {

        return Arrays.stream(MigrationManager.Migrationable.values())
                .map(MigrationManager.Migrationable::toString)
                .filter(string -> string.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return "Migrate play time data from another plugin to Autorank";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.MIGRATE_TIME;
    }

    @Override
    public String getUsage() {
        return "/ar migrate <type>";
    }

}
