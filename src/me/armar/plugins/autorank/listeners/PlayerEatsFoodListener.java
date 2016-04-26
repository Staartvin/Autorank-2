package me.armar.plugins.autorank.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.hooks.statsapi.customstats.FoodEatenStat;
import me.armar.plugins.autorank.util.AutorankTools;
import nl.lolmewn.stats.api.stat.Stat;
import nl.lolmewn.stats.api.user.StatsHolder;
import nl.lolmewn.stats.stat.DefaultStatEntry;
import nl.lolmewn.stats.stat.MetadataPair;

/**
 * This listener will listen to players eating food (for custom stat)
 * 
 * @author Staartvin
 * 
 */
public class PlayerEatsFoodListener implements Listener {

	private final Autorank plugin;

	public PlayerEatsFoodListener(final Autorank instance) {
		plugin = instance;
	}

	@EventHandler
	public void OnEat(final PlayerItemConsumeEvent event) {

		if (event.isCancelled())
			return;

		// Stats is not available
		if (!plugin.getDependencyManager().getDependency(dependency.STATS).isAvailable())
			return;

		final Player p = event.getPlayer();

		final String foodName = AutorankTools.getFoodName(event.getItem());

		if (foodName == null)
			return;

		final StatsAPIHandler handler = (StatsAPIHandler) plugin.getDependencyManager().getDependency(dependency.STATS);

		final Stat stat = handler.getAPI().getStatManager().getStat(FoodEatenStat.statName);

		final StatsHolder holder = handler.getAPI().getPlayer(p.getUniqueId());

		holder.addEntry(stat, new DefaultStatEntry(1, new MetadataPair("foodType", foodName),
				new MetadataPair("world", p.getWorld().getName())));

	}

}
