package me.armar.plugins.autorank.commands;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.hooks.DependencyHandler;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HooksCommand extends AutorankCommand {

	private final Autorank plugin;

	public HooksCommand(final Autorank instance) {
		this.setUsage("/ar hooks");
		this.setDesc("Shows a list of hookable plugins for Autorank");
		this.setPermission("autorank.hooks");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		sender.sendMessage(ChatColor.GOLD + "Autorank Hooks:");

		for (dependency dep : dependency.values()) {
			// There is no dependency handler for Autorank
			if (dep == dependency.AUTORANK)
				continue;

			DependencyHandler handler = plugin.getDependencyManager()
					.getDependency(dep);

			StringBuilder message = new StringBuilder(ChatColor.GRAY
					+ dep.toString() + ": " + ChatColor.RESET);

			if (handler.isAvailable()) {
				message.append(ChatColor.GREEN + "AVAILABLE");
			} else {
				message.append(ChatColor.RED + "NOT AVAILABLE");
			}

			sender.sendMessage(message.toString());
		}

		return true;
	}

}
