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
	private String invalidFormat;
	private String autorankReloaded;
	private String hasPlayedFor;
	private String isIn;
	private String noGroups;
	private String oneGroup;
	private String multipleGroups;
	
	public English(Autorank autorank) {
		setAutorank(autorank);
		playerNotOnline = "%player% is not online!";
		noPermission = "You need to have (%permission%) to do this!";
		cannotCheckConsole = "Cannot check for console.";
		playTimeChanged = "Changed playtime of %player% to %value%.";
		invalidFormat = "Invalid format, use %format%";
		autorankReloaded = "Reloaded Autorank!";
		hasPlayedFor = " has played for ";
		isIn = "is in ";
		noGroups = "no groups.";
		oneGroup = "group ";
		multipleGroups = "groups ";
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

	@Override
	public String getInvalidFormat(String format) {
		// TODO Auto-generated method stub
		return invalidFormat.replace("%format%", format);
	}

	@Override
	public String getAutorankReloaded() {
		// TODO Auto-generated method stub
		return autorankReloaded;
	}

	@Override
	public String getHasPlayedFor() {
		// TODO Auto-generated method stub
		return hasPlayedFor;
	}

	@Override
	public String getIsIn() {
		// TODO Auto-generated method stub
		return isIn;
	}

	@Override
	public String getNoGroups() {
		// TODO Auto-generated method stub
		return noGroups;
	}

	@Override
	public String getOneGroup() {
		// TODO Auto-generated method stub
		return oneGroup;
	}

	@Override
	public String getMultipleGroups() {
		// TODO Auto-generated method stub
		return multipleGroups;
	}
}
