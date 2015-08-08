package me.armar.plugins.autorank.commands;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import me.armar.plugins.autorank.util.uuid.UUIDManager;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GlobalAddCommand extends AutorankCommand {

	private final Autorank plugin;

	public GlobalAddCommand(final Autorank instance) {
		this.setUsage("/ar gadd [player] [value]");
		this.setDesc("Add [value] to [player]'s global time");
		this.setPermission("autorank.gadd");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.gadd", sender)) {
			return true;
		}

		if (args.length < 3) {
			sender.sendMessage(Lang.INVALID_FORMAT
					.getConfigValue("/ar gadd <player> <value>"));
			return true;
		}

		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			sender.sendMessage(ChatColor.RED
					+ Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
			return true;
		}

		final UUID uuid = UUIDManager.getUUIDFromPlayer(args[1]);

		if (uuid == null) {
			sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
			return true;
		}

		int value = -1;

		if (args.length > 2) {

			final StringBuilder builder = new StringBuilder();

			for (int i = 2; i < args.length; i++) {
				builder.append(args[i]);
			}

			if (!builder.toString().contains("m")
					&& !builder.toString().contains("h")
					&& !builder.toString().contains("d")) {
				value = AutorankTools.stringtoInt(builder.toString().trim());
				value += plugin.getPlaytimes().getGlobalTime(uuid);
			} else {
				value = AutorankTools.stringToTime(builder.toString(),
						Time.MINUTES);
				value += plugin.getPlaytimes().getGlobalTime(uuid);
			}
		}

		if (value >= 0) {
			try {
				plugin.getPlaytimes().setGlobalTime(uuid, value);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			AutorankTools.sendColoredMessage(sender,
					Lang.PLAYTIME_CHANGED.getConfigValue(args[1], value + ""));
		} else {
			AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT
					.getConfigValue("/ar gadd [player] [value]"));
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
