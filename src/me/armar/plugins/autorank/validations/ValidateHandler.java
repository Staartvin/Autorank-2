package me.armar.plugins.autorank.validations;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

public class ValidateHandler {

	private Autorank autorank;
	private PermissionGroupValidation permGroupValidate;

	public ValidateHandler(Autorank instance) {
		this.autorank = instance;
		permGroupValidate = new PermissionGroupValidation(instance);
	}

	public boolean validateConfigGroups(SimpleYamlConfiguration config) {

		if (permGroupValidate.validateGroups(config) == false) {
			autorank.getLogger().severe(
					"There are invalid groups defined in the config!");
			autorank.getLogger().severe("Check your config!");
			return false;
		} else {
			autorank.getLogger().info(
					"Config files have been correctly setup!");
			return true;
		}
	}
}
