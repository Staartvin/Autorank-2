package me.armar.plugins.autorank.commands;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

public class GlobalSetCommand extends AutorankCommand {

	private final Autorank plugin;

	public GlobalSetCommand(final Autorank instance) {
		this.setUsage("/ar gset [player] [value]");
		this.setDesc("Set [player]'s global time to [value].");
		this.setPermission("autorank.gset.other");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		int value = -1;

		if (args.length < 3) {
			sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar gset <player> <value>"));
			return true;
		}

		if (args.length > 2) {

			final StringBuilder builder = new StringBuilder();

			for (int i = 2; i < args.length; i++) {
				builder.append(args[i]);
			}

			if (!builder.toString().contains("m") && !builder.toString().contains("h")
					&& !builder.toString().contains("d") && !builder.toString().contains("s")) {
				value = AutorankTools.stringtoInt(builder.toString().trim());
			} else {

				if (builder.toString().contains("s")) {
					sender.sendMessage(
							ChatColor.RED + Lang.INVALID_FORMAT.getConfigValue("(h)ours, (m)inutes or (d)ays"));
					return true;
				}

				value = AutorankTools.stringToTime(builder.toString(), Time.MINUTES);
			}
		}

		if (value >= 0) {

			if (args[1].equalsIgnoreCase(sender.getName())) {
				if (!plugin.getCommandsManager().hasPermission("autorank.gset.self", sender)) {
					return true;
				}
			} else {
				if (!plugin.getCommandsManager().hasPermission("autorank.gset.other", sender)) {
					return true;
				}
			}

			final UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

			if (uuid == null) {
				sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
				return true;
			}

			if (plugin.getUUIDStorage().hasRealName(uuid)) {
				args[1] = plugin.getUUIDStorage().getRealName(uuid);
			}

			try {
				plugin.getPlaytimes().setGlobalTime(uuid, value);
			} catch (final SQLException e) {
				sender.sendMessage(Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
				return true;
			}

			AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(args[1],
					value + " " + Lang.MINUTE_PLURAL.getConfigValue() + "."));
		} else {
			AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue("/ar gset <player> <value>"));
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
			final String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
