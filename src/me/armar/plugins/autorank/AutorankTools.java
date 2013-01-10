package me.armar.plugins.autorank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.CommandSender;

/*
 * AutorankTools is a bunch of static methods, I put sendColoredMessage 
 * there so that if I ever wanted to change the message formatting I can just do that here.
 *
 */

public class AutorankTools {
    
    public static int stringToMinutes(String string){
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
	res += stringtoDouble(days) * 60 *24;
		
	return res;
    }
    
    public static double stringtoDouble(String string) throws NumberFormatException{
	double res = 0;
	
	if(string != null)

	res = Double.parseDouble(string);
	
	return res;
    }
    
    public static int stringtoInt(String string) throws NumberFormatException{
	int res = 0;
	
	if(string != null)

	res = Integer.parseInt(string);
	
	return res;
    }
    
    public static void sendColoredMessage(CommandSender sender, String msg){
	sender.sendMessage("\u00A72" + msg);
    }
    
}
