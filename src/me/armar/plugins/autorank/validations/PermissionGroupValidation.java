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
		
		ConfigurationSection section = config.getConfigurationSection("ranks");
		
		Set<String> ranks = section.getKeys(false);
		boolean isMissing = false;
		String[] groups = autorank.getPermPlugHandler().getGroups();
		
		for (String rank:ranks) {
			for (int i=0;i<groups.length;i++) {
				String group = groups[i];
				if (rank.equals(group)) break;
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
