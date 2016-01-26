package me.armar.plugins.autorank.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;

public class ResetCommand extends AutorankCommand {

	private final Autorank plugin;

	public ResetCommand(final Autorank instance) {
		this.setUsage("/ar reset <player> <action>");
		this.setDesc("Reset certain data of a player");
		this.setPermission("autorank.reset");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.reset",
				sender)) {
			return true;
		}

		if (args.length != 3) {
			
			sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar reset <player> <action>"));
			return true;
		}
		
		String target = args[1];
		String action = args[2];

		UUID uuid = plugin.getUUIDStorage().getStoredUUID(target);
		
		if (uuid == null) {
			sender.sendMessage(Lang.PLAYER_IS_INVALID.getConfigValue(target));
			return true;
		}
		
		String realName = plugin.getUUIDStorage().getRealName(uuid);
		
		if (!action.equalsIgnoreCase("progress")) {
			sender.sendMessage(ChatColor.RED + "Invalid action. You can only use: progress");
		}

		
		if (action.equalsIgnoreCase("progress")) {
			plugin.getPlayerDataHandler().setPlayerProgress(uuid, null);
			sender.sendMessage(ChatColor.GREEN + "Reset progress of " + ChatColor.YELLOW + realName);
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.commands.manager.AutorankCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender,
			final Command cmd, final String commandLabel, final String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
