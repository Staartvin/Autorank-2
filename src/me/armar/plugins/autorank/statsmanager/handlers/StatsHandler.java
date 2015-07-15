package me.armar.plugins.autorank.statsmanager.handlers;

import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;

public class StatsHandler implements StatsPlugin {

	public static enum statTypes {
		BLOCKS_BROKEN, BLOCKS_MOVED, BLOCKS_PLACED, DAMAGE_TAKEN, FISH_CAUGHT, ITEMS_CRAFTED, MOBS_KILLED, PLAYERS_KILLED, TIME_PLAYED, TIMES_SHEARED, TOTAL_BLOCKS_BROKEN, TOTAL_BLOCKS_PLACED, VOTES, FOOD_EATEN
	}

	private final Autorank plugin;

	private final StatsAPIHandler statsApi;;

	public StatsHandler(final Autorank instance, final StatsAPIHandler statsAPI) {
		this.plugin = instance;

		statsApi = statsAPI;
	}

	@Override
	public String getCorrectStatName(String statType) {

		statType = statType.replace(" ", "_");

		for (final statTypes t : statTypes.values()) {
			if (t.name().equalsIgnoreCase(statType)) {
				return t.name();
			}
		}

		return null;
	}

	@Override
	public int getNormalStat(final String statType, final UUID uuid,
			final Object... arguments) {
		// First argument is the world (or null)

		final String correctName = getCorrectStatName(statType).toLowerCase();

		// Invalid name
		if (correctName == null) {
			return -2;
		}

		//final String playerName = (String) arguments[0];
		String worldName = null;

		if (arguments.length > 1) {
			worldName = (String) arguments[0];
		}

		int value = -1;

		if (correctName.equals("votes")) {
			// Handle voting
			value = statsApi.getNormalStat(uuid, "Votes", worldName);
		} else if (correctName.equals("players_killed")) {
			// Handle players killed
			value = statsApi.getTotalMobsKilled(uuid, "player", worldName);
		} else if (correctName.equals("mobs_killed")) {
			// Handle mobs killed
			// arg[2] == mobType
			value = statsApi.getTotalMobsKilled(uuid, (String) arguments[1],
					worldName);
		} else if (correctName.equals("damage_taken")) {
			// Handle damage taken
			value = statsApi.getNormalStat(uuid, "Damage taken", worldName);
		} else if (correctName.equals("blocks_placed")) {
			// Handle blocks placed
			value = statsApi.getBlocksStat(uuid,
					Integer.parseInt((String) arguments[1]),
					Integer.parseInt((String) arguments[2]), worldName,
					"Blocks placed");
		} else if (correctName.equals("blocks_broken")) {
			// Handle blocks broken
			value = statsApi.getBlocksStat(uuid,
					Integer.parseInt((String) arguments[1]),
					Integer.parseInt((String) arguments[2]), worldName,
					"Blocks broken");
		} else if (correctName.equals("total_blocks_placed")) {
			// Handle total blocks placed
			value = statsApi.getTotalBlocksPlaced(uuid, worldName);
		} else if (correctName.equals("total_blocks_broken")) {
			// Handle total blocks placed
			value = statsApi.getTotalBlocksBroken(uuid, worldName);
		} else if (correctName.equals("time_played")) {
			// Handle time played
			value = statsApi.getTotalPlayTime(uuid, worldName);
		} else if (correctName.equals("blocks_moved")) {
			// Handle time played
			value = statsApi.getTotalBlocksMoved(uuid, (Integer) arguments[1],
					worldName);
		} else if (correctName.equals("fish_caught")) {
			// Handle time played
			value = statsApi.getNormalStat(uuid, "Fish caught", worldName);
		} else if (correctName.equals("items_crafted")) {
			// Handle time played
			value = statsApi.getNormalStat(uuid, "Items crafted", worldName);
		} else if (correctName.equals("times_sheared")) {
			// Handle time played
			value = statsApi.getNormalStat(uuid, "Shears", worldName);
		} else if (correctName.equals("food_eaten")) {
			// Handle food eaten
			value = statsApi.getFoodEaten(uuid, worldName,
					(String) arguments[1]);
		}

		return value;
	}

	@Override
	public boolean isEnabled() {
		if (statsApi == null) {
			plugin.getLogger().info(
					"Stats (by Lolmewn) api library was not found!");
			return false;
		}

		if (!statsApi.isAvailable()) {
			plugin.getLogger().info("Stats (by Lolmewn) is not enabled!");
			return false;
		}

		/*if (!statsApi.compatibleStatsVersion(plugin.getServer()
				.getPluginManager().getPlugin("Stats").getDescription()
				.getVersion())) {
			plugin.getLogger().info("This version of Stats (by Lolmewn) is not supported by Autorank!"); 
			return false;
		}*/

		return true;
	}

}
