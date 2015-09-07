package me.armar.plugins.autorank.listeners;

import me.armar.plugins.autorank.Autorank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This listener will listen to players joining and send them a message when an
 * update is available or an error has been found
 * 
 * @author Staartvin
 * 
 */
public class PlayerJoinListener implements Listener {

	private final Autorank plugin;

	public PlayerJoinListener(final Autorank instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		// Add cached player
		//UUIDManager.addCachedPlayer(player);

		// Refresh uuid of the player if it is outdated
		if (plugin.getUUIDStorage().isOutdated(player.getName())) {
			plugin.getUUIDStorage().storeUUID(player.getName(),
					player.getUniqueId());
		}

		// Cannot check player at this moment. -> try at next automatic task
		if (plugin.getPlayerChecker() == null) {
			plugin.getLogger()
					.severe("Autorank lost its player checker, this is bad! Please report this to the developers!");
			return;
		}

		//plugin.debugMessage("PlayerChecker: " + plugin.getPlayerChecker());

		// Do leaderboard exemption check
		plugin.getPlayerChecker().doLeaderboardExemptCheck(player);

		// Perform check for player on login
		plugin.getPlayerChecker().checkPlayer(player);

		// Player isn't allowed to see messages.
		if (player.hasPermission("autorank.noticeonupdate")) {
			// No update was available
			if (plugin.getUpdateHandler().isUpdateAvailable()) {
				// Schedule it later so it will appear at the bottom
				plugin.getServer().getScheduler()
						.runTaskLater(plugin, new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								player.sendMessage(ChatColor.GREEN
										+ plugin.getUpdateHandler()
												.getUpdater().getLatestName()
										+ ChatColor.GOLD
										+ " is now available for download!");
								player.sendMessage(ChatColor.GREEN
										+ "Available at: "
										+ ChatColor.GOLD
										+ plugin.getUpdateHandler()
												.getUpdater()
												.getLatestFileLink());
								//player.sendMessage(ChatColor.GOLD + "Type " + ChatColor.GREEN + "'/ar update'" + ChatColor.GOLD + " to update Autorank.");	
							}

						}, 10L);
			}
		}

		// If player has notice on warning permission
		if (player.hasPermission("autorank.warning.notice")) {

			if (plugin.getWarningManager().getHighestWarning() != null) {

				// Schedule it later so it will appear at the bottom
				plugin.getServer().getScheduler()
						.runTaskLater(plugin, new Runnable() {

							@Override
							public void run() {
								player.sendMessage(ChatColor.BLUE
										+ "<AUTORANK> "
										+ ChatColor.RED
										+ "Warning: "
										+ ChatColor.GREEN
										+ plugin.getWarningManager()
												.getHighestWarning());
							}

						}, 10L);
			}
		}
	}
}
