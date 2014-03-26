package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.CheckCommandEvent;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCommand implements CommandExecutor {

	private final Autorank plugin;

	public CheckCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		// This is a local check. It will not show you the database numbers
		if (args.length > 1) {

			if (!plugin.getCommandsManager().hasPermission(
					"autorank.checkothers", sender)) {
				return true;
			}

			final Player player = plugin.getServer().getPlayer(args[1]);
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
			if (!plugin.getCommandsManager().hasPermission("autorank.check",
					sender)) {
				return true;
			}

			if (AutorankTools.isExcluded((Player) sender)) {
				sender.sendMessage(ChatColor.RED
						+ Lang.PLAYER_IS_EXCLUDED
								.getConfigValue(new String[] { sender.getName() }));
				return true;
			}
			final Player player = (Player) sender;
			check(sender, player);
		} else {
			AutorankTools.sendColoredMessage(sender,
					Lang.CANNOT_CHECK_CONSOLE.getConfigValue(null));
		}
		return true;
	}

	public void check(final CommandSender sender, final Player player) {
		// Call event to let other plugins know that a player wants to check itself.
		
		// Create the event here
		CheckCommandEvent event = new CheckCommandEvent(player);
		// Call the event
		Bukkit.getServer().getPluginManager().callEvent(event);

		// Check if event is cancelled.
		if (event.isCancelled())
			return;

		// Check if the latest known group is the current group. Otherwise, reset progress
		final String currentGroup = plugin.getPermPlugHandler()
				.getPermissionPlugin()
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

		final String[] groups = plugin.getPermPlugHandler()
				.getPermissionPlugin().getPlayerGroups(player);
		final StringBuilder stringBuilder = new StringBuilder();
		// has played for
		stringBuilder
				.append(player.getName()
						+ Lang.HAS_PLAYED_FOR.getConfigValue(null)
						+ AutorankTools.minutesToString(plugin
								.getLocalTime(player.getName())) + ", ");
		// is in
		stringBuilder.append(Lang.IS_IN.getConfigValue(null));
		if (groups.length == 0)
			stringBuilder.append(Lang.NO_GROUPS.getConfigValue(null)); // No groups.
		else if (groups.length == 1)
			stringBuilder.append(Lang.ONE_GROUP.getConfigValue(null)); // One group
		else
			stringBuilder.append(Lang.MULTIPLE_GROUPS.getConfigValue(null)); // Multiple groups

		boolean first = true;
		for (final String group : groups) {
			if (!first) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(group);
			first = false;
		}

		AutorankTools.sendColoredMessage(sender, stringBuilder.toString());

		String nextRankup = plugin.getPlayerChecker()
				.getNextRankupGroup(player);

		if (nextRankup == null) {
			AutorankTools.sendColoredMessage(sender,
					Lang.NO_NEXT_RANK.getConfigValue(null));
		} else {
			RankChange rank = plugin.getPlayerChecker().getNextRank(player);
			List<Requirement> reqs = plugin.getPlayerChecker()
					.getRequirementsForNextRank(player);

			boolean onlyOptional = true;
			boolean meetsAllRequirements = true;
			final List<Integer> metRequirements = new ArrayList<Integer>();

			for (final Requirement req : reqs) {
				if (!req.isOptional())
					onlyOptional = false;
			}

			for (final Requirement req : reqs) {
				final int reqID = req.getReqId();

				if (req.useAutoCompletion()) {
					// Do auto complete
					if (req.meetsRequirement(player)) {
						// Player meets the requirement -> give him results

						if (!plugin.getRequirementHandler()
								.hasCompletedRequirement(reqID,
										player.getName())) {
							plugin.getRequirementHandler().addPlayerProgress(
									player.getName(), reqID);

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
					if (plugin.getRequirementHandler().hasCompletedRequirement(
							reqID, player.getName())) {
						// Player has completed requirement
						metRequirements.add(reqID);
						continue;
					} else {
						meetsAllRequirements = false;
						continue;
					}
				}
			}
			final String reqMessage = rank.getRankTo() == null ? Lang.MEETS_ALL_REQUIREMENTS_WITHOUT_RANK_UP
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
					final Requirement req = reqs.get(i);
					final int reqID = req.getReqId();

					if (req != null) {
						final StringBuilder message = new StringBuilder("     "
								+ ChatColor.GOLD + (i + 1) + ". ");
						if (metRequirements.contains(reqID)) {
							message.append(ChatColor.RED + req.getDescription()
									+ ChatColor.BLUE + " ("
									+ Lang.DONE_MARKER.getConfigValue(null)
									+ ")");
						} else {
							message.append(ChatColor.RED + req.getDescription());
						}

						if (req.isOptional()) {
							message.append(ChatColor.AQUA + " ("
									+ Lang.OPTIONAL_MARKER.getConfigValue(null)
									+ ")");
						}
						AutorankTools.sendColoredMessage(sender,
								message.toString());

					}
				}
			}
			//}
		}
	}
}
