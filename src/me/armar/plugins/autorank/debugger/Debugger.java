package me.armar.plugins.autorank.debugger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.armar.plugins.autorank.Autorank;

/**
 * This class is used to debug stuff when Autorank is running. <br>
 * Whenever a player uses the /ar debug command, it will create a new file with
 * all sorts of information. <br>
 * This information can be sent to the author so that he can easily see what's
 * wrong.
 * <p>
 * Date created: 21:00:29 15 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class Debugger {

	private final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private final static DateFormat humanDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final Autorank plugin;

	public Debugger(final Autorank instance) {
		plugin = instance;
	}

	/**
	 * Creates a debug file which is based on the current status of Autorank.
	 * <br>
	 * The file will be created in the folder 'debugger' and will have the name
	 * 'debug-yyyyMMddHHmmss'.
	 */
	public String createDebugFile() {

		final String dateFormatSave = dateFormat.format(new Date());
		// Creates a new file
		final File txt = new File(plugin.getDataFolder() + "/debugger", "debug-" + dateFormatSave + ".txt");
		try {
			txt.getParentFile().mkdirs();
			txt.createNewFile();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateFormatSave;
		}

		//Create our writer
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(txt));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateFormatSave;
		}

		//write stuff
		try {
			out.write(
					"This is a debug file of Autorank. You should give this to an author or ticket manager of Autorank.");
			out.newLine();
			out.write(
					"You can go to http://pastebin.com/ and paste this file. Then, give the link and state the problems you're having in a ticket on the Autorank page.");
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

			for (final String change : plugin.getPathManager().debugPaths()) {
				out.write(change);
				out.newLine();
			}

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

			out.write("Loaded addons: " + plugin.getAddonManager().getLoadedAddons().toString());
			out.newLine();
			out.write("");
			out.newLine();

		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				out.close();
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return dateFormatSave;
		}

		//close
		try {
			out.close();
			return dateFormatSave;
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateFormatSave;
		}
	}

}
