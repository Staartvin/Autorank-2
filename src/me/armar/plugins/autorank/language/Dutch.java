package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;

public class Dutch extends Language {

	private Autorank autorank;
	private String language = "Dutch";
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
	private String noNextRankup;
	private String meetsRequirements;
	private String rankedUpNow;
	private String doesntMeetRequirements;
	
	public Dutch(Autorank autorank) {
		setAutorank(autorank);
		playerNotOnline = "%player% is niet online!";
		noPermission = "Je hebt (%permission%) nodig om deze handeling uit te voeren!";
		cannotCheckConsole = "Kan niet de tijd bekijken voor de console.";
		playTimeChanged = "Je hebt de speeltijd van %player% veranderd naar %value%";
		invalidFormat = "Ongeldig formaat, gebruik %format%";
		autorankReloaded = "Autorank is herladen!";
		hasPlayedFor = " heeft gespeeld voor ";
		isIn = "zit in ";
		noGroups = "geen groep.";
		oneGroup = "groep ";
		multipleGroups = "de groepen ";
		noNextRankup = "en heeft geen volgende promotie.";
		meetsRequirements = "voldoet aan alle voorwaarden voor rang ";
		rankedUpNow = " en zal nu gepromoveerd worden.";
		doesntMeetRequirements = "en voldoet niet aan alle voorwaarden voor rang ";
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

	@Override
	public String getNoNextRankup() {
		// TODO Auto-generated method stub
		return noNextRankup;
	}

	@Override
	public String getMeetsRequirements() {
		// TODO Auto-generated method stub
		return meetsRequirements;
	}

	@Override
	public String getRankedUpNow() {
		// TODO Auto-generated method stub
		return rankedUpNow;
	}

	@Override
	public String getDoesntMeetRequirements() {
		// TODO Auto-generated method stub
		return doesntMeetRequirements;
	}
}
