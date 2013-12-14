package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private final Autorank plugin;

	public HelpCommand(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (args.length == 1) {
			showHelpPages(sender, 1);
		} else {
			int page = 1;
			try {
				page = Integer.parseInt(args[1]);
			} catch (final Exception e) {
				sender.sendMessage(ChatColor.RED
						+ Lang.INVALID_NUMBER
								.getConfigValue(new String[] { args[1] }));
				return true;
			}
			showHelpPages(sender, page);
		}
		return true;
	}

	private void showHelpPages(final CommandSender sender, final int page) {
		final int maxPages = 3;
		if (page == 2) {
			sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");
			sender.sendMessage(ChatColor.AQUA + "/ar help <page> "
					+ ChatColor.GRAY + "- Show a list of commands");
			sender.sendMessage(ChatColor.AQUA + "/ar reload " + ChatColor.GRAY
					+ "- Reload the plugin");
			sender.sendMessage(ChatColor.AQUA + "/ar import " + ChatColor.GRAY
					+ "- Import old data");
			sender.sendMessage(ChatColor.AQUA + "/ar archive <minimum> "
					+ ChatColor.GRAY + "- Archive data with a minimum");
			sender.sendMessage(ChatColor.AQUA + "/ar debug " + ChatColor.GRAY
					+ "- Shows debug information");
			sender.sendMessage(ChatColor.AQUA + "/ar complete # "
					+ ChatColor.GRAY
					+ "- Complete a requirement at this moment");
			sender.sendMessage(ChatColor.AQUA
					+ "/ar sync "
					+ ChatColor.GRAY
					+ "- Sync MySQL database with server. (Use only one time per server)");
			sender.sendMessage(ChatColor.BLUE + "Page 2 of " + maxPages);
		} else if (page == 3) {
			sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");
			sender.sendMessage(ChatColor.AQUA + "/ar syncstats"
					+ ChatColor.GRAY + "- Sync Autorank's time to Stats' time");
			sender.sendMessage(ChatColor.AQUA + "/ar forcecheck <player>"
					+ ChatColor.GRAY + "- Do a manual silent check.");
			sender.sendMessage(ChatColor.BLUE + "Page 3 of " + maxPages);
		} else {
			sender.sendMessage(ChatColor.GREEN + "-- Autorank Commands --");
			sender.sendMessage(ChatColor.AQUA + "/ar check " + ChatColor.GRAY
					+ "- Check your own status");
			sender.sendMessage(ChatColor.AQUA + "/ar check [player] "
					+ ChatColor.GRAY + "- Check [player]'s status");
			sender.sendMessage(ChatColor.AQUA + "/ar leaderboard "
					+ ChatColor.GRAY + "- Show the leaderboard");
			sender.sendMessage(ChatColor.AQUA + "/ar set [player] [value] "
					+ ChatColor.GRAY + "- Set [player]'s time to [value]");
			sender.sendMessage(ChatColor.AQUA + "/ar add [player] [value] "
					+ ChatColor.GRAY + "- Add [value] to [player]'s time");
			sender.sendMessage(ChatColor.AQUA + "/ar remove [player] [value] "
					+ ChatColor.GRAY + "- Remove [value] from [player]'s time");
			sender.sendMessage(ChatColor.AQUA + "/ar gcheck [player] "
					+ ChatColor.GRAY + "- Check [player]'s global playtime");
			sender.sendMessage(ChatColor.BLUE + "Page 1 of " + maxPages);
		}
	}
}
