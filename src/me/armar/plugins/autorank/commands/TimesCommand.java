package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

/**
 * The command delegator for the '/ar times' command.
 */
public class TimesCommand extends AutorankCommand {

	private final Autorank plugin;

	public TimesCommand(final Autorank instance) {
		this.setUsage("/ar times <player>");
		this.setDesc("Show the amount of time you played.");
		this.setPermission("autorank.times");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		// No player specified
		if (args.length == 1) {

		}

		String targetName = "";

		// A player specified a target
		if (args.length > 1) {

			if (!plugin.getCommandsManager().hasPermission("autorank.times.others", sender)) {
				return true;
			}

			targetName = args[1];

		} else if (sender instanceof Player) {
			if (!plugin.getCommandsManager().hasPermission("autorank.times", sender)) {
				return true;
			}

			targetName = sender.getName();

		} else {
			AutorankTools.sendColoredMessage(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
			return true;
		}

		final UUID uuid = plugin.getUUIDStorage().getStoredUUID(targetName);

		if (uuid == null) {
			sender.sendMessage(Lang.UNKNOWN_PLAYER.getConfigValue(targetName));
			return true;
		}

		// Now show data for target.
		targetName = plugin.getUUIDStorage().getRealName(uuid);

		if (targetName == null) {
			// This player has no real name stored -> use cached name
			targetName = plugin.getUUIDStorage().getCachedPlayerName(uuid);
		}

		final int daily = plugin.getFlatFileManager().getLocalTime(TimeType.DAILY_TIME, uuid);
		final int weekly = plugin.getFlatFileManager().getLocalTime(TimeType.WEEKLY_TIME, uuid);
		final int monthly = plugin.getFlatFileManager().getLocalTime(TimeType.MONTHLY_TIME, uuid);
		final int total = plugin.getFlatFileManager().getLocalTime(TimeType.TOTAL_TIME, uuid);

		sender.sendMessage(Lang.AR_TIMES_HEADER.getConfigValue(targetName));
		sender.sendMessage(Lang.AR_TIMES_PLAYER_PLAYED.getConfigValue(targetName));
		sender.sendMessage(Lang.AR_TIMES_TODAY.getConfigValue(AutorankTools.timeToString(daily, Time.MINUTES)));
		sender.sendMessage(Lang.AR_TIMES_THIS_WEEK.getConfigValue(AutorankTools.timeToString(weekly, Time.MINUTES)));
		sender.sendMessage(Lang.AR_TIMES_THIS_MONTH.getConfigValue(AutorankTools.timeToString(monthly, Time.MINUTES)));
		sender.sendMessage(Lang.AR_TIMES_TOTAL.getConfigValue(AutorankTools.timeToString(total, Time.MINUTES)));

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
