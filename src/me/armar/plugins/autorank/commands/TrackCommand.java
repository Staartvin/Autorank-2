package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.rankbuilder.holders.RequirementsHolder;

public class TrackCommand extends AutorankCommand {

	private final Autorank plugin;

	public TrackCommand(final Autorank instance) {
		this.setUsage("/ar track #");
		this.setDesc("Track the progress of a requirement.");
		this.setPermission("autorank.track");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		// Implemented /ar track #
		if (args.length != 2) {
			sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar track #"));
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.YOU_ARE_A_ROBOT.getConfigValue("you don't make progress, silly.."));
			return true;
		}

		/*
		if (!plugin.getConfigHandler().usePartialCompletion()) {
			/
			sender.sendMessage(ChatColor.RED
					+ "You cannot use this command as this server has not enabled partial completion!");
			return true;
		}*/

		if (!plugin.getCommandsManager().hasPermission("autorank.track", sender))
			return true;

		final Player player = (Player) sender;

		int completionID = 0;

		try {
			completionID = Integer.parseInt(args[1]);

			if (completionID < 1) {
				completionID = 1;
			}
		} catch (final Exception e) {
			player.sendMessage(ChatColor.RED + Lang.INVALID_NUMBER.getConfigValue(new String[] { args[1] }));
			return true;
		}

		final UUID uuid = plugin.getUUIDStorage().getStoredUUID(player.getName());

		final List<RequirementsHolder> holders = plugin.getPlayerChecker().getAllRequirementsHolders(player);

		if (holders.size() == 0) {
			player.sendMessage(ChatColor.RED + "You don't have any requirements!");
			return true;
		}

		player.sendMessage(ChatColor.GRAY + " ------------ ");

		// Rank player as he has fulfilled all requirements
		if (holders.size() == 0) {
			player.sendMessage(ChatColor.GREEN + "You don't have any requirements left.");
			return true;
		} else {
			// Get the specified requirement
			if (completionID > holders.size()) {
				completionID = holders.size();
			}

			if (completionID < 1) {
				// Fail-safe
				completionID = 1;
			}

			// Human logic = first number is 1 not 0.
			final RequirementsHolder holder = holders.get((completionID - 1));

			if (plugin.getPlayerDataHandler().hasCompletedRequirement((completionID - 1), uuid)) {
				player.sendMessage(ChatColor.RED + Lang.ALREADY_COMPLETED_REQUIREMENT.getConfigValue());
				return true;
			}

			player.sendMessage(ChatColor.RED + Lang.REQUIREMENT_PROGRESS.getConfigValue(completionID + ""));
			player.sendMessage(ChatColor.AQUA + holder.getDescription());
			player.sendMessage(ChatColor.GREEN + "Current: " + ChatColor.GOLD + holder.getProgress(player));
		}

		/*for (Requirement requirement: requirements) {
		
			
		}*/

		return true;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
			final String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
