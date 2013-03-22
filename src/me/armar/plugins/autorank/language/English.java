package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;

/**
 * @author Vincent
 * English Language Class
 *
 */
public class English extends Language {
	
	private Autorank autorank;
	private String language = "English";
	private String playerNotOnline;
	private String noPermission;
	private String cannotCheckConsole;
	private String playTimeChanged; 
	
	public English(Autorank autorank) {
		setAutorank(autorank);
		playerNotOnline = "%player% is not online!";
		noPermission = "You need to have (%permission%) to do this!";
		cannotCheckConsole = "Cannot check for console.";
		playTimeChanged = "Changed playtime of %player% to %value%.";
	}
	
	@Override
	public String getPlayerNotOnline(String playerName) {
		// TODO Auto-generated method stub
		return playerNotOnline.replace("%player%", playerName);
	}

	@Override
	public void setAutorank(Autorank autorank) {
		this.autorank = autorank;
		
	}

	@Override
	public Autorank getAutorank() {
		return autorank;
	}

	@Override
	public String getNoPermission(String permission) {
		return noPermission.replace("%permission%", permission);
	}


	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		return language;
	}

	@Override
	public String getCannotCheckConsole() {
		// TODO Auto-generated method stub
		return cannotCheckConsole;
	}

	@Override
	public String getPlayTimeChanged(String playerName, int value) {
		// TODO Auto-generated method stub
		return playTimeChanged.replace("%player%", playerName).replace("%value%", value + "");
	}
}
