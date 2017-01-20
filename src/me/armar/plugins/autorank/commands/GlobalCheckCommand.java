package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

/**
 * The command delegator for the '/ar gcheck' command.
 */
public class GlobalCheckCommand extends AutorankCommand {

    private final Autorank plugin;

    public GlobalCheckCommand(final Autorank instance) {
        this.setUsage("/ar gcheck [player]");
        this.setDesc("Check [player]'s global playtime.");
        this.setPermission("autorank.gcheck");

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

            if (!plugin.getCommandsManager().hasPermission("autorank.checkothers", sender)) {
                return true;
            }

            final Player player = plugin.getServer().getPlayer(args[1]);
            if (player == null) {

                final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

                if (plugin.getUUIDStorage().hasRealName(uuid)) {
                    args[1] = plugin.getUUIDStorage().getRealName(uuid);
                }

                if (uuid == null) {
                    sender.sendMessage(Lang.PLAYER_NOT_ONLINE.getConfigValue(args[1]));
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

                        AutorankTools.sendColoredMessage(sender, args[1] + " has played for "
                                + AutorankTools.timeToString(minutes, Time.MINUTES) + " across all servers.");

                    }

                });
                return true;

            } else {
                if (player.hasPermission("autorank.exclude")) {
                    sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(player.getName()));
                    return true;
                }

                final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

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

                // Do no check. Players can't be checked on global times (at the
                // moment)
                // check(sender, player);
            }
        } else if (sender instanceof Player) {
            if (!plugin.getCommandsManager().hasPermission("autorank.check", sender)) {
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
        // TODO Auto-generated method stub
        return null;
    }

}
