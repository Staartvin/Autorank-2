package me.armar.plugins.autorank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.language.Language;
import me.armar.plugins.autorank.language.LanguageHandler;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;

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
					sender.sendMessage(ChatColor.RED + "'" + args[1]
							+ "' is not a valid page number!");
					return true;
				}
				showHelpPages(sender, page);
			}
			return true;
		} else if (action.equalsIgnoreCase("check")) {
			// This is a local check. It will not show you the database numbers
			if (args.length > 1) {

				if (!hasPermission("autorank.checkothers", sender)) {
					return true;
				}

				Player player = plugin.getServer().getPlayer(args[1]);
				if (player == null) {
					AutorankTools.sendColoredMessage(
							sender,
							args[1]
									+ language.getHasPlayedFor()
									+ AutorankTools.minutesToString(plugin
											.getLocalTime(args[1])));
				} else {
					if (player.hasPermission("autorank.exclude")) {
						sender.sendMessage(ChatColor.RED + args[1]
								+ " is excluded from ranking!");
						return true;
					}
					check(sender, player);
				}
			} else if (sender instanceof Player) {
				if (!hasPermission("autorank.check", sender)) {
					return true;
				}

				if (sender.hasPermission("autorank.exclude")) {
					sender.sendMessage(ChatColor.RED
							+ "You are excluded from ranking!");
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
				plugin.setLocalTime(args[1], value);
				AutorankTools.sendColoredMessage(sender,
						language.getPlayTimeChanged(args[1], value));
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
					value += plugin.getLocalTime(args[1]);
				} catch (NumberFormatException e) {
				}

			if (value >= 0) {
				plugin.setLocalTime(args[1], value);
				AutorankTools.sendColoredMessage(sender,
						language.getPlayTimeChanged(args[1], value));
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
					value += plugin.getLocalTime(args[1]);
				} catch (NumberFormatException e) {
				}

			if (value >= 0) {
				plugin.setLocalTime(args[1], value);
				AutorankTools.sendColoredMessage(sender,
						language.getPlayTimeChanged(args[1], value));
			} else {
				AutorankTools.sendColoredMessage(sender, language
						.getInvalidFormat("/ar remove [player] [value]"));
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

			AutorankTools.sendColoredMessage(sender,
					language.getAutorankReloaded());
			plugin.reload();

			return true;
		} else if (action.equalsIgnoreCase("import")) {

			if (!hasPermission("autorank.import", sender)) {
				return true;
			}

			AutorankTools
					.sendColoredMessage(sender, language.getDataImported());
			plugin.getPlaytimes().importData();

			return true;
		} else if (action.equalsIgnoreCase("archive")) {

			if (!hasPermission("autorank.archive", sender)) {
				return true;
			}

			int rate = -1;

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED
						+ "You need to specify a time!");
				return true;
			}

			rate = AutorankTools.stringToMinutes(args[1]);

			if (rate <= 0) {
				sender.sendMessage(ChatColor.RED
						+ "Time is not correctly formatted!");
				sender.sendMessage(ChatColor.YELLOW
						+ "Example: /ar archive 10d/10h/10m");
				return true;
			}

			sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.YELLOW
					+ plugin.getPlaytimes().archive(rate) + ""
					+ ChatColor.GREEN + " records below " + ChatColor.YELLOW
					+ AutorankTools.minutesToString(rate) + ChatColor.GREEN
					+ ".");
			return true;
		} else if (action.equalsIgnoreCase("gcheck")) {
			// This is a global check. It will not show you the database numbers
			if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
				sender.sendMessage(ChatColor.RED
						+ "MySQL is not enabled and therefore global time does not exist!");
				return true;
			}

			if (args.length > 1) {

				if (!hasPermission("autorank.checkothers", sender)) {
					return true;
				}

				Player player = plugin.getServer().getPlayer(args[1]);
				if (player == null) {
					AutorankTools.sendColoredMessage(
							sender,
							args[1]
									+ language.getHasPlayedFor()
									+ AutorankTools.minutesToString(plugin
											.getGlobalTime(args[1]))
									+ " across all servers.");
				} else {
					if (player.hasPermission("autorank.exclude")) {
						sender.sendMessage(ChatColor.RED + args[1]
								+ " is excluded from ranking!");
						return true;
					}

					// Do no check. Players can't be checked on global times (at the moment)
					//check(sender, player);
				}
			} else if (sender instanceof Player) {
				if (!hasPermission("autorank.check", sender)) {
					return true;
				}

				if (sender.hasPermission("autorank.exclude")) {
					sender.sendMessage(ChatColor.RED
							+ "You are excluded from ranking!");
					return true;
				}
				Player player = (Player) sender;
				AutorankTools.sendColoredMessage(
						sender,
						"You have played for "
								+ AutorankTools.minutesToString(plugin
										.getGlobalTime(player.getName()))
								+ " across all servers.");

			} else {
				AutorankTools.sendColoredMessage(sender,
						language.getCannotCheckConsole());
			}
			return true;
		} else if (action.equalsIgnoreCase("complete")) {
			
			// Implemented /ar complete #
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect command usage!");
				sender.sendMessage(ChatColor.YELLOW + "Usage: /ar complete #");
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED
						+ "You are a robot! You can't rank up, silly..");
				return true;
			}

			if (!plugin.getConfigHandler().usePartialCompletion()) {
				sender.sendMessage(ChatColor.RED
						+ "You cannot use this command as this server has not enabled partial completion!");
				return true;
			}

			if (!hasPermission("autorank.complete", sender))
				return true;

			Player player = (Player) sender;

			int completionID = 0;

			try {
				completionID = Integer.parseInt(args[1]);

				if (completionID < 1) {
					completionID = 1;
				}
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + "'" + args[1]
						+ "' is not a valid id!");
				return true;
			}

			// Check if the latest known group is the current group. Otherwise, reset progress
			String currentGroup = plugin.getPermPlugHandler()
					.getPermissionPlugin().getPlayerGroups(player)[0];
			String latestKnownGroup = plugin.getRequirementHandler()
					.getLastKnownGroup(player.getName());

			if (!latestKnownGroup.equalsIgnoreCase(currentGroup)) {
				// Reset progress and update latest known group
				plugin.getRequirementHandler().setPlayerProgress(
						player.getName(), new ArrayList<Integer>());
				plugin.getRequirementHandler().setLastKnownGroup(
						player.getName(), currentGroup);
			}

			Map<RankChange, List<Requirement>> failed = plugin
					.getPlayerChecker().getAllRequirements(player);
			Set<RankChange> keySet = failed.keySet();

			if (keySet.size() == 0) {
				player.sendMessage(ChatColor.RED
						+ "You don't have a next rank up!");
				return true;
			}

			List<Requirement> requirements;
			for (Iterator<RankChange> it = keySet.iterator(); it.hasNext();) {
				RankChange rank = it.next();
				requirements = failed.get(rank);

				// Rank player as he has fulfilled all requirements
				if (requirements.size() == 0) {
					player.sendMessage(ChatColor.GREEN
							+ "You don't have any requirements left and you are ranked now!");
					plugin.getPlayerChecker().checkPlayer(player);
				} else {
					// Get the specified requirement
					if (completionID > requirements.size()) {
						completionID = requirements.size();
					}

					// Human logic = first number is 1 not 0.
					Requirement req = requirements.get((completionID - 1));

					if (plugin.getRequirementHandler().hasCompletedRequirement(
							(completionID - 1), player.getName())) {
						player.sendMessage(ChatColor.RED
								+ "You have already completed this requirement!");
						return true;
					}

					if (req.meetsRequirement(player)) {
						// Player meets requirement
						player.sendMessage(ChatColor.GREEN
								+ "You have successfully completed requirement "
								+ ChatColor.GOLD + completionID
								+ ChatColor.GREEN + ":");
						player.sendMessage(ChatColor.AQUA
								+ req.getDescription());

						List<Result> results = req.getResults();

						// Apply results of that requirement
						for (Result realResult : results) {
							if (realResult.applyResult(player))
								;
						}

						// Log that a player has passed this requirement
						plugin.getRequirementHandler().addPlayerProgress(
								player.getName(), (completionID - 1));

					} else {
						// player does not meet requirements
						player.sendMessage(ChatColor.RED
								+ "You do not meet requirements for #"
								+ ChatColor.GOLD + completionID + ChatColor.RED
								+ ":");
						player.sendMessage(ChatColor.AQUA
								+ req.getDescription());
					}

					return true;
				}

			}
		}

		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW
				+ "Use '/ar help' for a list of commands.");
		return true;
	}

	private void check(CommandSender sender, Player player) {

		// Check if the latest known group is the current group. Otherwise, reset progress
		String currentGroup = plugin.getPermPlugHandler().getPermissionPlugin()
				.getPlayerGroups(player)[0];
		String latestKnownGroup = plugin.getRequirementHandler()
				.getLastKnownGroup(player.getName());

		if (!latestKnownGroup.equalsIgnoreCase(currentGroup)) {
			// Reset progress and update latest known group
			plugin.getRequirementHandler().setPlayerProgress(player.getName(),
					new ArrayList<Integer>());
			plugin.getRequirementHandler().setLastKnownGroup(player.getName(),
					currentGroup);
		}

		// Change the way requirements are shown. When a player has completed a requirement, it will be green, otherwise it will be red.
		Map<RankChange, List<Requirement>> failed = plugin.getPlayerChecker()
				.getAllRequirements(player);

		Set<RankChange> keySet = failed.keySet();
		String playername = player.getName();

		String[] groups = plugin.getPermPlugHandler().getPermissionPlugin()
				.getPlayerGroups(player);
		StringBuilder stringBuilder = new StringBuilder();
		// has played for
		stringBuilder
				.append(playername
						+ language.getHasPlayedFor()
						+ AutorankTools.minutesToString(plugin
								.getLocalTime(playername)) + ", ");
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
			AutorankTools
					.sendColoredMessage(sender, language.getNoNextRankup());
		} else {
			Iterator<RankChange> it = keySet.iterator();
			while (it.hasNext()) {
				RankChange rank = it.next();
				List<Requirement> reqs = failed.get(rank);

				boolean onlyOptional = true;
				boolean meetsAllRequirements = true;

				for (Requirement req : reqs) {
					if (!req.isOptional())
						onlyOptional = false;
					else
						continue;
				}

				for (Requirement req : reqs) {
					if (!req.meetsRequirement(player)) {
						meetsAllRequirements = false;
					}
				}

				if (meetsAllRequirements || onlyOptional) {
					AutorankTools.sendColoredMessage(sender,
							language.getMeetsRequirements() + rank.getRankTo()
									+ language.getRankedUpNow());
					plugin.getPlayerChecker().checkPlayer(player);
				} else {
					AutorankTools.sendColoredMessage(
							sender,
							language.getDoesntMeetRequirements()
									+ rank.getRankTo() + ":");

					for (int i = 0; i < reqs.size(); i++) {
						Requirement req = reqs.get(i);

						if (req != null) {
							StringBuilder message = new StringBuilder("     "
									+ ChatColor.GOLD + (i + 1) + ". ");
							if (req.meetsRequirement(player)) {
								message.append(ChatColor.RED
										+ req.getDescription() + ChatColor.BLUE
										+ " (Done)");
							} else {
								message.append(ChatColor.RED
										+ req.getDescription());
							}

							if (req.isOptional()) {
								message.append(ChatColor.AQUA + " (Optional)");
							}
							AutorankTools.sendColoredMessage(sender,
									message.toString());

						}
					}
				}

			}
		}
	}

	private void showHelpPages(CommandSender sender, int page) {
		int maxPages = 2;
		if (page == 2) {
			sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");
			sender.sendMessage(ChatColor.AQUA + "/ar help <page> "
					+ ChatColor.GRAY + "- Show a list of commands");
			sender.sendMessage(ChatColor.AQUA + "/ar reload " + ChatColor.GRAY
					+ "- Reload the plugin");
			sender.sendMessage(ChatColor.AQUA + "/ar import " + ChatColor.GRAY
					+ "- Import old data");
			sender.sendMessage(ChatColor.AQUA + "/ar archive <minimum> "
					+ ChatColor.GRAY + "- Archive data with a minimum");
			sender.sendMessage(ChatColor.AQUA + "/ar debug " + ChatColor.GRAY
					+ "- Shows debug information");
			sender.sendMessage(ChatColor.AQUA + "/ar complete #" + ChatColor.GRAY
					+ "- Complete a requirement at this moment");
			sender.sendMessage(ChatColor.BLUE + "Page 2 of " + maxPages);
		} else {
			sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");
			sender.sendMessage(ChatColor.AQUA + "/ar check " + ChatColor.GRAY
					+ "- Check your own status");
			sender.sendMessage(ChatColor.AQUA + "/ar check [player] "
					+ ChatColor.GRAY + "- Check [player]'s status");
			sender.sendMessage(ChatColor.AQUA + "/ar leaderboard "
					+ ChatColor.GRAY + "- Show the leaderboard");
			sender.sendMessage(ChatColor.AQUA + "/ar set [player] [value] "
					+ ChatColor.GRAY + "- Set [player]'s time to [value]");
			sender.sendMessage(ChatColor.AQUA + "/ar add [player] [value] "
					+ ChatColor.GRAY + "- Add [value] to [player]'s time");
			sender.sendMessage(ChatColor.AQUA + "/ar remove [player] [value] "
					+ ChatColor.GRAY + "- Remove [value] from [player]'s time");
			sender.sendMessage(ChatColor.AQUA + "/ar gcheck [player] "
					+ ChatColor.GRAY + "- Check [player]'s global playtime");
			sender.sendMessage(ChatColor.BLUE + "Page 1 of " + maxPages);
		}
	}
}
