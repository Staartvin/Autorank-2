package me.armar.plugins.autorank;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.language.Language;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.additionalrequirement.AdditionalRequirement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	private Autorank plugin;
	private Language language;

	public Commands(Autorank plugin) {
		this.plugin = plugin;
		this.language = plugin.getLanguageHandler().getLanguage();
	}

	private boolean hasPermission(String permission, CommandSender sender) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ language.getNoPermission(permission));
			return false;
		}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.BLUE
					+ "-----------------------------------------------------");
			sender.sendMessage(ChatColor.GOLD + "Developed by: "
					+ ChatColor.GRAY + plugin.getDescription().getAuthors());
			sender.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.GRAY
					+ plugin.getDescription().getVersion());
			sender.sendMessage(ChatColor.YELLOW
					+ "Type /ar help for a list of commands.");
			return true;
		}

		String action = args[0];
		if (action.equalsIgnoreCase("help")) {

			AutorankTools.sendColoredMessage(sender, "-- Autorank Commands --");
			AutorankTools.sendColoredMessage(sender,
					"/ar check - Check your own status");
			AutorankTools.sendColoredMessage(sender,
					"/ar check [player] - Check [player]'s status");
			AutorankTools.sendColoredMessage(sender,
					"/ar leaderboard - Show the leaderboard");
			AutorankTools
					.sendColoredMessage(sender,
							"/ar set [player] [value] - Set [player]'s time to [value]");
			AutorankTools
					.sendColoredMessage(sender,
							"/ar add [player] [value] - Add [value] to [player]'s time");
			AutorankTools
					.sendColoredMessage(sender,
							"/ar remove [player] [value] - Remove [value] from [player]'s time");
			AutorankTools.sendColoredMessage(sender,
					"/ar debug - Shows debug information");
			AutorankTools.sendColoredMessage(sender,
					"/ar reload - Reload the plugin");

			return true;
		} else if (action.equalsIgnoreCase("check")) {
			if (args.length > 1) {

				if (!hasPermission("autorank.checkothers", sender)) {
					return true;
				}

				Player player = plugin.getServer().getPlayer(args[1]);
				if (player == null) {
					AutorankTools.sendColoredMessage(sender,
							language.getPlayerNotOnline(args[1]));
				} else {
					check(sender, player);
				}
			} else if (sender instanceof Player) {
				if (!hasPermission("autorank.check", sender)) {
					return true;
				}
				Player player = (Player) sender;
				check(sender, player);
			} else {
				AutorankTools.sendColoredMessage(sender,
						language.getCannotCheckConsole());
			}
			return true;
		} else if (action.equalsIgnoreCase("leaderboard")
				|| action.equalsIgnoreCase("leaderboards")) {
			if (!hasPermission("autorank.leaderboard", sender)) {
				return true;
			}
			plugin.getLeaderboard().sendLeaderboard(sender);
			return true;
		} else if (action.equalsIgnoreCase("set")) {

			if (!hasPermission("autorank.set", sender)) {
				return true;
			}

			int value = -1;
			if (args.length > 2)
				try {
					value = AutorankTools.stringtoInt(args[2]);
				} catch (NumberFormatException e) {
				}

			if (value >= 0) {
				plugin.setTime(args[1], value);
				AutorankTools.sendColoredMessage(sender, language.getPlayTimeChanged(args[1], value));
			} else {
				AutorankTools.sendColoredMessage(sender,
						language.getInvalidFormat("/ar set [player] [value]"));
			}

			return true;
		} else if (action.equalsIgnoreCase("add")) {

			if (!hasPermission("autorank.add", sender)) {
				return true;
			}

			int value = -1;
			if (args.length > 2)
				try {
					value = AutorankTools.stringtoInt(args[2]);
					value += plugin.getTime(args[1]);
				} catch (NumberFormatException e) {
				}

			if (value >= 0 && sender instanceof Player) {
				plugin.setTime(args[1], value);
				AutorankTools.sendColoredMessage(sender, language.getPlayTimeChanged(args[1], value));
			} else {
				AutorankTools.sendColoredMessage(sender,
						language.getInvalidFormat("/ar add [player] [value]"));
			}

			return true;
		} else if (action.equalsIgnoreCase("remove")
				|| action.equalsIgnoreCase("rem")) {

			if (!hasPermission("autorank.remove", sender)) {
				return true;
			}

			int value = -1;
			if (args.length > 2)
				try {
					value = -AutorankTools.stringtoInt(args[2]);
					value += plugin.getTime(args[1]);
				} catch (NumberFormatException e) {
				}

			if (value >= 0 && sender instanceof Player) {
				plugin.setTime(args[1], value);
				AutorankTools.sendColoredMessage(sender, language.getPlayTimeChanged(args[1], value));
			} else {
				AutorankTools.sendColoredMessage(sender,
						language.getInvalidFormat("/ar remove [player] [value]"));
			}

			return true;
		} else if (action.equalsIgnoreCase("debug")) {

			if (!hasPermission("autorank.debug", sender)) {
				return true;
			}

			AutorankTools.sendColoredMessage(sender, "-- Autorank Debug --");
			AutorankTools.sendColoredMessage(sender, "Rank Changes");
			for (String change : plugin.getPlayerChecker().toStringArray()) {
				AutorankTools.sendColoredMessage(sender, change);
			}
			AutorankTools.sendColoredMessage(sender, "--------------------");

			return true;
		} else if (action.equalsIgnoreCase("reload")) {

			if (!hasPermission("autorank.reload", sender)) {
				return true;
			}

			AutorankTools.sendColoredMessage(sender, language.getAutorankReloaded());
			plugin.reload();

			return true;
		}

		return false;
	}

	private void check(CommandSender sender, Player player) {
		Map<RankChange, List<AdditionalRequirement>> failed = plugin
				.getPlayerChecker().getFailedRequirementsForApplicableGroup(
						player);

		Set<RankChange> keySet = failed.keySet();
		String playername = player.getName();

		String[] groups = plugin.getPermPlugHandler().getPlayerGroups(player);
		StringBuilder stringBuilder = new StringBuilder();
		// has played for
		stringBuilder.append(playername + language.getHasPlayedFor()
				+ AutorankTools.minutesToString(plugin.getTime(playername))
				+ ", ");
		// is in
		stringBuilder.append(language.getIsIn());
		if (groups.length == 0)
			stringBuilder.append(language.getNoGroups()); // No groups.
		else if (groups.length == 1)
			stringBuilder.append(language.getOneGroup()); // One group
		else
			stringBuilder.append(language.getMultipleGroups()); // Multiple groups

		boolean first = true;
		for (String group : groups) {
			if (!first) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(group);
			first = false;
		}

		AutorankTools.sendColoredMessage(sender, stringBuilder.toString());

		if (keySet.size() == 0) {
			AutorankTools.sendColoredMessage(sender,
					"and doesn't have a next rankup.");
		} else {
			Iterator<RankChange> it = keySet.iterator();
			while (it.hasNext()) {
				RankChange rank = it.next();
				List<AdditionalRequirement> reqs = failed.get(rank);

				if (reqs.size() == 0) {
					AutorankTools.sendColoredMessage(
							sender,
							"meets all the requirements for rank "
									+ rank.getRank()
									+ " and will now be ranked up.");
					plugin.getPlayerChecker().checkPlayer(player);
				} else {
					AutorankTools.sendColoredMessage(sender,
							"and doesn't meet the requirements for rank "
									+ rank.getRank() + ":");

					for (AdditionalRequirement req : reqs) {
						if (req != null)
							AutorankTools.sendColoredMessage(sender, "     - "
									+ req.getDescription());
					}
				}

			}
		}
	}

}
