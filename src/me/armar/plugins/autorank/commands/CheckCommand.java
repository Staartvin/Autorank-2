package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.api.events.CheckCommandEvent;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.rankbuilder.ChangeGroup;
import me.armar.plugins.autorank.rankbuilder.holders.RequirementsHolder;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

public class CheckCommand extends AutorankCommand {

	private final Autorank plugin;

	public CheckCommand(final Autorank instance) {
		this.setUsage("/ar check [player]");
		this.setDesc("Check [player]'s status");
		this.setPermission("autorank.check");

		plugin = instance;
	}

	public void check(final CommandSender sender, final Player player) {
		// Call event to let other plugins know that a player wants to check
		// itself.
		// Create the event here
		final CheckCommandEvent event = new CheckCommandEvent(player);
		// Call the event
		Bukkit.getServer().getPluginManager().callEvent(event);

		// final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());
		UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

		// Check if event is cancelled.
		if (event.isCancelled())
			return;

		// Only do for the first group - will cause problems
		final String groupName = plugin.getPermPlugHandler().getPrimaryGroup(player);

		String chosenPath = plugin.getPlayerDataHandler().getChosenPath(uuid);
		final ChangeGroup chosenChangeGroup = plugin.getPlayerChecker().getChangeGroupManager()
				.matchChangeGroup(groupName, chosenPath);

		final List<ChangeGroup> changeGroups = plugin.getPlayerChecker().getChangeGroupManager()
				.getChangeGroups(groupName);

		if (!plugin.getPlayerDataHandler().checkValidChosenPath(player)) {
			chosenPath = "unknown";
		}

		if (chosenPath.equals("unknown") && chosenChangeGroup == null
				&& !(changeGroups == null || changeGroups.size() == 0)) {

			// Not yet chosen a changegroup

			sender.sendMessage(ChatColor.BLACK + "-------------------------------------");
			sender.sendMessage(ChatColor.BLUE + "There are multiple ranking paths, please choose one with "
					+ ChatColor.RED + "'/ar choose'" + ChatColor.BLUE + ".");
			sender.sendMessage(
					ChatColor.GREEN + "You can always change later if you want, but you'll lose your progress.");
			sender.sendMessage(ChatColor.BLUE + "To check what each path looks like, use " + ChatColor.RED
					+ "'/ar view'" + ChatColor.DARK_BLUE + ".");
			sender.sendMessage(ChatColor.YELLOW + "You can see a list of paths with '" + ChatColor.RED + "/ar view list"
					+ ChatColor.YELLOW + "'.");
			sender.sendMessage(ChatColor.BLACK + "-------------------------------------");
			return;

		}

		// Start building layout

		String layout = plugin.getConfigHandler().getCheckCommandLayout();

		layout = layout.replace("&p", player.getName());
		layout = layout.replace("&time", AutorankTools
				.timeToString(plugin.getPlaytimes().getTimeOfPlayer(player.getName(), true), Time.SECONDS));

		final StringBuilder groupsString = new StringBuilder("");

		if (groupName == null) {
			groupsString.append(Lang.NO_GROUPS.getConfigValue());
		} else {
			groupsString.append(groupName);
		}

		layout = layout.replace("&groups", groupsString.toString());

		// final List<Requirement> reqs = plugin.getPlayerChecker()
		// .getAllRequirements(player);

		boolean showReqs = false;

		List<RequirementsHolder> holders = null;

		if (chosenChangeGroup != null) {
			holders = chosenChangeGroup.getRequirementsHolders();
		}

		if (holders == null || holders.size() == 0) {
			layout = layout.replace("&reqs", Lang.NO_FURTHER_RANKUP_FOUND.getConfigValue());
		} else {
			layout = layout.replace("&reqs", "");
			showReqs = true;
		}

		AutorankTools.sendColoredMessage(sender, layout);

		// Don't get requirements when the player has no new requirements
		if (!showReqs)
			return;

		boolean onlyOptional = true;
		boolean meetsAllRequirements = true;
		final List<Integer> metRequirements = new ArrayList<Integer>();

		// Check if we only have optional requirements
		for (RequirementsHolder holder : holders) {
			if (!holder.isOptional())
				onlyOptional = false;
		}

		for (RequirementsHolder holder : holders) {
			final int reqID = holder.getReqID();

			// Use auto completion
			if (holder.useAutoCompletion()) {
				// Do auto complete

				if (holder.meetsRequirement(player, uuid)) {
					// Player meets the requirement -> give him results

					// Doesn't need to check whether this requirement was
					// already done
					if (!plugin.getConfigHandler().usePartialCompletion())
						continue;

					if (!plugin.getPlayerDataHandler().hasCompletedRequirement(reqID, uuid)) {
						plugin.getPlayerDataHandler().addPlayerProgress(uuid, reqID);

						// Run results
						plugin.getPlayerDataHandler().runResults(holder, player);
					}
					metRequirements.add(reqID);
					continue;
				} else {

					// Only check if player has done this when partial
					// completion is used
					if (plugin.getConfigHandler().usePartialCompletion()) {
						// Player does not meet requirements, but has done this
						// already
						if (plugin.getPlayerDataHandler().hasCompletedRequirement(reqID, uuid)) {
							metRequirements.add(reqID);
							continue;
						}
					}

					// If requirement is optional, we do not check.
					if (holder.isOptional()) {
						continue;
					}

					// Player does not meet requirements -> do nothing
					meetsAllRequirements = false;
					continue;
				}
			} else {

				if (!plugin.getConfigHandler().usePartialCompletion()) {

					// Doesn't auto complete and doesn't meet requirement, then
					// continue searching
					if (!holder.meetsRequirement(player, uuid)) {

						// If requirement is optional, we do not check.
						if (holder.isOptional()) {
							continue;
						}

						meetsAllRequirements = false;
						continue;
					} else {
						// Player does meet requirement, continue searching
						continue;
					}

				}

				// Do not auto complete
				if (plugin.getPlayerDataHandler().hasCompletedRequirement(reqID, uuid)) {
					// Player has completed requirement already
					metRequirements.add(reqID);
					continue;
				} else {

					// If requirement is optional, we do not check.
					if (holder.isOptional()) {
						continue;
					}

					meetsAllRequirements = false;
					continue;
				}
			}
		}

		final String reqMessage = Lang.MEETS_ALL_REQUIREMENTS_WITHOUT_RANK_UP.getConfigValue();

		String reqMessage2 = "";

		if (plugin.getPlayerDataHandler().hasCompletedRank(uuid, chosenChangeGroup.getParentGroup())) {
			reqMessage2 = " " + Lang.ALREADY_COMPLETED_RANK.getConfigValue();
		} else {
			reqMessage2 = Lang.RANKED_UP_NOW.getConfigValue();
		}

		if (meetsAllRequirements || onlyOptional) {

			AutorankTools.sendColoredMessage(sender, reqMessage + reqMessage2);
			plugin.getPlayerChecker().checkPlayer(player);
		} else {
			// AutorankTools.sendColoredMessage(sender,
			// Lang.REQUIREMENTS_TO_RANK.getConfigValue(null));

			if (showReqs) {
				final List<String> messages = plugin.getPlayerChecker().getRequirementsInStringList(holders,
						metRequirements);

				for (final String message : messages) {
					AutorankTools.sendColoredMessage(sender, message);
				}
			}

		}
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		// This is a local check. It will not show you the database numbers
		if (args.length > 1) {

			if (!plugin.getCommandsManager().hasPermission("autorank.checkothers", sender)) {
				return true;
			}

			final Player player = plugin.getServer().getPlayer(args[1]);
			if (player == null) {

				final int time = plugin.getPlaytimes().getTimeOfPlayer(args[1], true);

				if (time <= 0) {
					sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(args[1]));
					return true;
				}

				UUID uuid = plugin.getUUIDStorage().getStoredUUID(args[1]);

				if (plugin.getUUIDStorage().hasRealName(uuid)) {
					args[1] = plugin.getUUIDStorage().getRealName(uuid);
				}

				AutorankTools.sendColoredMessage(sender,
						Lang.HAS_PLAYED_FOR.getConfigValue(args[1], AutorankTools.timeToString(time, Time.SECONDS)));
			} else {
				if (AutorankTools.isExcluded(player)) {
					sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(player.getName()));
					return true;
				}
				check(sender, player);
			}
		} else if (sender instanceof Player) {
			if (!plugin.getCommandsManager().hasPermission("autorank.check", sender)) {
				return true;
			}

			if (AutorankTools.isExcluded((Player) sender)) {
				sender.sendMessage(ChatColor.RED + Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender.getName()));
				return true;
			}
			final Player player = (Player) sender;
			check(sender, player);
		} else {
			AutorankTools.sendColoredMessage(sender, Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
		}
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
