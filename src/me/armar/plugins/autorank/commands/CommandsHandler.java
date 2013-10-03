package me.armar.plugins.autorank.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsHandler implements CommandExecutor {

	private Autorank plugin;

	public CommandsHandler(Autorank plugin) {
		this.plugin = plugin;
	}

	private boolean hasPermission(String permission, CommandSender sender) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ Lang.NO_PERMISSION
							.getConfigValue(new String[] { permission }));
			return false;
		}
		return true;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd,
			String label, String[] args) {
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
					sender.sendMessage(ChatColor.RED
							+ Lang.INVALID_NUMBER
									.getConfigValue(new String[] { args[1] }));
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
									+ Lang.HAS_PLAYED_FOR.getConfigValue(null)
									+ AutorankTools.minutesToString(plugin
											.getLocalTime(args[1])));
				} else {
					if (AutorankTools.isExcluded(player)) {
						sender.sendMessage(ChatColor.RED
								+ Lang.PLAYER_IS_EXCLUDED
										.getConfigValue(new String[] { args[1] }));
						return true;
					}
					check(sender, player);
				}
			} else if (sender instanceof Player) {
				if (!hasPermission("autorank.check", sender)) {
					return true;
				}

				if (AutorankTools.isExcluded((Player) sender)) {
					sender.sendMessage(ChatColor.RED
							+ Lang.PLAYER_IS_EXCLUDED
									.getConfigValue(new String[] { sender
											.getName() }));
					return true;
				}
				Player player = (Player) sender;
				check(sender, player);
			} else {
				AutorankTools.sendColoredMessage(sender,
						Lang.CANNOT_CHECK_CONSOLE.getConfigValue(null));
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

			int value = -1;
			if (args.length > 2)
				try {
					value = AutorankTools.stringtoInt(args[2]);
				} catch (NumberFormatException e) {
				}

			if (value >= 0) {

				if (args[1].equalsIgnoreCase(sender.getName())) {
					if (!hasPermission("autorank.set.self", sender)) {
						return true;
					}
				} else {
					if (!hasPermission("autorank.set.other", sender)) {
						return true;
					}
				}

				plugin.setLocalTime(args[1], value);
				AutorankTools.sendColoredMessage(
						sender,
						Lang.PLAYTIME_CHANGED.getConfigValue(new String[] {
								args[1], value + "" }));
			} else {
				AutorankTools
						.sendColoredMessage(
								sender,
								Lang.INVALID_FORMAT
										.getConfigValue(new String[] { "/ar set [player] [value]" }));
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
				AutorankTools.sendColoredMessage(
						sender,
						Lang.PLAYTIME_CHANGED.getConfigValue(new String[] {
								args[1], value + "" }));
			} else {
				AutorankTools
						.sendColoredMessage(
								sender,
								Lang.INVALID_FORMAT
										.getConfigValue(new String[] { "/ar add [player] [value]" }));
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
				AutorankTools.sendColoredMessage(
						sender,
						Lang.PLAYTIME_CHANGED.getConfigValue(new String[] {
								args[1], value + "" }));
			} else {
				AutorankTools
						.sendColoredMessage(
								sender,
								Lang.INVALID_FORMAT
										.getConfigValue(new String[] { "/ar remove [player] [value]" }));
			}

			return true;
		} else if (action.equalsIgnoreCase("debug")) {

			// This will create a 'debug.txt' file containing a lot of information about the plugin
			if (!hasPermission("autorank.debug", sender)) {
				return true;
			}

			plugin.getServer().getScheduler()
					.runTaskAsynchronously(plugin, new Runnable() {
						public void run() {
							String fileName = plugin.getDebugger()
									.createDebugFile();

							sender.sendMessage(ChatColor.GREEN + "Debug file '"
									+ fileName + "' created!");
						}
					});

			return true;
		} else if (action.equalsIgnoreCase("reload")) {

			if (!hasPermission("autorank.reload", sender)) {
				return true;
			}

			AutorankTools.sendColoredMessage(sender,
					Lang.AUTORANK_RELOADED.getConfigValue(null));
			plugin.reload();

			return true;
		} else if (action.equalsIgnoreCase("import")) {

			if (!hasPermission("autorank.import", sender)) {
				return true;
			}

			AutorankTools.sendColoredMessage(sender,
					Lang.DATA_IMPORTED.getConfigValue(null));
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
						+ Lang.INVALID_FORMAT
								.getConfigValue(new String[] { "/ar archive 10d/10h/10m" }));
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
						+ Lang.MYSQL_IS_NOT_ENABLED.getConfigValue(null));
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
									+ Lang.HAS_PLAYED_FOR.getConfigValue(null)
									+ AutorankTools.minutesToString(plugin
											.getGlobalTime(args[1]))
									+ " across all servers.");
				} else {
					if (player.hasPermission("autorank.exclude")) {
						sender.sendMessage(ChatColor.RED
								+ Lang.PLAYER_IS_EXCLUDED
										.getConfigValue(new String[] { args[1] }));
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
							+ Lang.PLAYER_IS_EXCLUDED
									.getConfigValue(new String[] { sender
											.getName() }));
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
						Lang.CANNOT_CHECK_CONSOLE.getConfigValue(null));
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
				player.sendMessage(ChatColor.RED
						+ Lang.INVALID_NUMBER
								.getConfigValue(new String[] { args[1] }));
				return true;
			}

			// Check if the latest known group is the current group. Otherwise, reset progress
			String currentGroup = plugin.getPermPlugHandler()
					.getPermissionPlugin().getPlayerGroups(player)[0];
			String latestKnownGroup = plugin.getRequirementHandler()
					.getLastKnownGroup(player.getName());

			if (latestKnownGroup == null) {
				plugin.getRequirementHandler().setLastKnownGroup(
						player.getName(), currentGroup);

				latestKnownGroup = currentGroup;
			}

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
							+ "You don't have any requirements left.");
					return true;
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
								+ Lang.ALREADY_COMPLETED_REQUIREMENT
										.getConfigValue(null));
						return true;
					}

					if (req.meetsRequirement(player)) {
						// Player meets requirement
						player.sendMessage(ChatColor.GREEN
								+ Lang.SUCCESSFULLY_COMPLETED_REQUIREMENT
										.getConfigValue(new String[] { completionID
												+ "" }));
						player.sendMessage(ChatColor.AQUA
								+ req.getDescription());

						// Run results
						plugin.getRequirementHandler().runResults(req, player);

						// Log that a player has passed this requirement
						plugin.getRequirementHandler().addPlayerProgress(
								player.getName(), (completionID - 1));

					} else {
						// player does not meet requirements
						player.sendMessage(ChatColor.RED
								+ Lang.DO_NOT_MEET_REQUIREMENTS_FOR
										.getConfigValue(new String[] { completionID
												+ "" }));
						player.sendMessage(ChatColor.AQUA
								+ req.getDescription());
						player.sendMessage(ChatColor.GREEN + "Current: "
								+ ChatColor.GOLD + req.getProgress(player));
					}

					return true;
				}

			}
		} else if (action.equalsIgnoreCase("sync")) {
			if (!hasPermission("autorank.sync", sender))
				return true;

			if (!plugin.getConfigHandler().useMySQL()) {
				sender.sendMessage(ChatColor.RED + "MySQL is not being used!");
				return true;
			}

			sender.sendMessage(ChatColor.RED
					+ "You do not have to use this command regularly. Use this only one time per server.");

			// Do this async as we are accessing mysql database.
			plugin.getServer().getScheduler()
					.runTaskAsynchronously(plugin, new Runnable() {

						@Override
						public void run() {
							// Update all mysql records
							for (String player : plugin.getPlaytimes()
									.getKeys()) {
								if (plugin.getPlaytimes().getLocalTime(player) <= 0)
									continue;

								int localTime = plugin.getPlaytimes()
										.getLocalTime(player);
								int globalTime = plugin.getPlaytimes()
										.getGlobalTime(player);

								// Update record
								try {
									plugin.getPlaytimes().setGlobalTime(player,
											localTime + globalTime);
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							sender.sendMessage(ChatColor.GREEN
									+ "Successfully updated MySQL records!");
						}
					});
			return true;
		} else if (action.equalsIgnoreCase("syncstats")) {
			if (!hasPermission("autorank.syncstats", sender))
				return true;
			
			if (!plugin.getStatsHandler().isEnabled()) {
				sender.sendMessage(ChatColor.RED + "Stats is not enabled!");
				return true;
			}
			
			int count = 0;
			
			// Sync playtime of every player
			for (String entry: plugin.getPlaytimes().getKeys()) {
				
				// Time is stored in seconds
				int statsPlayTime = plugin.getStatsHandler().getStatsAPI().getPlaytime(entry);
				
				if (statsPlayTime <= 0) {
					System.out.print("LOW");
					continue;
				}
				
				// Check to see if the time actually changed.
				if ((statsPlayTime / 60) != plugin.getPlaytimes().getLocalTime(entry)) {
					
					// Update time
					plugin.getPlaytimes().setLocalTime(entry, statsPlayTime);
					
					// Increment count
					count++;
				}
			}
			
			sender.sendMessage(ChatColor.GREEN + (count + " entries have been updated!"));
			return true;
		} else if (action.equalsIgnoreCase("forcecheck")) {
			if (!hasPermission("autorank.forcecheck", sender)) return true;
			
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect command usage!");
				sender.sendMessage(ChatColor.YELLOW + "Usage: /ar forcecheck <player>");
				return true;
			}
			
			String target = args[1];
			Player targetPlayer = plugin.getServer().getPlayer(target);
			
			if (targetPlayer == null) {
				sender.sendMessage(ChatColor.RED + "Player " + target + " could not be found!");
				return true;
			}
			
			if (AutorankTools.isExcluded(targetPlayer)) {
				sender.sendMessage(ChatColor.RED + "This player is excluded from ranking!");
				return true;
			}
			
			// Check the player
			plugin.getPlayerChecker().checkPlayer(targetPlayer);
			
			// Let checker know that we checked.
			sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " checked!");
			
			return true;
		}

		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW
				+ "Use '/ar help' for a list of commands.");
		return true;
	}

	private void check(CommandSender sender, Player player) {

		// Check if the latest known group is the current group. Otherwise, reset progress
		String currentGroup = plugin.getPermPlugHandler().getPermissionPlugin()
				.getWorldGroups(player, player.getWorld().getName())[0];
		String latestKnownGroup = plugin.getRequirementHandler()
				.getLastKnownGroup(player.getName());

		if (latestKnownGroup == null) {
			plugin.getRequirementHandler().setLastKnownGroup(player.getName(),
					currentGroup);

			latestKnownGroup = currentGroup;
		}
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
						+ Lang.HAS_PLAYED_FOR.getConfigValue(null)
						+ AutorankTools.minutesToString(plugin
								.getLocalTime(playername)) + ", ");
		// is in
		stringBuilder.append(Lang.IS_IN.getConfigValue(null));
		if (groups.length == 0)
			stringBuilder.append(Lang.NO_GROUPS.getConfigValue(null)); // No groups.
		else if (groups.length == 1)
			stringBuilder.append(Lang.ONE_GROUP.getConfigValue(null)); // One group
		else
			stringBuilder.append(Lang.MULTIPLE_GROUPS.getConfigValue(null)); // Multiple groups

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
					Lang.NO_NEXT_RANK.getConfigValue(null));
		} else {
			Iterator<RankChange> it = keySet.iterator();
			while (it.hasNext()) {
				RankChange rank = it.next();
				List<Requirement> reqs = failed.get(rank);

				boolean onlyOptional = true;
				boolean meetsAllRequirements = true;
				List<Integer> metRequirements = new ArrayList<Integer>();

				for (Requirement req : reqs) {
					if (!req.isOptional())
						onlyOptional = false;
				}

				for (Requirement req : reqs) {
					int reqID = req.getReqId();

					if (req.useAutoCompletion()) {
						// Do auto complete
						if (req.meetsRequirement(player)) {
							// Player meets the requirement -> give him results

							if (!plugin.getRequirementHandler()
									.hasCompletedRequirement(reqID,
											player.getName())) {
								plugin.getRequirementHandler()
										.addPlayerProgress(player.getName(),
												reqID);

								// Run results
								plugin.getRequirementHandler().runResults(req,
										player);
							}
							metRequirements.add(reqID);
							continue;
						} else {
							// Player does not meet requirements, but has done this already
							if (plugin.getRequirementHandler()
									.hasCompletedRequirement(reqID,
											player.getName())) {
								metRequirements.add(reqID);
								continue;
							}

							// Player does not meet requirements -> do nothing
							meetsAllRequirements = false;
							continue;
						}
					} else {
						// Do not auto complete
						if (plugin.getRequirementHandler()
								.hasCompletedRequirement(reqID,
										player.getName())) {
							// Player has completed requirement
							metRequirements.add(reqID);
							continue;
						} else {
							meetsAllRequirements = false;
							continue;
						}
					}
				}
				String reqMessage = rank.getRankTo() == null ? Lang.MEETS_ALL_REQUIREMENTS_WITHOUT_RANK_UP
						.getConfigValue(null) : Lang.MEETS_ALL_REQUIREMENTS
						.getConfigValue(new String[] { rank.getRankTo() });

				if (meetsAllRequirements || onlyOptional) {

					AutorankTools.sendColoredMessage(sender, reqMessage
							+ Lang.RANKED_UP_NOW.getConfigValue(null));
					plugin.getPlayerChecker().checkPlayer(player);
				} else {
					AutorankTools.sendColoredMessage(sender,
							Lang.REQUIREMENTS_TO_RANK.getConfigValue(null));

					for (int i = 0; i < reqs.size(); i++) {
						Requirement req = reqs.get(i);
						int reqID = req.getReqId();

						if (req != null) {
							StringBuilder message = new StringBuilder("     "
									+ ChatColor.GOLD + (i + 1) + ". ");
							if (metRequirements.contains(reqID)) {
								message.append(ChatColor.RED
										+ req.getDescription() + ChatColor.BLUE
										+ " ("
										+ Lang.DONE_MARKER.getConfigValue(null)
										+ ")");
							} else {
								message.append(ChatColor.RED
										+ req.getDescription());
							}

							if (req.isOptional()) {
								message.append(ChatColor.AQUA
										+ " ("
										+ Lang.OPTIONAL_MARKER
												.getConfigValue(null) + ")");
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
			sender.sendMessage(ChatColor.AQUA + "/ar complete #"
					+ ChatColor.GRAY
					+ "- Complete a requirement at this moment");
			sender.sendMessage(ChatColor.AQUA
					+ "/ar sync"
					+ ChatColor.GRAY
					+ "- Sync MySQL database with server. (Use only one time per server)");
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
