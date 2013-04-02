package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;

/**
 * @author Vincent
 * Return the language being used.
 */
public class LanguageHandler {

	private static Autorank autorank;
	private static English english;
	private static Dutch dutch;
	
	public LanguageHandler(Autorank autorank) {
		LanguageHandler.autorank = autorank;
		english = new English(autorank);
		dutch = new Dutch(autorank);
		
		autorank.getLogger().info("Languages files loaded: Using " + getLanguage().getLanguage());
	}
	
	public static Language getLanguage() {
		String language = autorank.getAdvancedConfig().getString("language", "english");
		
		if (language.equalsIgnoreCase("english")) {
			return english;
		}
		else if (language.equalsIgnoreCase("dutch")) {
			return dutch;
		} else {
			return english;
		}
	}
}
