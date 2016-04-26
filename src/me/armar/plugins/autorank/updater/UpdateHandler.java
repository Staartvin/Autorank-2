package me.armar.plugins.autorank.updater;

import me.armar.plugins.autorank.Autorank;

public class UpdateHandler {

	long latestCheck = 0;

	private final Autorank plugin;

	private Updater updater;

	public UpdateHandler(final Autorank instance) {
		plugin = instance;
	}

	public boolean doCheckForNewVersion() {
		return plugin.getConfigHandler().doCheckForNewerVersion();
	}

	public Updater getUpdater() {
		return updater;
	}

	public boolean isUpdateAvailable() {

		// Do not check for updates when DEV version is used.
		if (plugin.isDevVersion())
			return false;

		// Latest check was more than 1 hour ago (Check again)
		if (((System.currentTimeMillis() - latestCheck) / 60000) >= 60) {
			// Check for new version
			return plugin.checkForUpdate();
		} else {
			// We checked less than an hour ago. (Recent enough)
			return (updater.getResult().equals(Updater.UpdateResult.UPDATE_AVAILABLE));
		}
	}

	public void setUpdater(final Updater updater) {
		// Store latest check time
		latestCheck = System.currentTimeMillis();
		this.updater = updater;
	}
}
