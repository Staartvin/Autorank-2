package me.armar.plugins.autorank.statsmanager.handlers;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;

public class StatsHandler implements StatsPlugin {

	private Autorank plugin;
	private StatsAPIHandler statsApi;

	public enum statTypes {
		BLOCKS_BROKEN, BLOCKS_PLACED, TOTAL_BLOCKS_BROKEN, TOTAL_BLOCKS_PLACED, VOTES, PLAYERS_KILLED, MOBS_KILLED, DAMAGE_TAKEN, TIME_PLAYED, BLOCKS_MOVED
	};

	public StatsHandler(Autorank instance, StatsAPIHandler statsAPI) {
		this.plugin = instance;

		statsApi = statsAPI;
	}

	@Override
	public boolean isEnabled() {
		if (statsApi == null) {
			plugin.getLogger().info("Stats (by Lolmewn) api library was not found!");
			return false;
		}
		
		if (!statsApi.isAvailable()) {
			plugin.getLogger().info("Stats (by Lolmewn) is not enabled!"); 
			return false;
		}
		
		if (!statsApi.areBetaFunctionsEnabled()) {
			plugin.getLogger().info("Stats (by Lolmewn) does not have beta functions enabled!"); 
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

	@Override
	public int getNormalStat(String statType, Object... arguments) {
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

		String playerName = (String) arguments[0];
		String worldName = (String) arguments[1];

		int value = -1;

		if (correctName.equals("votes")) {
			// Handle voting
			value = statsApi.getNormalStat(playerName, "Votes", worldName);
		} else if (correctName.equals("players_killed")) {
			// Handle players killed
			value = statsApi.getTotalMobsKilled(playerName, "player", worldName);
		} else if (correctName.equals("mobs_killed")) {
			// Handle mobs killed
			// arg[2] == mobType
			value = statsApi.getTotalMobsKilled(playerName,
					(String) arguments[2], worldName);
		} else if (correctName.equals("damage_taken")) {
			// Handle damage taken
			value = statsApi.getNormalStat(playerName, "Damage taken", worldName);
		} else if (correctName.equals("blocks_placed")) {
			// Handle blocks placed
			value = statsApi.getBlocksStat(playerName,
					Integer.parseInt((String) arguments[2]),
					Integer.parseInt((String) arguments[3]), worldName,
					"Block place");
		} else if (correctName.equals("blocks_broken")) {
			// Handle blocks broken
			value = statsApi.getBlocksStat(playerName,
					Integer.parseInt((String) arguments[2]),
					Integer.parseInt((String) arguments[3]), worldName,
					"Block break");
		} else if (correctName.equals("total_blocks_placed")) {
			// Handle total blocks placed
			value = statsApi.getTotalBlocksPlaced(playerName, worldName);
		} else if (correctName.equals("total_blocks_broken")) {
			// Handle total blocks placed
			value = statsApi.getTotalBlocksBroken(playerName, worldName);
		} else if (correctName.equals("time_played")) {
			// Handle time played
			value = statsApi.getTotalPlayTime(playerName, worldName);
		} else if (correctName.equals("blocks_moved")) {
			// Handle time played
			value = statsApi.getTotalBlocksMoved(playerName,
					(Integer) arguments[2], worldName);
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
