package me.armar.plugins.autorank.warningmanager;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.permissions.AutorankPermission;
import org.bukkit.entity.Player;

public class WarningNoticeTask implements Runnable {

    private final Autorank plugin;

    public WarningNoticeTask(final Autorank instance) {
        plugin = instance;
    }

    @Override
    public void run() {
        // Get all players -> Check if they have a certain permission -> send
        // the most important warning

        // Show warnings in console!
        plugin.getWarningManager().sendWarnings(plugin.getServer().getConsoleSender());

        // Don't show warnings if they are turned off.
        if (!plugin.getSettingsConfig().showWarnings()) {
            return;
        }

        for (final Player p : plugin.getServer().getOnlinePlayers()) {

            // If player has notice on warning permission
            if (p.hasPermission(AutorankPermission.NOTICE_ON_WARNINGS) || p.isOp()) {

                plugin.getWarningManager().sendWarnings(p);
            }
        }

    }

}
