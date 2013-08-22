package me.armar.plugins.autorank.debugger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.armar.plugins.autorank.Autorank;

public class Debugger {

	private Autorank plugin;
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private final static DateFormat humanDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Debugger(Autorank instance) {
		plugin = instance;
	}
	
	public String createDebugFile() {
		
		String dateFormatSave = dateFormat.format(new Date());
		// Creates a new file
		File txt = new File(plugin.getDataFolder() + "/debugger", "debug-" + dateFormatSave + ".txt");
		try {
			txt.getParentFile().mkdirs();
			txt.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateFormatSave;
		}

		//Create our writer
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(txt));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateFormatSave;
		}

		//write stuff
		try {
			out.write("This is a debug file of Autorank. You should give this to an author or ticket manager of Autorank.");
			out.newLine();
			out.write("You can go to http://pastebin.com/ and paste this file. Then, give the link and state the problems you're having in a ticket on the Autorank page.");
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("Date created: " + humanDateFormat.format(new Date()));
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("Autorank version: " + plugin.getDescription().getVersion());
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("Server implementation: " + plugin.getServer().getVersion());
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("Server version: " + plugin.getServer().getBukkitVersion());
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("Server warning state: " + plugin.getServer().getWarningState());
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("Ranks defined: ");
			out.newLine();
			out.write("");
			out.newLine();
			
			for (String change : plugin.getPlayerChecker().toStringArray()) {
				out.write(change);
				out.newLine();
			}
			
			out.write("");
			out.newLine();
			
			String usedConfig = (plugin.getConfigHandler().useAdvancedConfig() ? "AdvancedConfig.yml" : "SimpleConfig.yml");
			out.write("Config used: " + usedConfig);
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("Using MySQL: " + plugin.getConfigHandler().useMySQL());
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("Java version: " + System.getProperty("java.version"));
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("Operating system: " + System.getProperty("os.name"));
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("OS version: " + System.getProperty("os.version"));
			out.newLine();
			out.write("");
			out.newLine();
			
			out.write("OS architecture: " + System.getProperty("os.arch"));
			out.newLine();
			out.write("");
			out.newLine();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateFormatSave;
		}

		//close
		try {
			out.close();
			return dateFormatSave;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateFormatSave;
		}
	}
	
}
