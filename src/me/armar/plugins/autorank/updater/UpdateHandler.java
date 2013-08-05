package me.armar.plugins.autorank.updater;

import me.armar.plugins.autorank.Autorank;

public class UpdateHandler {

	private Autorank plugin;
	
	public UpdateHandler(Autorank instance) {
		plugin = instance;
	}
	
	private Updater updater;
	long latestCheck = 0;
	
	public void setUpdater(Updater updater) {
		// Store latest check time
		latestCheck = System.currentTimeMillis();
		this.updater = updater;
	}
	
	public Updater getUpdater() {
		return updater;
	}
	
	public boolean doCheckForNewVersion() {
		return plugin.getAdvancedConfig().getBoolean("auto-updater.check-for-new-versions");
	}
	
	public boolean isUpdateAvailable() {		
		// Latest check was more than 1 hour ago (Check again)
		if (((System.currentTimeMillis() - latestCheck) / 60000) >= 60) {
			// Check for new version
			return plugin.checkForUpdate();
		} else {
			// We checked less than an hour ago. (Recent enough)
			return (updater.getResult().equals(Updater.UpdateResult.UPDATE_AVAILABLE));
		}
	}
}
