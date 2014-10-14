package me.armar.plugins.autorank.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrackCommand extends AutorankCommand {

	private final Autorank plugin;

	public TrackCommand(final Autorank instance) {
		this.setUsage("/ar track #");
		this.setDesc("Track the progress of a requirement.");
		this.setPermission("autorank.track");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		// Implemented /ar track #
		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED + "Incorrect command usage!");
			sender.sendMessage(ChatColor.YELLOW + "Usage: /ar track #");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "You are a robot! You don't make progress, silly..");
			return true;
		}

		/*
		if (!plugin.getConfigHandler().usePartialCompletion()) {
			/
			sender.sendMessage(ChatColor.RED
					+ "You cannot use this command as this server has not enabled partial completion!");
			return true;
		}*/

		if (!plugin.getCommandsManager()
				.hasPermission("autorank.track", sender))
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

		final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

		final Map<RankChange, List<Requirement>> failed = plugin
				.getPlayerChecker().getAllRequirements(player);
		final Set<RankChange> keySet = failed.keySet();

		if (keySet.size() == 0) {
			player.sendMessage(ChatColor.RED
					+ "You don't have any requirements!");
			return true;
		}

		List<Requirement> requirements;

		player.sendMessage(ChatColor.GRAY + " ------------ ");

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
						(completionID - 1), uuid)) {
					player.sendMessage(ChatColor.RED
							+ Lang.ALREADY_COMPLETED_REQUIREMENT
									.getConfigValue());
					return true;
				}

				player.sendMessage(ChatColor.RED
						+ Lang.REQUIREMENT_PROGRESS.getConfigValue(completionID
								+ ""));
				player.sendMessage(ChatColor.AQUA + req.getDescription());
				player.sendMessage(ChatColor.GREEN + "Current: "
						+ ChatColor.GOLD + req.getProgress(player));
			}
		}

		return true;
	}

}
