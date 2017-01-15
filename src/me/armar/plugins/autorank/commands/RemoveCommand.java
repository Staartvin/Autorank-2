package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

/**
 * The command delegator for the '/ar remove' command.
 */
public class RemoveCommand extends AutorankCommand {

	private final Autorank plugin;

	public RemoveCommand(final Autorank instance) {
		this.setUsage("/ar remove [player] [value]");
		this.setDesc("Remove [value] from [player]'s time.");
		this.setPermission("autorank.remove");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.remove", sender)) {
			return true;
		}

		if (args.length < 3) {
			sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar rem <player> <value>"));
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

		int value = -1;

		if (args.length > 2) {

			final StringBuilder builder = new StringBuilder();

			for (int i = 2; i < args.length; i++) {
				builder.append(args[i]);
			}

			if (!builder.toString().contains("m") && !builder.toString().contains("h")
					&& !builder.toString().contains("d")) {
				value = -AutorankTools.stringtoInt(builder.toString().trim());
				value += plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid);
			} else {
				value = -AutorankTools.stringToTime(builder.toString(), Time.MINUTES);
				value += plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid);
			}
		}

		if (value >= 0) {
			plugin.getFlatFileManager().setLocalTime(TimeType.TOTAL_TIME, value, uuid);
			AutorankTools.sendColoredMessage(sender, Lang.PLAYTIME_CHANGED.getConfigValue(args[1], value + ""));
		} else {
			AutorankTools.sendColoredMessage(sender, Lang.INVALID_FORMAT.getConfigValue("/ar remove [player] [value]"));
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
