package me.armar.plugins.autorank.listeners;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.hooks.statsapi.customstats.MobKilledStat;
import nl.lolmewn.stats.api.stat.Stat;
import nl.lolmewn.stats.api.user.StatsHolder;
import nl.lolmewn.stats.stat.DefaultStatEntry;
import nl.lolmewn.stats.stat.MetadataPair;

/**
 * This listener will listen to players killing mobs (for custom stat)
 * 
 * @author Staartvin
 * 
 */
public class PlayerKillsMobListener implements Listener {

	private final Autorank plugin;

	public PlayerKillsMobListener(final Autorank instance) {
		plugin = instance;
	}

	@EventHandler
	public void OnKill(final EntityDeathEvent event) {

		// Stats is not available
		if (!plugin.getDependencyManager().getDependency(dependency.STATS)
				.isAvailable())
			return;

		final Entity e = event.getEntity();
		if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			final EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e
					.getLastDamageCause();
			if (nEvent.getDamager() instanceof Player) {

				String extraType = null;

				if (e instanceof Skeleton) {
					final Skeleton ske = (Skeleton) e;

					if (ske.getSkeletonType() == SkeletonType.WITHER) {
						extraType = "WITHER";
					}
				} else if (e instanceof Creeper) {
					final Creeper cre = (Creeper) e;

					if (cre.isPowered()) {
						extraType = "POWERED";
					}
				} else if (e instanceof Chicken) {
					final Chicken mob = (Chicken) e;

					if (mob.getPassenger() != null) {
						extraType = "JOCKEY";
					}
				} else if (e instanceof Rabbit) {
					final Rabbit mob = (Rabbit) e;

					if (mob.getRabbitType() == Type.THE_KILLER_BUNNY) {
						extraType = "KILLER";
					}
				} else if (e instanceof Spider) {
					final Spider mob = (Spider) e;

					if (mob.getPassenger() != null) {
						extraType = "JOCKEY";
					}
				} else if (e instanceof Guardian) {
					final Guardian mob = (Guardian) e;

					if (mob.isElder()) {
						extraType = "ELDER";
					}
				}

				if (extraType == null) {
					return;
				}

				final StatsAPIHandler handler = (StatsAPIHandler) plugin
						.getDependencyManager().getDependency(dependency.STATS);

				final Stat mobkilled = handler.getAPI().getStatManager()
						.getStat(MobKilledStat.statName);

				final StatsHolder holder = handler.getAPI().getPlayer(
						nEvent.getDamager().getUniqueId());

				holder.addEntry(mobkilled, new DefaultStatEntry(1,
						new MetadataPair("entityType", e.getType().toString()),
						new MetadataPair("extraType", extraType),
						new MetadataPair("world", e.getLocation().getWorld()
								.getName().toString())));

			}
		}
	}
}
