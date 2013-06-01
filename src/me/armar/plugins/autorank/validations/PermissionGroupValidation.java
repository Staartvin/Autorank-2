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
					autorank.getLogger().severe("Permissions group '" + rank + "' should be '" + group + "'");
					isMissing = true;
					break;
				}
				// If this is the last group and is not equal to the rank defined in the config:
				if ((i == (groups.length - 1)) && !rank.equals(group)) {
					autorank.getLogger().severe("Permissions group is not defined: " + rank);
					isMissing = true;
				}
			}
		}
		
		if (isMissing == false) return true;
		else return false;
	}
}
