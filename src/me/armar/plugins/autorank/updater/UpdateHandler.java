package me.armar.plugins.autorank.updater;

import me.armar.plugins.autorank.Autorank;

public class UpdateHandler {

    private final Autorank plugin;
    private long latestCheck = 0;
    private SpigotUpdater updater;

    private boolean lastResult;

    public UpdateHandler(final Autorank instance) {
        plugin = instance;
        updater = new SpigotUpdater(instance, 3239);
    }

    public boolean doCheckForNewVersion() {
        return plugin.getSettingsConfig().doCheckForNewerVersion();
    }

    public boolean isUpdateAvailable() {

        // Do not check for updates when DEV version is used.
        // Or when we should not check.
        if (plugin.isDevVersion() || !doCheckForNewVersion()) {
            return false;
        }


        // Latest check was more than 1 hour ago (Check again)
        if (((System.currentTimeMillis() - latestCheck) / 60000) >= 60) {
            // Check for new version
            return checkForUpdate();
        } else {
            // We checked less than an hour ago. (Recent enough)
            return lastResult;
        }
    }

    public SpigotUpdater getUpdater() {
        return updater;
    }

    public boolean checkForUpdate() {
        try {
            latestCheck = System.currentTimeMillis();
            lastResult = updater.checkForUpdates();
            return lastResult;
        } catch (Exception e) {
            //getLogger().warn("Could not check for updates! Stacktrace:");
            e.printStackTrace();
        }

        return false;
    }
}
