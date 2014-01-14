package me.armar.plugins.autorank.statsmanager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.statsapi.StatsAPIHandler;
import me.armar.plugins.autorank.statsmanager.handlers.DummyHandler;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import nl.lolmewn.stats.Main;

import org.bukkit.plugin.Plugin;


/**
 * This class decides which Stats plugin will be used for getting stat data.
 * @author Staartvin
 *
 */
public class StatsPluginManager {

	private Autorank plugin;
	
	public StatsPluginManager(Autorank instance) {
		plugin = instance;
		
		searchStatsPlugin();
	}
	
	private StatsPlugin statsPlugin;
	
	private void searchStatsPlugin() {
		if (findStats()) {
			// use Stats
			StatsAPIHandler api = new StatsAPIHandler(plugin);
			
			// Connect to Stats
			api.setupStatsAPI();
			
			statsPlugin = new StatsHandler(plugin, api);	
		} else {
			// use Vault
			// Use dummy handler if no stats plugin was found
			statsPlugin = new DummyHandler();
			
			// Do not register warning as that will show every few seconds.
			// Print image on console instead.
			//plugin.getWarningManager().registerWarning("Autorank did not find a stats plugin! Most requirements cannot be used!", 5);
			
			plugin.getLogger().severe("Autorank did not find a stats plugin! Most requirements cannot be used!");
			
		}
	}
	
	public boolean findStats() {
		Plugin x = plugin.getServer().getPluginManager()
				.getPlugin("Stats");
		// Main == Stats main class
		if (x != null & x instanceof Main) {
			return true;
		}
		
		return false;
	}
	
	public StatsPlugin getStatsPlugin() {
		return statsPlugin;
	}
}
