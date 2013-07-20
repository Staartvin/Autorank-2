package me.armar.plugins.autorank.playerchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.armar.plugins.autorank.Autorank;
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
	private Map<String, List<RankChange>> rankChanges = new HashMap<String, List<RankChange>>();
	private RankChangeBuilder builder;
	private Autorank plugin;

	public PlayerChecker(Autorank plugin) {
		setTrigger(new PlayerCheckerTrigger(plugin, this));
		setBuilder(new RankChangeBuilder(plugin));
		this.plugin = plugin;
	}

	public void initialiseFromConfigs(Autorank plugin) {
		SimpleYamlConfiguration simpleConfig = plugin.getSimpleConfig();
		SimpleYamlConfiguration advancedConfig = plugin.getAdvancedConfig();

		List<RankChange> ranks;
		if ((Boolean) advancedConfig.get("use advanced config")) {
			ranks = builder.createFromAdvancedConfig(advancedConfig);
		} else {
			ranks = builder.createFromSimpleConfig(simpleConfig);
		}

		for (RankChange rank : ranks) {
			addRankChange(rank.getRank(), rank);
		}

	}

	public void addRankChange(String name, RankChange change) {
		if (rankChanges.get(name) == null) {
			List<RankChange> list = new ArrayList<RankChange>();
			list.add(change);
			rankChanges.put(name, list);
		} else {
			rankChanges.get(name).add(change);
		}
	}

	public boolean checkPlayer(Player player) {
		boolean result = false;
		
		// Do not rank a player when he is excluded
		if (player.hasPermission("autorank.exclude")) {
			System.out.print(player.getName() + " is excluded!");
			return result;
		}

		String[] groups = plugin.getPermPlugHandler().getPermissionPlugin().getPlayerGroups(player);

		for (String group : groups) {
			List<RankChange> changes = rankChanges.get(group);
			if (changes != null) {
				for (RankChange change : changes) {
					if (change.applyChange(player, group)) {
						result = true;
					}
				}
			}
		}

		return result;
	}

	public Map<RankChange, List<Requirement>> getFailedRequirementsForApplicableGroup(
			Player player) {
		Map<RankChange, List<Requirement>> result = new HashMap<RankChange, List<Requirement>>();

		String[] groups = plugin.getPermPlugHandler().getPermissionPlugin().getPlayerGroups(player);

		for (String group : groups) {
			List<RankChange> changes = rankChanges.get(group);
			if (changes != null) {
				for (RankChange change : changes) {
					result.put(change, change.getFailedRequirements(player));
				}
			}
		}

		return result;
	}

	public PlayerCheckerTrigger getTrigger() {
		return trigger;
	}

	private void setTrigger(PlayerCheckerTrigger trigger) {
		this.trigger = trigger;
	}

	public RankChangeBuilder getBuilder() {
		return builder;
	}

	private void setBuilder(RankChangeBuilder builder) {
		this.builder = builder;
	}

	public String[] toStringArray() {
		List<String> list = new ArrayList<String>();

		for (String name : rankChanges.keySet()) {

			List<RankChange> changes = rankChanges.get(name);
			for (RankChange change : changes) {

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
