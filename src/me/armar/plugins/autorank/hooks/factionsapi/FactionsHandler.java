package me.armar.plugins.autorank.hooks.factionsapi;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.UPlayer;

public class FactionsHandler {

	private final Autorank plugin;
	private Factions factions;

	public FactionsHandler(final Autorank instance) {
		plugin = instance;
	}

	public boolean isEnabled() {
		return factions != null;
	}

	public boolean setupFactions() {
		final Plugin factionPlugin = plugin.getServer().getPluginManager()
				.getPlugin("Factions");

		if (factionPlugin == null) {
			plugin.getLogger().info("Factions has not been found!");
			return false;
		}

		if (!factionPlugin.getDescription().getAuthors().contains("Brettflan")) {
			plugin.getLogger().info("Factions plugin from another author has been found!");
			return false;
		}
			
		factions = (Factions) factionPlugin;

		plugin.getLogger().info("Factions has been found and can be used!");
		
		return factions != null;
	}

	public double getFactionPower(final Player player) {
		if (!isEnabled()) return 0.0d;
		
		final UPlayer uPlayer = UPlayer.get(player);

		if (!uPlayer.hasFaction()) {
			return 0.0d;
		}

		return uPlayer.getFaction().getPower();
	}
}
