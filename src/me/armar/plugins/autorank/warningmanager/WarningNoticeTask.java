package me.armar.plugins.autorank.warningmanager;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.Autorank;

public class WarningNoticeTask implements Runnable {

    private final Autorank plugin;

    public WarningNoticeTask(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public void run() {
        // Get all players -> Check if they have a certain permission -> send
        // the most important warning
        
        // Don't show warnings if they are turned off.
        if (!plugin.getConfigHandler().showWarnings()) {
            return;
        }

        for (final Player p : plugin.getServer().getOnlinePlayers()) {

            // If player has notice on warning permission
            if (p.hasPermission("autorank.warning.notice") || p.isOp()) {

               plugin.getWarningManager().sendWarnings(p);
            }
        }

    }

}
