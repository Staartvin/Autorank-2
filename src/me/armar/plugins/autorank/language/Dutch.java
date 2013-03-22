package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;

public class Dutch extends Language {

	private Autorank autorank;
	private String language = "Dutch";
	private String playerNotOnline;
	private String noPermission;
	private String cannotCheckConsole;
	private String playTimeChanged;
	
	
	public Dutch(Autorank autorank) {
		setAutorank(autorank);
		playerNotOnline = "%player% is niet online!";
		noPermission = "Je hebt (%permission%) nodig om deze handeling uit te voeren!";
		cannotCheckConsole = "Kan niet de tijd bekijken voor de console.";
		playTimeChanged = "Je hebt de speeltijd voor %player% veranderd naar %value%";
	}
	
	@Override
	public void setAutorank(Autorank autorank) {
		this.autorank = autorank;
		
	}

	@Override
	public Autorank getAutorank() {
		// TODO Auto-generated method stub
		return autorank;
	}

	@Override
	public String getPlayerNotOnline(String playerName) {
		// TODO Auto-generated method stub
		return playerNotOnline.replace("%player%", playerName);
	}

	@Override
	public String getNoPermission(String permission) {
		// TODO Auto-generated method stub
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
