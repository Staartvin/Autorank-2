package me.armar.plugins.autorank.validations;

import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

import org.bukkit.configuration.ConfigurationSection;

public class PermissionGroupValidation {

	private Autorank autorank;
	
	public PermissionGroupValidation(Autorank instance) {
		this.autorank = instance;
	}
	
	public boolean validateGroups(SimpleYamlConfiguration config) {
		
		if (config == null) return false;
		
		boolean isMissing = false;
		String[] groups = autorank.getPermPlugHandler().getPermissionPlugin().getGroups();
		Set<String> ranks;
		
		// Check for advanced config.
		if (config.getBoolean("use advanced config")) { 
		
		ConfigurationSection section = config.getConfigurationSection("ranks");
		ranks = section.getKeys(false);
		
		} // Check for simple config
		else {
			ranks = config.getKeys(false);
		}
		
		for (String rank:ranks) {
			for (int i=0;i<groups.length;i++) {
				String group = groups[i];
				if (rank.equals(group)) break;
				
				if (rank.equalsIgnoreCase(group)) {
					// Do not log but register warning
					autorank.getWarningManager().registerWarning("Permissions group '" + rank + "' should be '" + group + "'", 10);
					isMissing = true;
					break;
				}
				// If this is the last group and is not equal to the rank defined in the config:
				if ((i == (groups.length - 1)) && !rank.equals(group)) {
					autorank.getWarningManager().registerWarning("Permissions group is not defined: " + rank, 10);
					isMissing = true;
				}
			}
			
			if (!isValidChange(rank)) {
				autorank.getWarningManager().registerWarning("Rank change of rank '" + rank + "' is invalid. (Do the groups used exist?)", 10);
				isMissing = true;
			}
		}
		
		// If all is okay, then do nothing. Else, disable AR.
		return (!isMissing);
	}
	
	/**
	 * Checks whether the @group variable is the same as rankFrom group.
	 * It also checks whether the rankTo group is defined as a group in the permission plugin
	 * @param group
	 * @return true if (rankFrom.equals(group) && rankTo is defined in the config); false otherwise
	 */
	public boolean isValidChange(String group) {
		String rankChange = autorank.getAdvancedConfig().getString("ranks." + group + ".results.rank change", null);
		String[] groups = autorank.getPermPlugHandler().getPermissionPlugin().getGroups();
		
		if (rankChange == null) return true;
		
		if (rankChange.trim().equals("")) return false;
		
		if (!rankChange.contains(";")) {
			boolean isMissing = true;
			
			for (String group1: groups) {
				if (group1.equals(rankChange.trim())) {
					isMissing = false;
				}
			}
			
			return !isMissing;
		}
		
		String[] array = rankChange.split(";");
		
		String rankFrom = null, rankTo = null;
		
		if (array.length >= 2) {
			rankFrom = array[0].trim();
			rankTo = array[1].trim();
		}

		if (rankTo == null || rankFrom == null) return false;
		
		boolean isMissingRankTo = true, isMissingRankFrom = true;
		
		// Check whether the rankTo exists
		for (String group1: groups) {
			if (group1.equals(rankTo.trim())) {
				isMissingRankTo = false;
			}
		}
		
		// Check whether the rankFrom exists
		for (String group1: groups) {
			if (group1.equals(rankFrom.trim())) {
				isMissingRankFrom = false;
			}
		}
		
		return (rankFrom.equals(group) && !isMissingRankTo && !isMissingRankFrom);
	}
}
