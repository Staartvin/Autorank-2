package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompleteCommand extends AutorankCommand {

	private final Autorank plugin;

	public CompleteCommand(final Autorank instance) {
		this.setUsage("/ar complete #");
		this.setDesc("Complete a requirement at this moment");
		this.setPermission("autorank.complete");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		// Implemented /ar complete #
		if (args.length != 2) {
			sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar complete #"));
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.YOU_ARE_A_ROBOT.getConfigValue("you can't rank up, silly.."));
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

		final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

		List<Requirement> requirements = plugin.getPlayerChecker()
				.getAllRequirements(player);

		if (requirements.size() == 0) {
			player.sendMessage(ChatColor.RED + "You don't have a next rank up!");
			return true;
		}

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

			if (plugin.getPlayerDataHandler().hasCompletedRequirement(
					(completionID - 1), uuid)) {
				player.sendMessage(ChatColor.RED
						+ Lang.ALREADY_COMPLETED_REQUIREMENT.getConfigValue());
				return true;
			}

			if (req.meetsRequirement(player)) {
				// Player meets requirement
				player.sendMessage(ChatColor.GREEN
						+ Lang.SUCCESSFULLY_COMPLETED_REQUIREMENT
								.getConfigValue(completionID + ""));
				player.sendMessage(ChatColor.AQUA + req.getDescription());

				// Run results
				plugin.getPlayerDataHandler().runResults(req, player);

				// Log that a player has passed this requirement
				plugin.getPlayerDataHandler().addPlayerProgress(uuid,
						(completionID - 1));

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

		/*for (Entry<String, List<Requirement>> entry: failed.entrySet()) {
			
		}*/

		return true;
	}

}
