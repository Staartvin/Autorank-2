package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The command delegator for the '/ar gcheck' command.
 */
public class GlobalCheckCommand extends AutorankCommand {

    private final Autorank plugin;

    public GlobalCheckCommand(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        // This is a global check. It will not show you the database numbers
        if (!plugin.getMySQLManager().isMySQLEnabled()) {
            sender.sendMessage(ChatColor.RED + Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        }

        if (args.length > 1) {

            if (!this.hasPermission(AutorankPermission.CHECK_OTHERS, sender)) {
                return true;
            }

            final Player player = plugin.getServer().getPlayer(args[1]);
            if (player == null) {

                final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

                if (uuid == null) {
                    sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                    return true;
                }

                if (plugin.getUUIDStorage().hasRealName(uuid)) {
                    args[1] = plugin.getUUIDStorage().getRealName(uuid);
                }

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                    @Override
                    public void run() {
                        final int minutes = plugin.getMySQLManager().getGlobalTime(uuid);

                        if (minutes < 0) {
                            sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                            return;
                        }

                        AutorankTools.sendColoredMessage(sender, args[1] + " has played for "
                                + AutorankTools.timeToString(minutes, Time.MINUTES) + " across all servers.");

                    }

                });
                return true;

            } else {
                if (player.hasPermission(AutorankPermission.EXCLUDE_FROM_PATHING)) {
                    sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(player.getName()));
                    return true;
                }

                final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

                if (uuid == null) {
                    sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                    return true;
                }

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                    @Override
                    public void run() {
                        final int minutes = plugin.getMySQLManager().getGlobalTime(uuid);

                        if (minutes < 0) {
                            sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
                            return;
                        }

                        AutorankTools.sendColoredMessage(sender, player.getName() + " has played for "
                                + AutorankTools.timeToString(minutes, Time.MINUTES) + " across all servers.");

                    }

                });
            }
        } else if (sender instanceof Player) {
            if (!this.hasPermission(AutorankPermission.CHECK_GLOBAL, sender)) {
                return true;
            }

            if (sender.hasPermission("autorank.exclude")) {
                sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender.getName()));
                return true;
            }

            final Player player = (Player) sender;

            final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    AutorankTools.sendColoredMessage(sender,
                            "You have played for " + AutorankTools
                                    .timeToString(plugin.getMySQLManager().getGlobalTime(uuid), Time.MINUTES)
                                    + " across all servers.");
                }
            });

        } else {
            AutorankTools.sendColoredMessage(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Check [player]'s global playtime.";
    }

    @Override
    public String getPermission() {
        return AutorankPermission.CHECK_GLOBAL;
    }

    @Override
    public String getUsage() {
        return "/ar gcheck [player]";
    }
}
