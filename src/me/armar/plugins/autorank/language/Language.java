package me.armar.plugins.autorank.language;

import me.armar.plugins.autorank.Autorank;

/**
 * @author Vincent
 *         Abstract class that will be extended by language classes.
 * 
 */
/**
 * Language class only holds message methods. 
 * An enum {@link messages} holds all messages.  
 * 
 * @author Staartvin
 *
 */
public abstract class Language {

	// Messages
	public enum messages {
		playerNotOnline, noPermission, cannotCheckConsole, playTimeChanged, invalidFormat, autorankReloaded, hasPlayedFor, isIn, noGroups, oneGroup, multipleGroups, noNextRankup, meetsRequirements, rankedUpNow, doesntMeetRequirements, dataImported, timeRequirement, voteRequirement, moneyRequirement
	};

	public abstract void setAutorank(Autorank autorank);

	public abstract Autorank getAutorank();

	public abstract String getLanguage();

	public abstract String getPlayerNotOnline(String playerName);

	public abstract String getNoPermission(String permission);

	public abstract String getCannotCheckConsole();

	public abstract String getPlayTimeChanged(String playerName, int value);

	public abstract String getInvalidFormat(String format);

	public abstract String getAutorankReloaded();

	public abstract String getHasPlayedFor();

	public abstract String getIsIn();

	public abstract String getNoGroups();

	public abstract String getOneGroup();

	public abstract String getMultipleGroups();

	public abstract String getNoNextRankup();

	public abstract String getMeetsRequirements();

	public abstract String getRankedUpNow();

	public abstract String getDoesntMeetRequirements();

	public abstract String getDataImported();

	public abstract String getTimeRequirement(String time);

	public abstract String getWorldRequirement(String world);
	
	public abstract String getVoteRequirement(int votes);
	
	public abstract String getMoneyRequirement(int votes);
}
