package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompleteCommand implements CommandExecutor {

	private final Autorank plugin;

	public CompleteCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

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

		if (!plugin.getCommandsManager().hasPermission("autorank.complete",
				sender))
			return true;

		final Player player = (Player) sender;

		int completionID = 0;

		try {
			completionID = Integer.parseInt(args[1]);

			if (completionID < 1) {
				completionID = 1;
			}
		} catch (final Exception e) {
			player.sendMessage(ChatColor.RED
					+ Lang.INVALID_NUMBER
							.getConfigValue(new String[] { args[1] }));
			return true;
		}

		// Check if the latest known group is the current group. Otherwise, reset progress
		final String currentGroup = plugin.getPermPlugHandler()
				.getPermissionPlugin().getPlayerGroups(player)[0];
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

		final Map<RankChange, List<Requirement>> failed = plugin
				.getPlayerChecker().getAllRequirements(player);
		final Set<RankChange> keySet = failed.keySet();

		if (keySet.size() == 0) {
			player.sendMessage(ChatColor.RED + "You don't have a next rank up!");
			return true;
		}

		List<Requirement> requirements;
		for (final Iterator<RankChange> it = keySet.iterator(); it.hasNext();) {
			final RankChange rank = it.next();
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
				final Requirement req = requirements.get((completionID - 1));

				if (plugin.getRequirementHandler().hasCompletedRequirement(
						(completionID - 1), player.getName())) {
					player.sendMessage(ChatColor.RED
							+ Lang.ALREADY_COMPLETED_REQUIREMENT
									.getConfigValue());
					return true;
				}

				if (req.meetsRequirement(player)) {
					// Player meets requirement
					player.sendMessage(ChatColor.GREEN
							+ Lang.SUCCESSFULLY_COMPLETED_REQUIREMENT
									.getConfigValue(completionID + ""));
					player.sendMessage(ChatColor.AQUA + req.getDescription());

					// Run results
					plugin.getRequirementHandler().runResults(req, player);

					// Log that a player has passed this requirement
					plugin.getRequirementHandler().addPlayerProgress(
							player.getName(), (completionID - 1));

				} else {
					// player does not meet requirements
					player.sendMessage(ChatColor.RED
							+ Lang.DO_NOT_MEET_REQUIREMENTS_FOR
									.getConfigValue(completionID + ""));
					player.sendMessage(ChatColor.AQUA + req.getDescription());
					player.sendMessage(ChatColor.GREEN + "Current: "
							+ ChatColor.GOLD + req.getProgress(player));
				}
			}
		}

		return true;
	}

}
