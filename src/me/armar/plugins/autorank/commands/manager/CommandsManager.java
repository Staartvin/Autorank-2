package me.armar.plugins.autorank.commands.manager;

import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.AddCommand;
import me.armar.plugins.autorank.commands.ArchiveCommand;
import me.armar.plugins.autorank.commands.CheckCommand;
import me.armar.plugins.autorank.commands.CompleteCommand;
import me.armar.plugins.autorank.commands.DebugCommand;
import me.armar.plugins.autorank.commands.ForceCheckCommand;
import me.armar.plugins.autorank.commands.GlobalCheckCommand;
import me.armar.plugins.autorank.commands.HelpCommand;
import me.armar.plugins.autorank.commands.ImportCommand;
import me.armar.plugins.autorank.commands.LeaderboardCommand;
import me.armar.plugins.autorank.commands.ReloadCommand;
import me.armar.plugins.autorank.commands.RemoveCommand;
import me.armar.plugins.autorank.commands.SetCommand;
import me.armar.plugins.autorank.commands.SyncCommand;
import me.armar.plugins.autorank.commands.SyncStatsCommand;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.google.common.collect.Lists;

public class CommandsManager implements TabExecutor {

	private final Autorank plugin;

	/**
	 * This class will manage all incoming command request.
	 * Commands are not performed here, they are only send to the correct place.
	 * A specific command class handles the task of performing the command.
	 * 
	 * @param plugin Autorank main class
	 */
	public CommandsManager(final Autorank plugin) {
		this.plugin = plugin;

		// Register command classes
		addCommand = new AddCommand(plugin);
		helpCommand = new HelpCommand(plugin);
		setCommand = new SetCommand(plugin);
		leaderboardCommand = new LeaderboardCommand(plugin);
		removeCommand = new RemoveCommand(plugin);
		debugCommand = new DebugCommand(plugin);
		syncCommand = new SyncCommand(plugin);
		syncStatsCommand = new SyncStatsCommand(plugin);
		reloadCommand = new ReloadCommand(plugin);
		importCommand = new ImportCommand(plugin);
		completeCommand = new CompleteCommand(plugin);
		checkCommand = new CheckCommand(plugin);
		archiveCommand = new ArchiveCommand(plugin);
		globalCheckCommand = new GlobalCheckCommand(plugin);
		forceCheckCommand = new ForceCheckCommand(plugin);
	}

	// All command classes
	private final AddCommand addCommand;
	private final SetCommand setCommand;
	private final HelpCommand helpCommand;
	private final LeaderboardCommand leaderboardCommand;
	private final RemoveCommand removeCommand;
	private final DebugCommand debugCommand;
	private final SyncCommand syncCommand;
	private final SyncStatsCommand syncStatsCommand;
	private final ReloadCommand reloadCommand;
	private final ImportCommand importCommand;
	private final CompleteCommand completeCommand;
	private final CheckCommand checkCommand;
	private final ArchiveCommand archiveCommand;
	private final GlobalCheckCommand globalCheckCommand;
	private final ForceCheckCommand forceCheckCommand;

	public boolean hasPermission(final String permission,
			final CommandSender sender) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ Lang.NO_PERMISSION
							.getConfigValue(new String[] { permission }));
			return false;
		}
		return true;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {
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

		final String action = args[0];
		if (action.equalsIgnoreCase("help")) {

			return helpCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("check")) {

			return checkCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("leaderboard")
				|| action.equalsIgnoreCase("leaderboards")) {

			return leaderboardCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("set")) {

			return setCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("add")) {

			return addCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("remove")
				|| action.equalsIgnoreCase("rem")) {

			return removeCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("debug")) {

			return debugCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("reload")) {

			return reloadCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("import")) {

			return importCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("archive")) {

			return archiveCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("gcheck")) {

			return globalCheckCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("complete")) {

			return completeCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("sync")) {

			return syncCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("syncstats")) {

			return syncStatsCommand.onCommand(sender, cmd, label, args);
		} else if (action.equalsIgnoreCase("forcecheck")) {

			return forceCheckCommand.onCommand(sender, cmd, label, args);
		}

		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		sender.sendMessage(ChatColor.YELLOW
				+ "Use '/ar help' for a list of commands.");
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {

		System.out.print("CommandLabel: " + commandLabel);

		if (args.length == 1) {
			// Show a list of commands if needed
			return Lists.newArrayList("help", "add", "set", "remove", "check",
					"leaderboard", "gcheck", "reload", "import", "archive",
					"debug", "complete", "sync", "syncstats", "forcecheck");
		}

		if (args.length > 1) {
			String subCommand = args[0];

			if (subCommand.equalsIgnoreCase("add")
					|| subCommand.equalsIgnoreCase("remove")
					|| subCommand.equalsIgnoreCase("set")) {
				// Give example numbers if needed.
				if (args.length == 3) {
					return Lists.newArrayList("5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60");
				}
			}
		}

		// TODO Auto-generated method stub
		return null;
	}
}
