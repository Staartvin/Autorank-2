package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;

/**
 * @author Vincent
 * Abstract class that will be extended by language classes.
 *
 */
public abstract class Language {

	// Messages
	public enum messages {playerNotOnline, noPermission, cannotCheckConsole};
	
	public abstract void setAutorank(Autorank autorank);

	public abstract Autorank getAutorank();
	
	public abstract String getLanguage();
	
	public abstract String getPlayerNotOnline(String playerName);
	
	public abstract String getNoPermission(String permission);
	
	public abstract String getCannotCheckConsole();
	
	public abstract String getPlayTimeChanged(String playerName, int value);
}
