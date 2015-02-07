package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.CheckCommandEvent;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCommand extends AutorankCommand {

	private final Autorank plugin;

	public CheckCommand(final Autorank instance) {
		this.setUsage("/ar check [player]");
		this.setDesc("Check [player]'s status");
		this.setPermission("autorank.check");

		plugin = instance;
	}

	public void check(final CommandSender sender, final Player player) {
		// Call event to let other plugins know that a player wants to check itself.
		// Create the event here
		final CheckCommandEvent event = new CheckCommandEvent(player);
		// Call the event
		Bukkit.getServer().getPluginManager().callEvent(event);

		final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

		// Check if event is cancelled.
		if (event.isCancelled())
			return;

		// Check if the latest known group is the current group. Otherwise, reset progress
		final String currentGroup = plugin.getPermPlugHandler()
				.getPermissionPlugin()
				.getWorldGroups(player, player.getWorld().getName())[0];
		String latestKnownGroup = plugin.getRequirementHandler()
				.getLastKnownGroup(uuid);

		if (latestKnownGroup == null) {
			plugin.getRequirementHandler()
					.setLastKnownGroup(uuid, currentGroup);

			latestKnownGroup = currentGroup;
		}
		if (!latestKnownGroup.equalsIgnoreCase(currentGroup)) {
			// Reset progress and update latest known group
			plugin.getRequirementHandler().setPlayerProgress(uuid,
					new ArrayList<Integer>());
			plugin.getRequirementHandler()
					.setLastKnownGroup(uuid, currentGroup);
		}

		final String[] groups = plugin.getPermPlugHandler()
				.getPermissionPlugin().getPlayerGroups(player);

		String layout = plugin.getConfigHandler().getCheckCommandLayout();

		// Start building layout

		layout = layout.replace("&p", player.getName());
		layout = layout.replace("&time",
				AutorankTools.timeToString(plugin.getPlaytimes()
						.getTimeOfPlayer(player.getName()), Time.SECONDS));

		final StringBuilder groupsString = new StringBuilder("");

		if (groups.length == 0) {
			groupsString.append(Lang.NO_GROUPS.getConfigValue());
		} else {
			boolean first = true;
			for (final String group : groups) {
				if (!first) {
					groupsString.append(", ");
				}
				groupsString.append(group);
				first = false;
			}
		}

		layout = layout.replace("&groups", groupsString.toString());

		final RankChange nextRankChange = plugin.getPlayerChecker()
				.getNextRank(player);

		boolean showReqs = false;

		if (nextRankChange == null) {
			layout = layout.replace("&reqs", "none (last rankup group)");
		} else {
			layout = layout.replace("&reqs", "");
			showReqs = true;
		}

		AutorankTools.sendColoredMessage(sender, layout);

		// has played for
		/*stringBuilder.append(player.getName()
				+ Lang.HAS_PLAYED_FOR.getConfigValue(null)
				+ AutorankTools.minutesToString(plugin.getPlaytimes()
						.getLocalTime(uuid)) + ", ");*/

		// is in
		//stringBuilder.append(Lang.IS_IN.getConfigValue(null));
		/*if (groups.length == 0)
			stringBuilder.append(Lang.NO_GROUPS.getConfigValue(null)); // No groups.
		else if (groups.length == 1)
			stringBuilder.append(Lang.ONE_GROUP.getConfigValue(null)); // One group
		else
			stringBuilder.append(Lang.MULTIPLE_GROUPS.getConfigValue(null)); // Multiple groups
		*/

		/*String nextRankup = plugin.getPlayerChecker()
				.getNextRankupGroup(player);

		if (nextRankup == null || plugin.getRequirementHandler().getCompletedRanks(uuid).contains(nextRankChange.getRankFrom())) {
			AutorankTools.sendColoredMessage(sender,
					Lang.NO_NEXT_RANK.getConfigValue());
		}*/

		// Don't get requirements when the player has no new requirements
		if (nextRankChange == null)
			return;

		final List<Requirement> reqs = plugin.getPlayerChecker()
				.getRequirementsForNextRank(player);

		boolean onlyOptional = true;
		boolean meetsAllRequirements = true;
		final List<Integer> metRequirements = new ArrayList<Integer>();

		// Check if we only have optional requirements
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

					// Doesn't need to check whether this requirement was already done
					if (!plugin.getConfigHandler().usePartialCompletion())
						continue;

					if (!plugin.getRequirementHandler()
							.hasCompletedRequirement(reqID, uuid)) {
						plugin.getRequirementHandler().addPlayerProgress(uuid,
								reqID);

						// Run results
						plugin.getRequirementHandler().runResults(req, player);
					}
					metRequirements.add(reqID);
					continue;
				} else {

					// Only check if player has done this when partial completion is used
					if (plugin.getConfigHandler().usePartialCompletion()) {
						// Player does not meet requirements, but has done this already
						if (plugin.getRequirementHandler()
								.hasCompletedRequirement(reqID, uuid)) {
							metRequirements.add(reqID);
							continue;
						}
					}

					// Player does not meet requirements -> do nothing
					meetsAllRequirements = false;
					continue;
				}
			} else {

				// Doesn't auto complete and doesn't meet requirement, then continue searching
				if (!plugin.getConfigHandler().usePartialCompletion()) {

					if (!req.meetsRequirement(player)) {
						meetsAllRequirements = false;
						continue;
					} else {
						// Player does meet requirement, continue searching
						continue;
					}

				}

				// Do not auto complete
				if (plugin.getRequirementHandler().hasCompletedRequirement(
						reqID, uuid)) {
					// Player has completed requirement already
					metRequirements.add(reqID);
					continue;
				} else {
					meetsAllRequirements = false;
					continue;
				}
			}
		}
		final String reqMessage = nextRankChange.getRankTo() == null ? Lang.MEETS_ALL_REQUIREMENTS_WITHOUT_RANK_UP
				.getConfigValue() : Lang.MEETS_ALL_REQUIREMENTS
				.getConfigValue(nextRankChange.getRankTo());

		String reqMessage2 = "";

		if (plugin.getRequirementHandler().hasCompletedRank(uuid,
				nextRankChange.getRankFrom())) {
			reqMessage2 = " but has already completed this rankup before.";
		} else {
			reqMessage2 = Lang.RANKED_UP_NOW.getConfigValue();
		}

		if (meetsAllRequirements || onlyOptional) {

			AutorankTools.sendColoredMessage(sender, reqMessage + reqMessage2);
			plugin.getPlayerChecker().checkPlayer(player);
		} else {
			//AutorankTools.sendColoredMessage(sender,
			//	Lang.REQUIREMENTS_TO_RANK.getConfigValue(null));

			if (showReqs) {
				final List<String> messages = getRequirementsInStringList(reqs,
						metRequirements);

				for (final String message : messages) {
					AutorankTools.sendColoredMessage(sender, message);
				}
			}

		}
	}

	private List<String> getRequirementsInStringList(
			final List<Requirement> reqs, final List<Integer> metRequirements) {
		// Converts requirements into a list of readable requirements

		final List<String> messages = new ArrayList<String>();

		for (int i = 0; i < reqs.size(); i++) {
			final Requirement req = reqs.get(i);
			final int reqID = req.getReqId();

			if (req != null) {
				final StringBuilder message = new StringBuilder("     "
						+ ChatColor.GOLD + (i + 1) + ". ");
				if (metRequirements.contains(reqID)) {
					message.append(ChatColor.RED + req.getDescription()
							+ ChatColor.BLUE + " ("
							+ Lang.DONE_MARKER.getConfigValue() + ")");
				} else {
					message.append(ChatColor.RED + req.getDescription());
				}

				if (req.isOptional()) {
					message.append(ChatColor.AQUA + " ("
							+ Lang.OPTIONAL_MARKER.getConfigValue() + ")");
				}

				messages.add(message.toString());

			}
		}

		return messages;

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

				final int time = plugin.getPlaytimes().getTimeOfPlayer(args[1]);

				if (time <= 0) {
					sender.sendMessage(Lang.PLAYER_IS_INVALID
							.getConfigValue(args[1]));
					return true;
				}

				AutorankTools.sendColoredMessage(
						sender,
						args[1]
								+ " has played for "
								+ AutorankTools
										.timeToString(time, Time.SECONDS));
			} else {
				if (AutorankTools.isExcluded(player)) {
					sender.sendMessage(ChatColor.RED
							+ Lang.PLAYER_IS_EXCLUDED.getConfigValue(args[1]));
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
						+ Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender
								.getName()));
				return true;
			}
			final Player player = (Player) sender;
			check(sender, player);
		} else {
			AutorankTools.sendColoredMessage(sender,
					Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
		}
		return true;
	}
}
