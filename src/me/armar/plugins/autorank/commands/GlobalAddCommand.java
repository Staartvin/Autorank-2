package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import net.md_5.bungee.api.ChatColor;

/**
 * The command delegator for the '/ar gadd' command.
 */
public class GlobalAddCommand extends AutorankCommand {

    private final Autorank plugin;

    public GlobalAddCommand(final Autorank instance) {
        this.setUsage("/ar gadd [player] [value]");
        this.setDesc("Add [value] to [player]'s global time");
        this.setPermission("autorank.gadd");

        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        
        if (!plugin.getCommandsManager().hasPermission("autorank.gadd", sender)) {
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar gadd <player> <value>"));
            return true;
        }

        if (!plugin.getMySQLManager().isMySQLEnabled()) {
            sender.sendMessage(ChatColor.RED + Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
            return true;
        }

        final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

        if (uuid == null) {
            sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
            return true;
        }

        if (plugin.getUUIDStorage().hasRealName(uuid)) {
            args[1] = plugin.getUUIDStorage().getRealName(uuid);
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                int value = 0;
                
                if (args.length > 2) {

                    final StringBuilder builder = new StringBuilder();

                    for (int i = 2; i < args.length; i++) {
                        builder.append(args[i]);
                    }
                    
                    int changeValue = 0;

                    if (!builder.toString().contains("m") && !builder.toString().contains("h")
                            && !builder.toString().contains("d")) {
                        changeValue = AutorankTools.stringtoInt(builder.toString().trim());
                    } else {
                        changeValue = AutorankTools.stringToTime(builder.toString(), Time.MINUTES);   
                    }
                    
                    if (changeValue < 0) {
                        value = -1;
                    } else {
                        System.out.println("DATA: " + plugin.getMySQLManager().getFreshGlobalTime(uuid));
                        System.out.println("CHANGE VALUE: " + changeValue);
                        value += plugin.getMySQLManager().getFreshGlobalTime(uuid) + changeValue;
                    }
                }

                if (value >= 0) {
                    if (!plugin.getMySQLManager().setGlobalTime(uuid, value)) {
                        sender.sendMessage(Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
                        return;
                    }
                    AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(args[1], value + ""));
                } else {
                    AutorankTools.sendColoredMessage(sender,
                            Lang.INVALID_FORMAT.getConfigValue("/ar gadd [player] [value]"));
                }
            }

        });

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
