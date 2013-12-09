package me.armar.plugins.autorank.commands.manager;

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
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandsManager implements CommandExecutor {

	private Autorank plugin;

	/**
	 * This class will manage all incoming command request.
	 * Commands are not performed here, they are only send to the correct place. 
	 * A specific command class handles the task of performing the command.
	 * @param plugin Autorank main class
	 */
	public CommandsManager(Autorank plugin) {
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
	private AddCommand addCommand; 
	private SetCommand setCommand;
	private HelpCommand helpCommand;
	private LeaderboardCommand leaderboardCommand;
	private RemoveCommand removeCommand;
	private DebugCommand debugCommand;
	private SyncCommand syncCommand;
	private SyncStatsCommand syncStatsCommand;
	private ReloadCommand reloadCommand;
	private ImportCommand importCommand;
	private CompleteCommand completeCommand;
	private CheckCommand checkCommand;
	private ArchiveCommand archiveCommand;
	private GlobalCheckCommand globalCheckCommand;
	private ForceCheckCommand forceCheckCommand;
	
	public boolean hasPermission(String permission, CommandSender sender) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED
					+ Lang.NO_PERMISSION
							.getConfigValue(new String[] { permission }));
			return false;
		}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
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

		String action = args[0];
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
}
