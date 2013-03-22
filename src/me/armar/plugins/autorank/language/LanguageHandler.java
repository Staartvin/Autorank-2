package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;

/**
 * @author Vincent
 * Return the language being used.
 */
public class LanguageHandler {

	private Autorank autorank;
	private English english;
	private Dutch dutch;
	
	public LanguageHandler(Autorank autorank) {
		this.autorank = autorank;
		english = new English(autorank);
		dutch = new Dutch(autorank);
		
		autorank.getLogger().info("Languages files loaded: Using " + getLanguage().getLanguage());
	}
	
	public Language getLanguage() {
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
