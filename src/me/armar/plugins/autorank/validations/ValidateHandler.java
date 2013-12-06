package me.armar.plugins.autorank.validations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

public class ValidateHandler {

	private Autorank autorank;
	private PermissionGroupValidation permGroupValidate;
	private StatsRequirementValidation statsValidate;

	public ValidateHandler(Autorank instance) {
		this.autorank = instance;
		permGroupValidate = new PermissionGroupValidation(instance);
		statsValidate = new StatsRequirementValidation(instance);
	}

	public boolean validateConfigGroups(SimpleYamlConfiguration config) {

		boolean correctSetup = false;
		
		// Simple logic to find out what config to check for.
		if (autorank.getConfigHandler().useAdvancedConfig()) {
			correctSetup = permGroupValidate.validateAdvancedGroups(config);
		} else {
			correctSetup = permGroupValidate.validateSimpleGroups(config);
		}
		
		if (!correctSetup) {
			autorank.getLogger().severe(
					"There are invalid groups defined in the config!");
			autorank.getLogger().severe("Check your config!");
			return false;
		} else {
			autorank.getLogger().info(
					"Config files have been correctly setup!");
		}
		
		// Check for Stats required requirements
		if (!statsValidate.validateRequirements(config)) {
			return false;
		}
		
		return true;
	}
}
