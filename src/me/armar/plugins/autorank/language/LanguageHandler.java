package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;

/**
 * @author Vincent Return the language being used.
 */
public class LanguageHandler {

	private static Language language;

	public LanguageHandler(Autorank autorank) {
		String configLanguage = autorank.getAdvancedConfig().getString(
				"language", "english");

		if (configLanguage.equalsIgnoreCase("english")) {
			language = new English(autorank);
		} else if (configLanguage.equalsIgnoreCase("dutch")) {
			language = new Dutch(autorank);
		} else {
			language = new English(autorank);
		}

		autorank.getLogger().info(
				"Languages files loaded: Using " + getLanguage().getLanguage());
	}

	public static Language getLanguage() {
		return language;
	}
}
