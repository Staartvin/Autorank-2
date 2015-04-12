package me.armar.plugins.autorank.warningmanager;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WarningNoticeTask extends BukkitRunnable {

    private final Autorank plugin;

    public WarningNoticeTask(final Autorank instance) {
        plugin = instance;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
		// Get all players -> Check if they have a certain permission -> send the most important warning

        // Don't show warnings if they are turned off.
        if (!plugin.getConfigHandler().showWarnings()) {
            return;
        }

        for (final Player p : plugin.getServer().getOnlinePlayers()) {
            // If player has notice on warning permission
            if (p.hasPermission("autorank.warning.notice")) {

                if (plugin.getWarningManager().getHighestWarning() != null) {

                    p.sendMessage(ChatColor.BLUE + "<AUTORANK> "
                            + ChatColor.RED + "Warning: " + ChatColor.GREEN
                            + plugin.getWarningManager().getHighestWarning());

                }
            }
        }

    }

}
