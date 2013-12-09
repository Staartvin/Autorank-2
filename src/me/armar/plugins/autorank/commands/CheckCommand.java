package me.armar.plugins.autorank.commands;

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

public class CheckCommand implements CommandExecutor {

	private Autorank plugin;

	public CheckCommand(Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		// This is a local check. It will not show you the database numbers
		if (args.length > 1) {

			if (!plugin.getCommandsManager().hasPermission("autorank.checkothers", sender)) {
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
			if (!plugin.getCommandsManager().hasPermission("autorank.check", sender)) {
				return true;
			}

			if (AutorankTools.isExcluded((Player) sender)) {
				sender.sendMessage(ChatColor.RED
						+ Lang.PLAYER_IS_EXCLUDED
								.getConfigValue(new String[] { sender.getName() }));
				return true;
			}
			Player player = (Player) sender;
			check(sender, player);
		} else {
			AutorankTools.sendColoredMessage(sender,
					Lang.CANNOT_CHECK_CONSOLE.getConfigValue(null));
		}
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
}
