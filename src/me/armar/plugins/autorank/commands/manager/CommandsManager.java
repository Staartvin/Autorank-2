package me.armar.plugins.autorank.commands.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.AddCommand;
import me.armar.plugins.autorank.commands.ArchiveCommand;
import me.armar.plugins.autorank.commands.CheckCommand;
import me.armar.plugins.autorank.commands.CompleteCommand;
import me.armar.plugins.autorank.commands.ConvertUUIDCommand;
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
import org.bukkit.command.CommandExecutor;
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
		registeredCommands.put(Arrays.asList("add"), new AddCommand(plugin));
		registeredCommands.put(Arrays.asList("help"), new HelpCommand(plugin));
		registeredCommands.put(Arrays.asList("set"), new SetCommand(plugin));
		registeredCommands.put(Arrays.asList("leaderboard", "leaderboards"), new LeaderboardCommand(plugin));
		registeredCommands.put(Arrays.asList("remove", "rem"), new RemoveCommand(plugin));
		registeredCommands.put(Arrays.asList("debug"), new DebugCommand(plugin));
		registeredCommands.put(Arrays.asList("sync"), new SyncCommand(plugin));
		registeredCommands.put(Arrays.asList("syncstats"), new SyncStatsCommand(plugin));
		registeredCommands.put(Arrays.asList("reload"), new ReloadCommand(plugin));
		registeredCommands.put(Arrays.asList("import"), new ImportCommand(plugin));
		registeredCommands.put(Arrays.asList("complete"), new CompleteCommand(plugin));
		registeredCommands.put(Arrays.asList("check"), new CheckCommand(plugin));
		registeredCommands.put(Arrays.asList("archive", "arch"), new ArchiveCommand(plugin));
		registeredCommands.put(Arrays.asList("gcheck", "globalcheck"), new GlobalCheckCommand(plugin));
		registeredCommands.put(Arrays.asList("fcheck", "forcecheck"), new ForceCheckCommand(plugin));
		registeredCommands.put(Arrays.asList("convert"), new ConvertUUIDCommand(plugin));
	}
	private HashMap<List<String>, CommandExecutor> registeredCommands = new HashMap<List<String>, CommandExecutor>();

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
		
		
		// Go through every list and check if that action is in there.
		for (Entry<List<String>, CommandExecutor> entry : registeredCommands.entrySet()) {
			
			for (String actionString: entry.getKey()) {
				if (actionString.equalsIgnoreCase(action)) {
					return entry.getValue().onCommand(sender, cmd, label, args);
				}
			}
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

		if (args.length == 1) {
			// Show a list of commands if needed
			return Lists.newArrayList("help", "check", "leaderboard", "set",
					"add", "remove", "debug", "reload", "import", "archive",
					"gcheck", "complete", "sync", "syncstats", "forcecheck", "convert");
		}

		if (args.length > 1) {
			String subCommand = args[0];

			if (subCommand.equalsIgnoreCase("add")
					|| subCommand.equalsIgnoreCase("remove")
					|| subCommand.equalsIgnoreCase("set")) {
				// Give example numbers if needed.
				if (args.length == 3) {
					return Lists.newArrayList("5", "10", "15", "20", "25",
							"30", "35", "40", "45", "50", "55", "60");
				}
			}
		}

		// TODO Auto-generated method stub
		return null;
	}
}
