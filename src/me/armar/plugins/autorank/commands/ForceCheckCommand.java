package me.armar.plugins.autorank.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class ForceCheckCommand extends AutorankCommand {

	private final Autorank plugin;

	public ForceCheckCommand(final Autorank instance) {
		this.setUsage("/ar forcecheck <player>");
		this.setDesc("Do a manual silent check.");
		this.setPermission("autorank.forcecheck");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		if (!plugin.getCommandsManager().hasPermission("autorank.forcecheck", sender))
			return true;

		if (args.length != 2) {
			sender.sendMessage(Lang.INVALID_FORMAT.getConfigValue("/ar forcecheck <player>"));
			return true;
		}

		final String target = args[1];
		final Player targetPlayer = plugin.getServer().getPlayer(target);

		if (targetPlayer == null) {
			sender.sendMessage(Lang.PLAYER_NOT_ONLINE.getConfigValue(target));
			return true;
		}

		if (AutorankTools.isExcluded(targetPlayer)) {
			sender.sendMessage(Lang.PLAYER_IS_EXCLUDED.getConfigValue(targetPlayer.getName()));
			return true;
		}

		// Check the player
		plugin.getPlayerChecker().checkPlayer(targetPlayer);

		// Let checker know that we checked.
		sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " checked!");

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
