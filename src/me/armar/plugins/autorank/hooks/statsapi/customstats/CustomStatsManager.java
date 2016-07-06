package me.armar.plugins.autorank.hooks.statsapi.customstats;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.listeners.PlayerEatsFoodListener;
import me.armar.plugins.autorank.listeners.PlayerKillsMobListener;

/**
 * Manages all custom stats that Autorank creates with Stats.
 * <p>
 * Date created: 15:00:27 15 jul. 2015
 * 
 * @author Staartvin
 * 
 */
public class CustomStatsManager {

	private final Autorank plugin;
	private final StatsAPIHandler handler;

	public CustomStatsManager(final Autorank instance) {
		this.plugin = instance;
		this.handler = (StatsAPIHandler) instance.getDependencyManager().getDependency(dependency.STATS);
	}

	public void registerCustomStats() {
		handler.addStat(new MobKilledStat());
		plugin.debugMessage("Registered '" + MobKilledStat.statName + "' to Stats.");

		handler.addStat(new FoodEatenStat());
		plugin.debugMessage("Registered '" + FoodEatenStat.statName + "' to Stats.");

		// Register listeners
		plugin.getServer().getPluginManager().registerEvents(new PlayerEatsFoodListener(plugin), plugin);
		plugin.getServer().getPluginManager().registerEvents(new PlayerKillsMobListener(plugin), plugin);
	}
}
