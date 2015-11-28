package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SetCommand extends AutorankCommand {

	private final Autorank plugin;

	public SetCommand(final Autorank instance) {
		this.setUsage("/ar set [player] [value]");
		this.setDesc("Set [player]'s time to [value].");
		this.setPermission("autorank.set.other");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		int value = -1;

		if (args.length < 3) {
			sender.sendMessage(Lang.INVALID_FORMAT
					.getConfigValue("/ar set <player> <value>"));
			return true;
		}

		if (args.length > 2) {

			final StringBuilder builder = new StringBuilder();

			for (int i = 2; i < args.length; i++) {
				builder.append(args[i]);
			}

			if (!builder.toString().contains("m")
					&& !builder.toString().contains("h")
					&& !builder.toString().contains("d")
					&& !builder.toString().contains("s")) {
				value = AutorankTools.stringtoInt(builder.toString().trim());
			} else {

				if (builder.toString().contains("s")) {
					sender.sendMessage(ChatColor.RED
							+ Lang.INVALID_FORMAT
									.getConfigValue("(h)ours, (m)inutes or (d)ays"));
					return true;
				}

				value = AutorankTools.stringToTime(builder.toString(),
						Time.MINUTES);
			}
		}

		if (value >= 0) {

			if (args[1].equalsIgnoreCase(sender.getName())) {
				if (!plugin.getCommandsManager().hasPermission(
						"autorank.set.self", sender)) {
					return true;
				}
			} else {
				if (!plugin.getCommandsManager().hasPermission(
						"autorank.set.other", sender)) {
					return true;
				}
			}

			final UUID uuid = UUIDManager.getUUIDFromPlayer(args[1]);

			//System.out.print("Name of UUID: " + UUIDManager.getPlayerFromUUID(UUID.fromString("fc914960-7aa1-3ae2-a3ee-70f5ac1e81e5")));

			if (uuid == null) {
				sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(args[1]));
				return true;
			}

			plugin.getPlaytimes().setLocalTime(uuid, value);
			AutorankTools.sendColoredMessage(
					sender,
					Lang.PLAYTIME_CHANGED.getConfigValue(args[1], value
							+ " minutes"));
		} else {
			AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT
					.getConfigValue("/ar set <player> <value>"));
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender,
			final Command cmd, final String commandLabel, final String[] args) {
		// TODO Auto-generated method stub

		return null;
	}

}
