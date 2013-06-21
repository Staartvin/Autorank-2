package me.armar.plugins.autorank;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.language.Language;
import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;

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
		this.language = LanguageHandler.getLanguage();
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
			if (args.length == 1) {
				showHelpPages(sender, 1);	
			} else {
				int page = 1;
				try {
					page = Integer.parseInt(args[1]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid page number!");
					return true;
				}
				showHelpPages(sender, page);
			}
			return true;
		} else if (action.equalsIgnoreCase("check")) {
			if (args.length > 1) {

				if (!hasPermission("autorank.checkothers", sender)) {
					return true;
				}

				Player player = plugin.getServer().getPlayer(args[1]);
				if (player == null) {
					AutorankTools.sendColoredMessage(sender,  args[1] + language.getHasPlayedFor()
							+ AutorankTools.minutesToString(plugin.getTime(args[1])));
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

			if (value >= 0) {
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

			if (value >= 0) {
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
		} else if (action.equalsIgnoreCase("import")) {

			if (!hasPermission("autorank.import", sender)) {
				return true;
			}

			AutorankTools.sendColoredMessage(sender, language.getDataImported());
			plugin.getPlaytimes().importData();

			return true;
		} else if (action.equalsIgnoreCase("archive")) {
			
			if (!hasPermission("autorank.archive", sender)) {
				return true;
			}
			
			int rate = -1;
			
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "You need to give a number!");
				return true;
			}
			
			try {
				rate = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number!");
				return true;
			}
			
			if (rate <= 0) {
				sender.sendMessage(ChatColor.RED + "Value cannot be lower or equal to 0.");
				return true;
			}
			
			sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.YELLOW + plugin.getPlaytimes().archive(rate) + "" + ChatColor.GREEN + " records.");
			return true;
		}
			
		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW + "Use '/ar help' for a list of commands.");
		return true;
	}

	private void check(CommandSender sender, Player player) {
		Map<RankChange, List<Requirement>> failed = plugin
				.getPlayerChecker().getFailedRequirementsForApplicableGroup(
						player);

		Set<RankChange> keySet = failed.keySet();
		String playername = player.getName();

		String[] groups = plugin.getPermPlugHandler().getPermissionPlugin().getPlayerGroups(player);
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
					language.getNoNextRankup());
		} else {
			Iterator<RankChange> it = keySet.iterator();
			while (it.hasNext()) {
				RankChange rank = it.next();
				List<Requirement> reqs = failed.get(rank);

				if (reqs.size() == 0) {
					AutorankTools.sendColoredMessage(
							sender,
							language.getMeetsRequirements()
									+ rank.getRankTo()
									+ language.getRankedUpNow());
					plugin.getPlayerChecker().checkPlayer(player);
				} else {
					AutorankTools.sendColoredMessage(sender,
							language.getDoesntMeetRequirements()
									+ rank.getRankTo() + ":");

					for (Requirement req : reqs) {
						if (req != null)
							AutorankTools.sendColoredMessage(sender, "     - "
									+ req.getDescription());
					}
				}

			}
		}
	}
	
	private void showHelpPages(CommandSender sender, int page) {
		int maxPages = 2;
		if (page == 2) {
			sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");
			sender.sendMessage(ChatColor.AQUA + "/ar help <page> " + ChatColor.GRAY + "- Show a list of commands");
			sender.sendMessage(ChatColor.AQUA + "/ar reload " + ChatColor.GRAY + "- Reload the plugin");
			sender.sendMessage(ChatColor.AQUA + "/ar import " + ChatColor.GRAY + "- Import old data");
			sender.sendMessage(ChatColor.AQUA + "/ar archive <minimum> " + ChatColor.GRAY + "- Archive data with a minimum");
			sender.sendMessage(ChatColor.BLUE + "Page 2 of " + maxPages);
		} else {
			sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");
			sender.sendMessage(ChatColor.AQUA + "/ar check " + ChatColor.GRAY + "- Check your own status");
			sender.sendMessage(ChatColor.AQUA + 
					"/ar check [player] " + ChatColor.GRAY + "- Check [player]'s status");
			sender.sendMessage(ChatColor.AQUA + 
					"/ar leaderboard " + ChatColor.GRAY + "- Show the leaderboard");
			sender.sendMessage(ChatColor.AQUA + 
							"/ar set [player] [value] " + ChatColor.GRAY + "- Set [player]'s time to [value]");
			sender.sendMessage(ChatColor.AQUA + 
							"/ar add [player] [value] " + ChatColor.GRAY + "- Add [value] to [player]'s time");
			sender.sendMessage(ChatColor.AQUA + 
							"/ar remove [player] [value] " + ChatColor.GRAY + "- Remove [value] from [player]'s time");
			sender.sendMessage(ChatColor.AQUA + 
					"/ar debug " + ChatColor.GRAY + "- Shows debug information");
			sender.sendMessage(ChatColor.BLUE + "Page 1 of " + maxPages);
		}
	}

}
