package me.armar.plugins.autorank.playerchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.playerchecker.builders.RankChangeBuilder;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

/*
 * PlayerChecker is where the magic happens :P It has a RankChangeBuilder that reads 
 * the config and makes new RankChange objects. It sends the names of the needed results 
 * and requirements to AdditionalRequirementBuilder and ResultBuilder. Those are dynamic 
 * factories because they don't have any hardcoded classes to build. You register all 
 * the requirements or results when the plugin is started. Because of this other 
 * plugins / addons can register their own custom requirements and results very easily.
 * 
 * So: PlayerChecker has a list of RankChanges and a RankChange has a list of AdditionalRequirement and Results.
 * 
 */
public class PlayerChecker {

	private final Map<String, List<RankChange>> rankChanges = new HashMap<String, List<RankChange>>();
	private RankChangeBuilder builder;
	private final Autorank plugin;

	public PlayerChecker(final Autorank plugin) {
		setBuilder(new RankChangeBuilder(plugin));
		this.plugin = plugin;
	}

	public void initialiseFromConfigs(final Autorank plugin) {
		final SimpleYamlConfiguration simpleConfig = plugin.getSimpleConfig();
		final SimpleYamlConfiguration advancedConfig = plugin
				.getAdvancedConfig();

		List<RankChange> ranks;
		if ((Boolean) advancedConfig.get("use advanced config")) {
			ranks = builder.createFromAdvancedConfig(advancedConfig);
		} else {
			ranks = builder.createFromSimpleConfig(simpleConfig);
		}

		for (final RankChange rank : ranks) {
			addRankChange(rank.getRank(), rank);
		}

	}

	public void addRankChange(final String name, final RankChange change) {
		if (rankChanges.get(name) == null) {
			final List<RankChange> list = new ArrayList<RankChange>();
			list.add(change);
			rankChanges.put(name, list);
		} else {
			rankChanges.get(name).add(change);
		}
	}

	public boolean checkPlayer(final Player player) {

		boolean result = false;

		// Do not rank a player when he is excluded
		if (AutorankTools.isExcluded(player))
			return result;

		final String[] groups = plugin.getPermPlugHandler()
				.getPermissionPlugin().getPlayerGroups(player);

		for (final String group : groups) {
			final List<RankChange> changes = rankChanges.get(group);
			if (changes != null) {
				for (final RankChange change : changes) {
					if (change.applyChange(player, group)) {
						result = true;
					}
				}
			}
		}

		return result;
	}

	public Map<RankChange, List<Requirement>> getFailedRequirements(
			final Player player) {
		final Map<RankChange, List<Requirement>> result = new HashMap<RankChange, List<Requirement>>();

		final String[] groups = plugin.getPermPlugHandler()
				.getPermissionPlugin().getPlayerGroups(player);

		for (final String group : groups) {
			final List<RankChange> changes = rankChanges.get(group);
			if (changes != null) {
				for (final RankChange change : changes) {
					result.put(change, change.getFailedRequirements(player));
				}
			}
		}

		return result;
	}

	public Map<RankChange, List<Requirement>> getAllRequirements(
			final Player player) {
		final Map<RankChange, List<Requirement>> result = new HashMap<RankChange, List<Requirement>>();

		final String[] groups = plugin.getPermPlugHandler()
				.getPermissionPlugin().getPlayerGroups(player);

		for (final String group : groups) {
			final List<RankChange> changes = rankChanges.get(group);
			if (changes != null) {
				for (final RankChange change : changes) {
					result.put(change, change.getReq());
				}
			}
		}

		return result;
	}
	
	/**
	 * Get the next rank up permission group
	 * @param player Player to check for
	 * @return name of the permission group the player will be ranked to; null if no rank up
	 */
	public String getNextRankupGroup(Player player) {
		Map<RankChange, List<Requirement>> requirements = getAllRequirements(player);
		
		Set<RankChange> rankchanges = requirements.keySet();
		
		if (rankchanges.size() == 0) {
			return null;
		}
		
		String rankName = null;
		
		for (RankChange rank: rankchanges) {
			rankName = rank.getRankTo();
		}
		
		return rankName;
	}
	
	/**
	 * Get the next {@link RankChange} class for the player
	 * @param player Player to check for.
	 * @return the {@link RankChange} class that is associated with this player's rankup; null if no rank up
	 */
	public RankChange getNextRank(Player player) {
		Map<RankChange, List<Requirement>> requirements = getAllRequirements(player);
		
		Set<RankChange> rankchanges = requirements.keySet();
		
		if (rankchanges.size() == 0) {
			return null;
		}
		
		RankChange newRank = null;
		
		for (RankChange rank: rankchanges) {
			newRank = rank;
		}
		
		return newRank;
	}
	
	/**
	 * Get all requirements for a player to rank up.
	 * All requirements are included, even if they are completed.
	 * @param player Player to get the requirements for
	 * @return a list of requirements; null if the player has no rank up.
	 */
	public List<Requirement> getRequirementsForNextRank(Player player) {
		RankChange rank = getNextRank(player);
		
		if (rank == null) {
			return null;
		}
		
		return getAllRequirements(player).get(rank);
	}

	public RankChangeBuilder getBuilder() {
		return builder;
	}

	private void setBuilder(final RankChangeBuilder builder) {
		this.builder = builder;
	}

	public String[] toStringArray() {
		final List<String> list = new ArrayList<String>();

		for (final String name : rankChanges.keySet()) {

			final List<RankChange> changes = rankChanges.get(name);
			for (final RankChange change : changes) {

				if (change == null) {
					list.add("- NULL");
				} else {
					list.add("- " + change.toString());
				}
			}
		}

		return list.toArray(new String[] {});
	}

}
