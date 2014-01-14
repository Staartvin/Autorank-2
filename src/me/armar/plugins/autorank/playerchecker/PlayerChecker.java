package me.armar.plugins.autorank.playerchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.playerchecker.builders.RankChangeBuilder;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;

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

	private PlayerCheckerTrigger trigger;
	private final Map<String, List<RankChange>> rankChanges = new HashMap<String, List<RankChange>>();
	private RankChangeBuilder builder;
	private final Autorank plugin;

	public PlayerChecker(final Autorank plugin) {
		setTrigger(new PlayerCheckerTrigger(plugin, this));
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
	
	public String getNextRankup(Player player) {
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

	public PlayerCheckerTrigger getTrigger() {
		return trigger;
	}

	private void setTrigger(final PlayerCheckerTrigger trigger) {
		this.trigger = trigger;
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
