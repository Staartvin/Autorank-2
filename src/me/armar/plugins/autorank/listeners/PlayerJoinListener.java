package me.armar.plugins.autorank.listeners;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This listener will listen to players joining and send them a message when an update is available
 * 
 * @author Staartvin
 *
 */
public class PlayerJoinListener implements Listener {

	private Autorank plugin;
	
	public PlayerJoinListener(Autorank instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		
		// Player isn't allowed to see messages.
		if (!player.hasPermission("autorank.noticeonupdate")) return;
	
		System.out.print("Check for update");
		// No update was available
		if (!plugin.getUpdateHandler().isUpdateAvailable()) {
			System.out.print("No update!");
			return;
		}
		
		
		// Schedule it later so it will appear at the bottom
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				player.sendMessage(ChatColor.GREEN + plugin.getUpdateHandler().getUpdater().getLatestVersionString() + ChatColor.GOLD + " is now available for download!");
				//player.sendMessage(ChatColor.GOLD + "Type " + ChatColor.GREEN + "'/ar update'" + ChatColor.GOLD + " to update Autorank.");	
			}
			
		}, 10L);
		
	}
}
