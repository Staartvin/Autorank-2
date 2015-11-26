package me.armar.plugins.autorank.commands;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.rankbuilder.ChangeGroup;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewCommand extends AutorankCommand {

	private final Autorank plugin;

	public ViewCommand(final Autorank instance) {
		this.setUsage("/ar view <path name>");
		this.setDesc("Gives a preview of a certain ranking path");
		this.setPermission("autorank.view");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		// This command will give a preview of a certain path of ranking.
		if (!plugin.getCommandsManager().hasPermission("autorank.view", sender)) {
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(Lang.INVALID_FORMAT
					.getConfigValue("/ar view <path name> or /ar view list"));
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.YOU_ARE_A_ROBOT
					.getConfigValue("you can't view ranking paths of players, silly.."));
			return true;
		}

		final Player player = (Player) sender;

		final String pathName = AutorankTools.getStringFromArgs(args, 1);

		final String groupName = plugin.getAPI().getPrimaryGroup(player);

		if (pathName.equals("list")) {
			sender.sendMessage(ChatColor.GREEN + "You can choose these paths: ");

			final String pathsString = AutorankTools
					.createStringFromList(plugin.getPlayerChecker()
							.getChangeGroupManager().getChangeGroups(groupName));
			sender.sendMessage(ChatColor.WHITE + pathsString);
			return true;
		}

		final ChangeGroup changeGroup = plugin
				.getPlayerChecker()
				.getChangeGroupManager()
				.matchChangeGroupFromDisplayName(groupName,
						pathName.toLowerCase());

		if (changeGroup == null) {
			sender.sendMessage(ChatColor.RED
					+ "There was no ranking path found.");
			return true;
		}

		final List<String> messages = plugin.getPlayerChecker()
				.getRequirementsInStringList(
						changeGroup.getRequirements(),
						plugin.getPlayerChecker().getMetRequirements(
								changeGroup.getRequirements(), player));

		for (final String message : messages) {
			AutorankTools.sendColoredMessage(sender, message);
		}

		sender.sendMessage(ChatColor.GREEN + "Preview of path '"
				+ ChatColor.GRAY + pathName + ChatColor.GREEN + "'");

		return true;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender,
			final Command cmd, final String commandLabel, final String[] args) {
		// TODO Auto-generated method stub
		final Player player = (Player) sender;

		final List<String> possibilities = new ArrayList<String>();

		final String groupName = plugin.getPermPlugHandler().getPrimaryGroup(
				player);

		final List<ChangeGroup> changeGroups = plugin.getPlayerChecker()
				.getChangeGroupManager().getChangeGroups(groupName);

		// List shows a list of changegroups to view
		possibilities.add("list");

		for (final ChangeGroup changeGroup : changeGroups) {
			possibilities.add(changeGroup.getDisplayName());
		}

		return possibilities;
	}

}
