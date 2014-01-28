package me.armar.plugins.autorank.statsmanager.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;

import org.bukkit.World;

public class StatsHandler implements StatsPlugin {

	private Autorank plugin;
	private StatsAPIHandler statsApi;

	public enum statTypes {
		BLOCKS_BROKEN,
		BLOCKS_PLACED,
		TOTAL_BLOCKS_BROKEN,
		TOTAL_BLOCKS_PLACED,
		VOTES,
		PLAYERS_KILLED,
		MOBS_KILLED,
		DAMAGE_TAKEN,
		TIME_PLAYED
	};

	public StatsHandler(Autorank instance, StatsAPIHandler statsAPI) {
		this.plugin = instance;

		statsApi = statsAPI;
	}

	@Override
	public boolean isEnabled() {
		return statsApi != null && statsApi.isEnabled() && statsApi.areBetaFunctionsEnabled();
	}

	@Override
	public int getNormalStat(String statType, String[] arguments) {
		// First argument is always the name, second arg is always the world

		if (arguments.length < 2) {
			throw new IllegalArgumentException(
					"Missing player or world for stat " + statType);
		}

		String correctName = getCorrectStatName(statType).toLowerCase();

		// Invalid name
		if (correctName == null) {
			return -2;
		}

		String playerName = arguments[0];
		String worldName = arguments[1];

		World world = null;

		if (worldName != null) {
			world = plugin.getServer().getWorld(worldName);
		}

		int value = -1;

		if (correctName.equals("votes")) {
			// Handle voting
			value = statsApi.getNormalStat(playerName, "Votes", world);
		} else if (correctName.equals("players_killed")) {
			// Handle players killed
			value = statsApi.getTotalMobsKilled(playerName, "player", world);
		} else if (correctName.equals("mobs_killed")) {
			// Handle mobs killed
			// arg[2] == mobType
			value = statsApi.getTotalMobsKilled(playerName, arguments[2], world);
		} else if (correctName.equals("damage_taken")) {
			// Handle damage taken
			value = statsApi.getNormalStat(playerName, "Damage taken", world);
		} else if (correctName.equals("blocks_placed")) {
			// Handle blocks placed
			value = statsApi.getBlocksStat(playerName, Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]), world, "Block place");
		} else if (correctName.equals("blocks_broken")) {
			// Handle blocks broken
			value = statsApi.getBlocksStat(playerName, Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]), world, "Block break");
		} else if (correctName.equals("total_blocks_placed")) {
			// Handle total blocks placed
			value = statsApi.getTotalBlocksPlaced(playerName, world);
		} else if (correctName.equals("total_blocks_broken")) {
			// Handle total blocks placed
			value = statsApi.getTotalBlocksBroken(playerName, world);
		} else if (correctName.equals("time_played")) {
			// Handle time played
			value = statsApi.getTotalPlayTime(playerName, world);
		}

		return value;
	}

	@Override
	public String getCorrectStatName(String statType) {

		statType = statType.replace(" ", "_");

		for (statTypes t : statTypes.values()) {
			if (t.name().equalsIgnoreCase(statType)) {
				return t.name();
			}
		}

		return null;
	}

}
