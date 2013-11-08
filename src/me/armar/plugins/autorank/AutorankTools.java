package me.armar.plugins.autorank;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.armar.plugins.autorank.language.Lang;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * AutorankTools is a bunch of static methods, I put sendColoredMessage 
 * there so that if I ever wanted to change the message formatting I can just do that here.
 *
 */

public class AutorankTools {
	
	static List<String> reqTypes = new ArrayList<String>();

	public static int stringToMinutes(String string) {
		int res = 0;

		string = string.trim();

		Pattern pattern = Pattern.compile("((\\d+)d)?((\\d+)h)?((\\d+)m)?");
		Matcher matcher = pattern.matcher(string);

		matcher.find();
		String days = matcher.group(2);
		String hours = matcher.group(4);
		String minutes = matcher.group(6);

		res += stringtoDouble(minutes);
		res += stringtoDouble(hours) * 60;
		res += stringtoDouble(days) * 60 * 24;

		return res;
	}

	public static String minutesToString(int minutes) {
		StringBuilder b = new StringBuilder();

		int days = minutes / 1440;
		minutes -= days * 1440;
		int hours = minutes / 60;
		minutes -= hours * 60;

		if (days != 0) {
			b.append(days);
			b.append(" ");
			if (days != 1)
				b.append(Lang.DAY_PLURAL.getConfigValue(null));
			else
				b.append(Lang.DAY_SINGULAR.getConfigValue(null));
			
			if (hours != 0 || minutes != 0)
				b.append(" ");
		}

		if (hours != 0) {
			b.append(hours);
			b.append(" ");
			if (hours != 1)
				b.append(Lang.HOUR_PLURAL.getConfigValue(null));
			else
				b.append(Lang.HOUR_SINGULAR.getConfigValue(null));
			
			if (minutes != 0)
				b.append(" ");
		}

		if (minutes != 0 || (hours == 0 && days == 0)) {
			b.append(minutes);
			b.append(" ");
			if (minutes != 1)
				b.append(Lang.MINUTE_PLURAL.getConfigValue(null));
			else
				b.append(Lang.MINUTE_SINGULAR.getConfigValue(null));
		}

		return b.toString();
	}

	public static double stringtoDouble(String string)
			throws NumberFormatException {
		double res = 0;

		if (string != null)

			res = Double.parseDouble(string);

		return res;
	}

	public static int stringtoInt(String string) throws NumberFormatException {
		int res = 0;

		if (string != null)

			res = Integer.parseInt(string);

		return res;
	}

	public static void sendColoredMessage(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + msg));
	}
	
	/**
	 * Elaborate method to check whether a player is excluded from ranking.
	 * 
	 * When a player has a wildcard permission but is an OP, it will return false;
	 * When a player has a wildcard permission but is not an OP, it will return true;
	 * When a player only has autorank.exclude, it will return true;
	 * 
	 * @param player Player to check for
	 * @return whether a player is excluded from ranking or not.
	 */
	public static boolean isExcluded(Player player) {
		if (player.hasPermission("autorank.askdjaslkdj")) {
			// Op's have all permissions, but if he is a OP, he isn't excluded
			if (player.isOp()) {
				return false;
			}
			
			// Player uses wildcard permission, so excluded
			return true;
		}
		
		if (player.hasPermission("autorank.exclude")) {
			return true;
		}
		
		return false;	
	}
	
	/** 
	 * This will return the correct type of the requirement.
	 * As players might want to use multiple requirements of the same type, they only have to specify the name of it with a unique identifier.
	 * E.g. time1, time2 or exp1, exp2, etc.
	 * @param oldName Name of the requirement to search for.
	 * @return correct requirement name or old name if none was found.
	 */
	public static String getCorrectName(String oldName) {
		
		for (String type: reqTypes) {
			if (oldName.contains(type)) {
				return type;
			}
		}
		
		return oldName;
	}
	
	public static void registerRequirement(String type) {
		if (!reqTypes.contains(type)) {
			reqTypes.add(type);
		}
	}

}
